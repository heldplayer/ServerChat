
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.packet.ConnectionState;
import me.heldplayer.chat.framework.packet.auth.PacketAuthChallenge;
import me.heldplayer.chat.framework.util.KeyUtils;

public class RunnableAttemptConnect implements Runnable {

    private final ConnectionsList connectionsList;
    private final LocalServer connection;

    protected RunnableAttemptConnect(ConnectionsList connectionsList, LocalServer connection) {
        this.connection = connection;
        this.connectionsList = connectionsList;
    }

    @Override
    public void run() {
        try {
            this.connection.setState(ConnectionState.CONNECTING);

            try {
                if (this.connection.entry != null) {
                    String address = this.connection.entry.getIp();
                    int port = this.connection.entry.getPort();
                    Socket socket = new Socket(address, port);
                    this.connection.setInOut(socket);
                    this.connection.setState(ConnectionState.AUTHENTICATING);
                    UUID uuid = this.connectionsList.getConfiguration().getServerUUID();
                    String challenge = KeyUtils.getRandomChallenge();
                    byte[] signature = KeyUtils.getSignature(this.connection.connectionsList.getConfiguration().getPrivateKey(), challenge);
                    this.connection.addPacket(this.connectionsList.getServerCredentials());
                    this.connection.addPacket(new PacketAuthChallenge(uuid, challenge, signature));
                    this.connection.startThread();
                    return;
                }
            }
            catch (UnknownHostException e) {
                this.connection.disconnectServer(null);
            }
            catch (IOException e) {
                this.connection.disconnectServer(null);
            }

            this.connection.setState(ConnectionState.DISCONNECTED);
        }
        catch (AuthenticationException e) {
            this.connection.disconnectServer("Error connecting: " + e.getMessage());
        }
    }
}
