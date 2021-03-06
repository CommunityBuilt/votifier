package com.vexsoftware.votifier.sponge.config;

import com.google.gson.annotations.SerializedName;
import com.vexsoftware.votifier.config.JsonConfig;
import lombok.Getter;

public class Config extends JsonConfig {
    @Getter
    private String host = "0.0.0.0";
    @Getter
    private int port = 8192;
    @Getter
    private boolean debug = false;
    @SerializedName(value = "listener_folder")
    @Getter
    private String listenerFolder = "/listeners/";
}
