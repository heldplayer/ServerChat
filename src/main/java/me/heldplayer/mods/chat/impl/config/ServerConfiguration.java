
package me.heldplayer.mods.chat.impl.config;

import java.security.KeyPair;
import java.util.UUID;

import me.heldplayer.chat.framework.config.ServerEntry;

import com.google.gson.annotations.SerializedName;

public class ServerConfiguration {

    @SerializedName("uuid")
    UUID uuid;
    @SerializedName("server-entries")
    ServerEntry[] serverEntries;
    @SerializedName("host")
    String host;
    @SerializedName("port")
    int port;
    @SerializedName("key-pair")
    KeyPair keyPair;

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ServerEntry[] getServerEntries() {
        return this.serverEntries;
    }

    public void setServerEntries(ServerEntry[] serverEntries) {
        this.serverEntries = serverEntries;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 0 and 65535");
        }
        this.port = port;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

}
