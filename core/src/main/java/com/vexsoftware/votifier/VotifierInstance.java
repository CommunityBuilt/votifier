package com.vexsoftware.votifier;

import com.vexsoftware.votifier.model.VoteListener;

import java.security.KeyPair;
import java.util.List;
import java.util.logging.Logger;

public interface VotifierInstance {
    public String getVersion();

    public KeyPair getKeyPair();

    public boolean isDebug();

    public List<VoteListener> getListeners();

    public Logger getLog();
}
