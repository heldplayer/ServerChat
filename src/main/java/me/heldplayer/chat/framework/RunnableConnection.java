
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RunnableConnection implements Runnable {

    boolean running;
    private final ServerSocket serverSocket;
    private final ConnectionsList connectionsList;

    RunnableConnection(ConnectionsList connectionsList, ServerSocket socket) {
        this.running = true;
        this.serverSocket = socket;
        this.connectionsList = connectionsList;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                Socket socket = this.serverSocket.accept();
                ServerConnection connection = new ServerConnection(socket);

                Thread.sleep(10L);
            }
            catch (IOException e) {}
            catch (InterruptedException e) {}
        }
    }

}
