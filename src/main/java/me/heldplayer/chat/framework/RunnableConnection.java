
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class RunnableConnection implements Runnable {

    private boolean running;
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
                System.err.println("==================== Got connection!");
                ServerConnection connection = new ServerConnection(this.connectionsList, socket);
                this.connectionsList.addConnection(connection);

                Thread.sleep(10L);
            }
            catch (SocketException e) {}
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {}
        }
    }

    public void stopListening() {
        this.running = false;
        try {
            this.serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
