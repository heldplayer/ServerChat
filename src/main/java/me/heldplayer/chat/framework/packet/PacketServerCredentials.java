
package me.heldplayer.chat.framework.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.ServerConnection;

/**
 * Sent when a server disconnects, indicates that an attempt to reconnect should
 * not be made
 */
public class PacketServerCredentials extends ChatPacket {

    private String host;
    private int port;

    public PacketServerCredentials(String reason) {
        this.host = reason;
    }

    public PacketServerCredentials() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        byte[] reasonBytes = this.host.getBytes();
        out.write(reasonBytes.length);
        out.write(reasonBytes);

        out.writeInt(this.port);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte[] reasonBytes = new byte[in.readInt()];
        in.readFully(reasonBytes);
        this.host = new String(reasonBytes);

        this.port = in.readInt();
    }

    @Override
    public void onPacket(ServerConnection connection) {
        // TODO: store server credentials or retrieve them
    }

}
