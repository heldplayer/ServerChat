
package me.heldplayer.chat.framework;

import java.util.UUID;

import me.heldplayer.chat.framework.packet.ChatPacket;

public abstract class Server {

    public abstract UUID getUuid();

    public abstract ChatPacket createPacket(String id);

    public abstract String getId(ChatPacket packet);

}
