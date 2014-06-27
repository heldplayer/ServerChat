
package me.heldplayer.chat.framework;

import java.util.UUID;

import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;

public class RemoteServer extends Server {

    private final UUID uuid;

    public RemoteServer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public ChatPacket createPacket(String id) {
        return ConnectionState.CONNECTED.createPacket(id);
    }

    @Override
    public String getId(ChatPacket packet) {
        return ConnectionState.CONNECTED.getPacketName(packet.getClass());
    }

}
