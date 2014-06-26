
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Packet sent to target a server that isn't directly connected to the server
 */
public class PacketCrossServer extends ChatPacket {

    private UUID target;
    private UUID sender;
    private UUID[] stack;
    private byte[] data;

    public PacketCrossServer(UUID target, UUID sender, byte[] data, UUID... stack) {
        this.target = target;
        this.sender = sender;
        this.stack = stack;
    }

    public PacketCrossServer(UUID target, UUID sender, byte[] data, UUID[] stack, UUID stack0) {
        this.target = target;
        this.sender = sender;
        this.stack = new UUID[stack.length + 1];
        System.arraycopy(stack, 0, this.stack, 0, stack.length);
        this.stack[stack.length] = stack0;
    }

    public PacketCrossServer() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        String target = this.target.toString();
        byte[] targetBytes = target.getBytes();
        out.writeInt(targetBytes.length);
        out.write(targetBytes);

        String sender = this.target.toString();
        byte[] senderBytes = sender.getBytes();
        out.writeInt(senderBytes.length);
        out.write(senderBytes);

        out.writeInt(this.stack.length);
        for (UUID uuid : this.stack) {
            String uuidStr = uuid.toString();
            byte[] uuidBytes = uuidStr.getBytes();
            out.writeInt(uuidBytes.length);
            out.write(uuidBytes);
        }

        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte[] targetBytes = new byte[in.readInt()];
        in.readFully(targetBytes);
        this.target = UUID.fromString(new String(targetBytes));

        this.stack = new UUID[in.readInt()];
        for (int i = 0; i < this.stack.length; i++) {
            byte[] uuidBytes = new byte[in.readInt()];
            in.readFully(uuidBytes);
            this.stack[i] = UUID.fromString(new String(uuidBytes));
        }

        this.data = new byte[in.readInt()];
        in.readFully(targetBytes);
    }

    @Override
    public void onPacket(LocalServer connection) {
        if (connection.connectionsList.getConfiguration().getServerUUID().equals(this.target)) {
            // It arrived!
        }
        else {
            LocalServer remote = connection.connectionsList.getConnectionContaining(this.target);
            if (remote != null) {
                remote.addPacket(new PacketCrossServer(this.target, this.sender, data, this.stack, connection.getUuid()));
            }
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
