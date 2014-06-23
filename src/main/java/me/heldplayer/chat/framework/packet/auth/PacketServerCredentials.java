
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;

public class PacketServerCredentials extends ChatPacket {

    private UUID uuid;
    private String host;
    private int port;

    public PacketServerCredentials(UUID uuid, String host, int port) {
        this.uuid = uuid;
        this.host = host;
        this.port = port;
    }

    public PacketServerCredentials() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        String uuid = this.uuid.toString();
        byte[] uuidBytes = uuid.getBytes();
        out.writeInt(uuidBytes.length);
        out.write(uuidBytes);

        byte[] reasonBytes = this.host.getBytes();
        out.writeInt(reasonBytes.length);
        out.write(reasonBytes);

        out.writeInt(this.port);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte[] uuidBytes = new byte[in.readInt()];
        in.readFully(uuidBytes);
        this.uuid = UUID.fromString(new String(uuidBytes));

        byte[] reasonBytes = new byte[in.readInt()];
        in.readFully(reasonBytes);
        this.host = new String(reasonBytes);

        this.port = in.readInt();
    }

    @Override
    public void onPacket(ServerConnection connection) {
        if (connection.entry != null) {
            if (connection.getUuid() != null) {
                connection.disconnect("UUID already sent");
            }
            else {
                System.out.println("Adding server " + this.uuid + " (" + this.host + ":" + this.port + ")");
                connection.entry = new ServerEntry();
                connection.entry.setUuid(this.uuid);
            }
            return;
        }

        System.out.println("Adding server " + this.uuid + " (" + this.host + ":" + this.port + ")");
        connection.entry = new ServerEntry();
        connection.entry.setUuid(this.uuid);
        connection.entry.setIp(this.host);
        connection.entry.setPort(this.port);
    }

}
