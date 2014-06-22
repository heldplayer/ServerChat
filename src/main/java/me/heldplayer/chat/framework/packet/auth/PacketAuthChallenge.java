
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.packet.ChatPacket;

public class PacketAuthChallenge extends ChatPacket {

    private UUID uuid;
    private String challenge;
    private byte[] signature;

    public PacketAuthChallenge(UUID uuid, String challenge, byte[] signature) {
        this.uuid = uuid;
        this.challenge = challenge;
        this.signature = signature;
    }

    public PacketAuthChallenge() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        String uuid = this.uuid.toString();
        byte[] uuidBytes = uuid.getBytes();
        out.write(uuidBytes.length);
        out.write(uuidBytes);

        byte[] challengeBytes = challenge.getBytes();
        out.write(challengeBytes.length);
        out.write(challengeBytes);

        out.write(signature.length);
        out.write(signature);
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
        // TODO Auto-generated method stub

    }

}
