
package me.heldplayer.chat.framework;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.heldplayer.chat.framework.config.IServerConfiguration;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.auth.PacketServerCredentials;
import me.heldplayer.chat.framework.packet.coms.PacketChallengeRequest;
import me.heldplayer.chat.framework.packet.coms.PacketRemoteServerConnected;
import me.heldplayer.chat.framework.util.KeyUtils;

public class ConnectionsList {

    private ServerSocket serverSocket;
    private IServerConfiguration config;
    private RunnableConnection runnable;
    private Thread thread;

    private List<ServerConnection> connections;

    public ConnectionsList(IServerConfiguration config, File saveDir) {
        if (config.isOfflineMode()) {
            throw new RuntimeException("Mod is not supported in offline mode!");
        }

        this.connections = new ArrayList<ServerConnection>();
        this.config = config;
        this.config.load(saveDir);
        this.config.save(saveDir);
    }

    public void startListening() throws IOException {
        InetAddress adress = null;

        String host = this.config.getHost();
        int port = this.config.getPort();
        if (host != null && !host.isEmpty()) {
            InetAddress.getByName(host);
        }

        this.serverSocket = new ServerSocket(port, 0, adress);

        this.thread = new Thread(this.runnable = new RunnableConnection(this, this.serverSocket), "ServerChat connection listener");
        this.thread.start();

        for (ServerEntry entry : this.config.getServers()) {
            ServerConnection connection = new ServerConnection(this, entry);
            this.connections.add(connection);
        }
    }

    public void stopListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            @SuppressWarnings("deprecation")
            public void run() {
                ConnectionsList.this.runnable.stopListening();
                for (ServerConnection connection : ConnectionsList.this.connections) {
                    if (connection.runnable != null) {
                        connection.runnable.running = false;
                    }
                }
                int count = 500;
                try {
                    boolean goOn = true;
                    while (goOn && count > 0) {
                        count--;

                        goOn = ConnectionsList.this.thread.isAlive();
                        for (ServerConnection connection : ConnectionsList.this.connections) {
                            if (connection.thread != null) {
                                goOn |= connection.thread.isAlive();
                            }
                        }

                        Thread.sleep(10L);
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ConnectionsList.this.thread.isAlive()) {
                    ConnectionsList.this.thread.stop(new RuntimeException("Stopping connection"));
                }

                for (ServerConnection connection : ConnectionsList.this.connections) {
                    if (connection.thread != null && connection.thread.isAlive()) {
                        connection.thread.stop(new RuntimeException("Stopping connection"));
                    }
                }
            }
        }, "Connection Terminator Thread");
        thread.start();
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addConnection(ServerConnection connection) {
        this.connections.add(connection);
    }

    public void removeConnection(ServerConnection connection) {
        this.connections.remove(connection);
    }

    public List<ServerConnection> getConnections() {
        return this.connections;
    }

    public byte[] getSignature(String input) {
        return KeyUtils.getSignature(this.config.getPrivateKey(), input);
    }

    public IServerConfiguration getConfiguration() {
        return this.config;
    }

    public PacketServerCredentials getServerCredentials() {
        return new PacketServerCredentials(this.config.getServerUUID(), this.config.getHost(), this.config.getPort());
    }

    public void broadcastConnection(ServerConnection connection, String challenge, byte[] signature) {
        for (ServerConnection currConnection : this.connections) {
            if (currConnection != connection && currConnection.getUuid().equals(connection.getUuid())) {
                currConnection.addPacket(new PacketRemoteServerConnected(connection.getUuid(), challenge, signature));
            }
        }
    }

    public void broadcastRemoteConnection(ServerConnection origin, RemoteConnection connection, String challenge, byte[] signature) {
        for (ServerConnection currServerConnection : this.connections) {
            if (currServerConnection.getUuid().equals(connection.uuid)) {
                return;
            }
            if (currServerConnection.getUuid().equals(origin.getUuid())) {
                continue;
            }
            for (RemoteConnection currRemoteConnection : currServerConnection.getRemoteConnections()) {
                if (currRemoteConnection.uuid.equals(connection.uuid)) {
                    return;
                }
            }
        }

        for (ServerConnection currServerConnection : this.connections) {
            if (currServerConnection.getUuid().equals(origin.getUuid())) {
                continue;
            }
            currServerConnection.addPacket(new PacketRemoteServerConnected(connection.uuid, challenge, signature));
        }
    }

    public void synchronizeData(ServerConnection connection) {
        // Tell the new server about all connected servers
        for (ServerConnection currServerConnection : this.connections) {
            if (currServerConnection.getUuid().equals(connection.getUuid())) {
                continue;
            }

            connection.addPacket(new PacketChallengeRequest(currServerConnection.getUuid(), connection.getUuid()));

            for (RemoteConnection currRemoteConnection : currServerConnection.getRemoteConnections()) {
                if (currRemoteConnection.uuid.equals(connection.getUuid())) {
                    connection.addPacket(new PacketChallengeRequest(currRemoteConnection.uuid, connection.getUuid()));
                }
            }
        }
    }

    public void sendToServer(UUID uuid, ChatPacket packet) {
        for (ServerConnection server : this.connections) {
            if (server.getUuid().equals(uuid)) {
                server.addPacket(packet);

                break;
            }
        }
    }

    public ServerConnection getConnectionContaining(UUID uuid) {
        for (ServerConnection server : this.connections) {
            if (server.getUuid().equals(uuid)) {
                return server;
            }

            for (RemoteConnection remoteConnection : server.getRemoteConnections()) {
                if (remoteConnection.uuid.equals(uuid)) {
                    return server;
                }
            }
        }

        return null;
    }

}
