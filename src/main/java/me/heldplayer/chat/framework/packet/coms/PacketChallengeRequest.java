
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.util.KeyUtils;

/**
 * Sent by a server to request a token and its signature for verification, used
 * for other servers to authenticate the server
 */
public class PacketChallengeRequest extends ChatPacket {

    private UUID target;
    private UUID[] stack;

    public PacketChallengeRequest(UUID target, UUID... stack) {
        this.target = target;
        this.stack = stack;
    }

    public PacketChallengeRequest(UUID target, UUID[] stack, UUID stack0) {
        this.target = target;
        this.stack = new UUID[stack.length + 1];
        System.arraycopy(stack, 0, this.stack, 0, stack.length);
        this.stack[stack.length] = stack0;
    }

    public PacketChallengeRequest() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.target.toString());

        out.writeInt(this.stack.length);
        for (UUID uuid : this.stack) {
            out.writeUTF(uuid.toString());
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.target = UUID.fromString(in.readUTF());

        this.stack = new UUID[in.readInt()];
        for (int i = 0; i < this.stack.length; i++) {
            this.stack[i] = UUID.fromString(in.readUTF());
        }
    }

    @Override
    public void onPacket(LocalServer connection) {
        if (connection.connectionsList.getConfiguration().getServerUUID().equals(this.target)) {
            String challenge = KeyUtils.getRandomChallenge();
            byte[] signature = KeyUtils.getSignature(connection.connectionsList.getConfiguration().getPrivateKey(), challenge);
            connection.addPacket(new PacketChallengeResponse(this.target, this.stack, challenge, signature));
        }
        else {
            LocalServer remote = connection.connectionsList.getConnectionContaining(this.target);
            if (remote != null) {
                remote.addPacket(new PacketChallengeRequest(this.target, this.stack, connection.getUuid()));
            }
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
