
package me.heldplayer.chat.framework;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import me.heldplayer.chat.framework.config.IServerConfiguration;
import me.heldplayer.chat.framework.util.KeyUtils;

public class ConnectionsList {

    private ServerSocket serverSocket;
    private IServerConfiguration config;
    private RunnableConnection runnable;
    private Thread thread;

    private List<ServerConnection> connections;

    public ConnectionsList(IServerConfiguration config) {
        if (config.isOfflineMode()) {
            throw new RuntimeException("Mod is not supported in offline mode!");
        }

        this.connections = new ArrayList<ServerConnection>();
        this.config = config;
        this.config.load(new File("servers.cfg"));
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
    }

    public void stopListening() {
        Thread thread = new Thread(new Runnable() {
            @Override
            @SuppressWarnings("deprecation")
            public void run() {
                ConnectionsList.this.runnable.running = false;
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
                    ConnectionsList.this.thread.stop();
                }

                for (ServerConnection connection : ConnectionsList.this.connections) {
                    if (connection.thread != null && connection.thread.isAlive()) {
                        connection.thread.stop();
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

    public byte[] getSignature(String input) {
        return KeyUtils.getSignature(this.config.getPrivateKey(), input);
    }

    public IServerConfiguration getConfiguration() {
        return this.config;
    }

}
