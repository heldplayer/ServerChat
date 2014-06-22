
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Sent when a server disconnects, indicates that an attempt to reconnect should
 * not be made
 */
public class PacketDisconnect extends ChatPacket {

    private String reason;

    public PacketDisconnect(String reason) {
        this.reason = reason;
    }

    public PacketDisconnect() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte[] reasonBytes = this.reason.getBytes();
        out.write(reasonBytes.length);
        out.write(reasonBytes);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte[] reasonBytes = new byte[in.readInt()];
        in.readFully(reasonBytes);
        this.reason = new String(reasonBytes);
    }

    @Override
    public void onPacket(ServerConnection connection) {
        connection.connectionsList.removeConnection(connection);
    }

}
