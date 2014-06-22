
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.ConnectionState;
import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.auth.ServerAuthentication;
import me.heldplayer.chat.framework.packet.ChatPacket;

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
        String uuid = this.uuid.toString();
        byte[] uuidBytes = uuid.getBytes();
        out.write(uuidBytes.length);
        out.write(uuidBytes);

        byte[] challengeBytes = this.challenge.getBytes();
        out.write(challengeBytes.length);
        out.write(challengeBytes);

        out.write(this.signature.length);
        out.write(this.signature);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        byte[] uuidBytes = new byte[in.readInt()];
        in.readFully(uuidBytes);
        this.uuid = UUID.fromString(new String(uuidBytes));

        byte[] challengeBytes = new byte[in.readInt()];
        in.readFully(challengeBytes);
        this.challenge = new String(challengeBytes);

        this.signature = new byte[in.readInt()];
        in.readFully(this.signature);
    }

    @Override
    public void onPacket(ServerConnection connection) {
        if (connection.getState() == ConnectionState.AUTHENTICATING) {
            boolean verified = ServerAuthentication.verifyIdentity(this.uuid, this.challenge, this.signature);
            if (verified) {
                try {
                    connection.setState(ConnectionState.AUTHENTICATED);
                }
                catch (AuthenticationException e) {
                    connection.disconnect(e.getMessage());
                    return;
                }
                connection.addPacket(new PacketAuthenticationComplete());
            }
            else {
                connection.disconnect("Verification of server failed");
            }
        }
        else {
            connection.disconnect("Invalid connection state detected");
        }
    }

}
