
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
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
        String target = this.target.toString();
        byte[] targetBytes = target.getBytes();
        out.writeInt(targetBytes.length);
        out.write(targetBytes);

        out.writeInt(this.stack.length);
        for (UUID uuid : this.stack) {
            String uuidStr = uuid.toString();
            byte[] uuidBytes = uuidStr.getBytes();
            out.writeInt(uuidBytes.length);
            out.write(uuidBytes);
        }

        byte[] challengeBytes = this.challenge.getBytes();
        out.writeInt(challengeBytes.length);
        out.write(challengeBytes);

        out.writeInt(this.signature.length);
        out.write(this.signature);
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

}
