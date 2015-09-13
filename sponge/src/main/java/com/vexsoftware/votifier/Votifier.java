package com.vexsoftware.votifier;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.vexsoftware.votifier.config.Config;
import com.vexsoftware.votifier.config.JsonConfig;
import com.vexsoftware.votifier.crypto.RSAIO;
import com.vexsoftware.votifier.crypto.RSAKeygen;
import com.vexsoftware.votifier.model.ListenerLoader;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;
import com.vexsoftware.votifier.model.VotifierEvent;
import com.vexsoftware.votifier.net.VoteReceiver;
import lombok.Getter;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;

import java.io.File;
import java.security.KeyPair;
import java.util.List;
import java.util.function.Consumer;

@Plugin(id = "Votifier", name = "Votifier", version = "1.9")
public class Votifier implements VotifierInstance {
    @Getter
    private static Votifier instance;

    @Inject
    @Getter
    private Game game;

    @Inject
    @Getter
    private PluginContainer container;
    @Inject
    @Getter
    private Logger logger;
    @Inject
    @Getter
    private java.util.logging.Logger javaLogger;
    @Inject
    @ConfigDir(sharedRoot = false)
    @Getter
    private File configDir;

    @Getter
    private Config config;
    private KeyPair keyPair;
    private final List<VoteListener> listeners = Lists.newArrayList();
    private VoteReceiver receiver;

    public Votifier() {
        instance = this;
    }

    @Listener
    public void initialization(GameInitializationEvent event) {
        initConfig();

        if (!initRSA()) {
            return;
        }

        initListeners();

        try {
            initReceiver();
        } catch (Exception e) {
            logger.warn("Failed to initialize the vote receiver.", e);
        }
    }

    private void initConfig() {
        File configFile = new File(configDir, "config.json");
        config = JsonConfig.load(configFile, Config.class);

        if (!configFile.exists()) {
            config.save(configFile);
        }
    }

    private boolean initRSA() {
        File rsaDir = new File(configDir, "rsa/");

        try {
            if (!rsaDir.exists()) {
                rsaDir.mkdir();
                keyPair = RSAKeygen.generate(2048);
                RSAIO.save(rsaDir, keyPair);
            } else {
                keyPair = RSAIO.load(rsaDir);
            }
        } catch (Exception ex) {
            logger.warn("Error reading configuration file or RSA keys", ex);
            return false;
        }

        return true;
    }

    private void initListeners() {
        File listenerDir = new File(configDir, config.getListenerFolder());

        if (!listenerDir.exists()) {
            listenerDir.mkdirs();
        }

        listeners.addAll(ListenerLoader.load(listenerDir));
    }

    private void initReceiver() throws Exception {
        receiver = new VoteReceiver(this, config.getHost(), config.getPort(), new Consumer<Vote>() {
            @Override
            public void accept(final Vote vote) {
                Votifier.this.game.getScheduler().createTaskBuilder()
                        .execute(new Runnable() {
                            public void run() {
                                Votifier.this.game.getEventManager().post(new VotifierEvent(vote));
                            }
                        })
                        .submit(Votifier.this);
            }
        });
        receiver.start();
    }

    @Override
    public String getVersion() {
        return container.getVersion();
    }

    @Override
    public KeyPair getKeyPair() {
        return keyPair;
    }

    @Override
    public boolean isDebug() {
        return config.isDebug();
    }

    @Override
    public List<VoteListener> getListeners() {
        return listeners;
    }

    @Override
    public java.util.logging.Logger getLog() {
        return javaLogger;
    }
}
