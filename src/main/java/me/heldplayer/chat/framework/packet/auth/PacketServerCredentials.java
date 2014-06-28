
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
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
    public void onPacket(LocalServer connection) {
        if (connection.entry != null) {
            if (connection.getUuid() != null && !connection.getUuid().equals(this.uuid)) {
                connection.kickServer("UUID already sent");
            }
            else {
                ServerEntry entry = connection.entry;
                entry.setUuid(this.uuid);
                if (!this.host.isEmpty()) {
                    entry.setIp(this.host);
                }
                entry.setPort(this.port);
                connection.log.trace("Added server %s (%s:%s)", entry.getUuid(), entry.getIp(), entry.getPort());
            }
            return;
        }

        connection.log.debug("Server doesn't have an entry set?!");
        connection.log.debug("Adding server %s (%s:%s)", this.uuid, this.host, this.port);
        connection.entry = new ServerEntry();
        connection.entry.setUuid(this.uuid);
        connection.entry.setIp(this.host);
        connection.entry.setPort(this.port);
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
