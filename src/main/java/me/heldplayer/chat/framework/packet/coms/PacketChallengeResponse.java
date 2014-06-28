
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Sent in response to {@link PacketChallengeRequest}
 * 
 * @see PacketChallengeRequest
 */
public class PacketChallengeResponse extends ChatPacket {

    private UUID target;
    private UUID[] stack;
    private String challenge;
    private byte[] signature;

    public PacketChallengeResponse(UUID target, UUID[] stack, String challenge, byte[] signature) {
        this.target = target;
        this.stack = stack;
        this.challenge = challenge;
        this.signature = signature;
    }

    public PacketChallengeResponse() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.target.toString());

        out.writeInt(this.stack.length);
        for (UUID uuid : this.stack) {
            out.writeUTF(uuid.toString());
        }

        out.writeUTF(this.challenge);
        out.writeInt(this.signature.length);
        out.write(this.signature);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.target = UUID.fromString(in.readUTF());

        this.stack = new UUID[in.readInt()];
        for (int i = 0; i < this.stack.length; i++) {
            this.stack[i] = UUID.fromString(in.readUTF());
        }

        byte[] challengeBytes = new byte[in.readInt()];
        in.readFully(challengeBytes);
        this.challenge = new String(challengeBytes);

        this.signature = new byte[in.readInt()];
        in.readFully(this.signature);
    }

    @Override
    public void onPacket(LocalServer connection) {
        if (this.stack.length > 1) {
            UUID[] stack = new UUID[this.stack.length - 1];
            System.arraycopy(this.stack, 0, stack, 0, stack.length);
            connection.connectionsList.sendToServer(this.stack[stack.length], new PacketChallengeResponse(this.target, stack, this.challenge, this.signature));
        }
        else {
            connection.connectionsList.sendToServer(this.stack[0], new PacketRemoteServerConnected(this.target, this.challenge, this.signature));
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
