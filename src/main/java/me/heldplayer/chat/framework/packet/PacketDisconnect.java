
package me.heldplayer.chat.framework.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;

/**
 * Sent when a server disconnects, indicates that an attempt to reconnect should
 * not be made
 */
public class PacketDisconnect extends ChatPacket {

    private String reason;
    private boolean kicked;

    public PacketDisconnect(String reason, boolean kicked) {
        this.reason = reason;
    }

    public PacketDisconnect() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(this.kicked);
        out.writeUTF(this.reason);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.kicked = in.readBoolean();
        this.reason = in.readUTF();
    }

    @Override
    public void onPacket(LocalServer connection) {
        connection.log.info("Server disconnected: %s", this.reason);
        connection.disconnectServer(null);
        if (this.kicked) {
            connection.connectionsList.removeServer(connection.getUuid());
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
