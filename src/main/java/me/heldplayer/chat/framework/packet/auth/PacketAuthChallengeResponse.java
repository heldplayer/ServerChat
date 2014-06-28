
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.auth.ServerAuthentication;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;

/**
 * Sent in response to {@link PacketAuthChallenge}, sends a different challenge
 * to the original server to also verify the server's identity
 */
public class PacketAuthChallengeResponse extends ChatPacket {

    private UUID uuid;
    private String challenge;
    private byte[] signature;

    public PacketAuthChallengeResponse(UUID uuid, String challenge, byte[] signature) {
        this.uuid = uuid;
        this.challenge = challenge;
        this.signature = signature;
    }

    public PacketAuthChallengeResponse() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.uuid.toString());
        out.writeUTF(this.challenge);
        out.writeInt(this.signature.length);
        out.write(this.signature);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.uuid = UUID.fromString(in.readUTF());
        this.challenge = in.readUTF();
        this.signature = new byte[in.readInt()];
        in.readFully(this.signature);
    }

    @Override
    public void onPacket(LocalServer connection) {
        if (!this.uuid.equals(connection.getUuid())) {
            connection.kickServer("Mismatched UUIDs (" + this.uuid + " vs. " + connection.getUuid() + ")");
            return;
        }
        if (connection.getState() == ConnectionState.AUTHENTICATING) {
            boolean verified = ServerAuthentication.verifyIdentity(this.uuid, this.challenge, this.signature);
            if (verified) {
                try {
                    connection.setState(ConnectionState.AUTHENTICATED);
                }
                catch (AuthenticationException e) {
                    connection.kickServer(e.getMessage());
                    return;
                }
                connection.addPacket(new PacketAuthComplete());
            }
            else {
                connection.kickServer("Verification of server failed");
            }
        }
        else {
            connection.kickServer("Invalid connection state detected");
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
