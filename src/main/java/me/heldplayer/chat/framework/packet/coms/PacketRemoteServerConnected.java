
package me.heldplayer.chat.framework.packet.coms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;
import me.heldplayer.chat.framework.auth.ServerAuthentication;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Sent by a server when a server has successfully connected or sent for each
 * server connected to the server when connecting to a server
 * 
 * The event gets propogated along all servers if the credentials are valid, if
 * the server is already directly connected then it is ignored.
 */
public class PacketRemoteServerConnected extends ChatPacket {

    private UUID uuid;
    private String challenge;
    private byte[] signature;

    public PacketRemoteServerConnected(UUID uuid, String challenge, byte[] signature) {
        this.uuid = uuid;
        this.challenge = challenge;
        this.signature = signature;
    }

    public PacketRemoteServerConnected() {}

    @Override
    public void write(DataOutputStream out) throws IOException {
        String uuid = this.uuid.toString();
        byte[] uuidBytes = uuid.getBytes();
        out.writeInt(uuidBytes.length);
        out.write(uuidBytes);

        byte[] challengeBytes = this.challenge.getBytes();
        out.writeInt(challengeBytes.length);
        out.write(challengeBytes);

        out.writeInt(this.signature.length);
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
    public void onPacket(LocalServer connection) {
        boolean verified = ServerAuthentication.verifyIdentity(this.uuid, this.challenge, this.signature);
        if (verified) {
            RemoteServer remoteConnection = new RemoteServer(this.uuid);
            System.out.println(String.format("Server with UUID %s connected remotely through %s", this.uuid, connection.getUuid()));
            connection.addRemoteConnection(remoteConnection);
            connection.connectionsList.broadcastRemoteConnection(connection, remoteConnection, this.challenge, this.signature);
        }
        else {
            connection.disconnectServer("Unsafe connection detected");
        }
    }

    @Override
    public void onPacket(RemoteServer connection) {}

}
