
package me.heldplayer.chat.framework;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.heldplayer.chat.framework.config.IServerConfiguration;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;
import me.heldplayer.chat.framework.packet.auth.PacketServerCredentials;
import me.heldplayer.chat.framework.packet.coms.PacketChallengeRequest;
import me.heldplayer.chat.framework.packet.coms.PacketRemoteServerConnected;
import me.heldplayer.chat.framework.util.KeyUtils;

public class ConnectionsList {

    private ServerSocket serverSocket;
    private IServerConfiguration config;
    private RunnableConnection runnable;
    private Thread thread;

    private Queue<LocalServer> connections;
    private Collection<LocalServer> roConnections;
    private File saveDir;

    public ConnectionsList(IServerConfiguration config, File saveDir) {
        if (config.isOfflineMode()) {
            throw new RuntimeException("Mod is not supported in offline mode!");
        }
        this.saveDir = saveDir;

        this.connections = new ConcurrentLinkedQueue<LocalServer>();
        this.roConnections = Collections.unmodifiableCollection(this.connections);
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
            LocalServer connection = new LocalServer(this, entry);
            this.connections.add(connection);
        }
    }

    public void stopListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            @SuppressWarnings("deprecation")
            public void run() {
                for (LocalServer connection : ConnectionsList.this.connections) {
                    connection.disconnectServer("Shutting down...");
                }
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException e1) {}
                ConnectionsList.this.runnable.stop();
                ;
                int count = 500;
                try {
                    boolean goOn = true;
                    while (goOn && count > 0) {
                        count--;

                        goOn = ConnectionsList.this.thread.isAlive();
                        for (LocalServer connection : ConnectionsList.this.connections) {
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

                for (LocalServer connection : ConnectionsList.this.connections) {
                    if (connection.thread != null && connection.thread.isAlive()) {
                        connection.thread.forceStop(new RuntimeException("Stopping connection"));
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

    public void addConnection(LocalServer connection) {
        this.connections.add(connection);
    }

    public void removeConnection(LocalServer connection) {
        this.connections.remove(connection);
    }

    public void removeServer(UUID uuid) {
        if (uuid == null) {
            return;
        }
        List<ServerEntry> entries = this.config.getServers();

        for (Iterator<ServerEntry> i = entries.iterator(); i.hasNext();) {
            ServerEntry entry = i.next();

            if (entry.getUuid() == null) {
                continue;
            }

            if (entry.getUuid().equals(uuid)) {
                i.remove();
                break;
            }
        }

        this.save();
    }

    public Collection<LocalServer> getConnections() {
        return this.roConnections;
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

    public void broadcastConnection(LocalServer connection, String challenge, byte[] signature) {
        for (LocalServer currConnection : this.connections) {
            if (currConnection != connection && currConnection.getUuid().equals(connection.getUuid())) {
                currConnection.addPacket(new PacketRemoteServerConnected(connection.getUuid(), challenge, signature));
            }
        }
    }

    public void broadcastRemoteConnection(LocalServer origin, RemoteServer connection, String challenge, byte[] signature) {
        for (LocalServer currServerConnection : this.connections) {
            if (currServerConnection.getUuid().equals(connection.uuid)) {
                return;
            }
            if (currServerConnection.getUuid().equals(origin.getUuid())) {
                continue;
            }
            for (RemoteServer currRemoteConnection : currServerConnection.getRemoteConnections()) {
                if (currRemoteConnection.uuid.equals(connection.uuid)) {
                    return;
                }
            }
        }

        for (LocalServer currServerConnection : this.connections) {
            if (currServerConnection.getUuid().equals(origin.getUuid())) {
                continue;
            }
            currServerConnection.addPacket(new PacketRemoteServerConnected(connection.uuid, challenge, signature));
        }
    }

    public void synchronizeData(LocalServer connection) {
        // Tell the new server about all connected servers
        for (LocalServer currServerConnection : this.connections) {
            if (currServerConnection.getState() != ConnectionState.CONNECTED) {
                continue;
            }
            if (currServerConnection.getUuid().equals(connection.getUuid())) {
                continue;
            }

            System.out.println(String.format("Telling %s about locally connected server %s", connection.getUuid(), currServerConnection.getUuid()));
            currServerConnection.addPacket(new PacketChallengeRequest(currServerConnection.getUuid(), connection.getUuid()));

            // Also tell the other servers about the new server
            System.out.println(String.format("Telling locally connected server %s about %s", currServerConnection.getUuid(), connection.getUuid()));
            connection.addPacket(new PacketChallengeRequest(connection.getUuid(), currServerConnection.getUuid()));

            for (RemoteServer currRemoteConnection : currServerConnection.getRemoteConnections()) {
                if (currRemoteConnection.uuid.equals(connection.getUuid())) {
                    continue;
                }

                System.out.println(String.format("Telling %s about remotely connected server %s", connection.getUuid(), currServerConnection.getUuid()));

                currServerConnection.addPacket(new PacketChallengeRequest(currRemoteConnection.uuid, connection.getUuid()));
            }
        }
    }

    public void sendToServer(UUID uuid, ChatPacket packet) {
        for (LocalServer server : this.connections) {
            if (server.getUuid().equals(uuid)) {
                server.addPacket(packet);

                break;
            }
        }
    }

    public LocalServer getConnectionContaining(UUID uuid) {
        for (LocalServer server : this.connections) {
            if (server.getUuid().equals(uuid)) {
                return server;
            }

            for (RemoteServer remoteConnection : server.getRemoteConnections()) {
                if (remoteConnection.uuid.equals(uuid)) {
                    return server;
                }
            }
        }

        return null;
    }

    public void save() {
        List<ServerEntry> entries = this.config.getServers();

        for (LocalServer connection : this.connections) {
            boolean present = false;

            if (connection.entry.getUuid() == null) {
                continue;
            }

            for (ServerEntry entry : entries) {
                if (connection.entry.getUuid().equals(entry.getUuid())) {
                    present = true;
                    if (connection.entry.getIp() != null && !connection.entry.getIp().isEmpty()) {
                        entry.setIp(connection.entry.getIp());
                    }
                    if (connection.entry.getPort() != 0) {
                        entry.setPort(connection.entry.getPort());
                    }
                }
            }

            if (!present) {
                entries.add(connection.entry);
            }
        }

        this.config.save(this.saveDir);
    }

    public void load() {
        this.config.load(this.saveDir);
    }

}
