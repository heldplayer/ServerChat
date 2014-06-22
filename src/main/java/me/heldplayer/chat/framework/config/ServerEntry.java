
package me.heldplayer.chat.framework.config;

import java.util.UUID;

public final class ServerEntry {

    private String ip;
    private int port;
    private UUID uuid;

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

}
