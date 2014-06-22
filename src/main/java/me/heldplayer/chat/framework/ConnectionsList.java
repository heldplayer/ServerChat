
package me.heldplayer.chat.framework;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import me.heldplayer.chat.framework.config.IServerConfiguration;

public class ConnectionsList {

    private ServerSocket serverSocket;
    private IServerConfiguration config;
    private RunnableConnection runnable;
    private Thread thread;

    public ConnectionsList(IServerConfiguration config) {
        if (config.isOfflineMode()) {
            throw new RuntimeException("Mod is not supported in offline mode!");
        }

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
                int count = 500;
                try {
                    while (ConnectionsList.this.thread.isAlive() && count > 0) {
                        count--;
                        Thread.sleep(10L);
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (ConnectionsList.this.thread.isAlive()) {
                    ConnectionsList.this.thread.stop();
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

}
