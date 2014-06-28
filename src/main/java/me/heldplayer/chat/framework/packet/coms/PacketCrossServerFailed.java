
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.Server;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Packet sent to target a server that isn't directly connected to the server
 */
public class PacketCrossServerFailed extends ChatPacket {

    private UUID target;
    private UUID sender;
    private UUID[] stack;
    private byte[] data;

    public PacketCrossServerFailed(UUID target, UUID sender, byte[] data, UUID... stack) {
        this.target = target;
        this.sender = sender;
        this.stack = stack;
    }

    public PacketCrossServerFailed(UUID target, UUID sender, byte[] data, UUID[] stack, UUID stack0) {
        this.target = target;
        this.sender = sender;
        this.stack = new UUID[stack.length + 1];
        System.arraycopy(stack, 0, this.stack, 0, stack.length);
        this.stack[stack.length] = stack0;
    }

    public PacketCrossServerFailed() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.target.toString());
        out.writeUTF(this.sender.toString());

        out.writeInt(this.stack.length);
        for (UUID uuid : this.stack) {
            out.writeUTF(uuid.toString());
        }

        out.writeInt(this.data.length);
        out.write(this.data);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.target = UUID.fromString(in.readUTF());

        this.stack = new UUID[in.readInt()];
        for (int i = 0; i < this.stack.length; i++) {
            this.stack[i] = UUID.fromString(in.readUTF());
        }

        this.data = new byte[in.readInt()];
        in.readFully(this.data);
    }

    @Override
    public void onPacket(LocalServer connection) {
        this.onPacket((Server) connection);
    }

    @Override
    public void onPacket(RemoteServer connection) {
        this.onPacket((Server) connection);
    }

    private void onPacket(Server connection) {

    }

}
