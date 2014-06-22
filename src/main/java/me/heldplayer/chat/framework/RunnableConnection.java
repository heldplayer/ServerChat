
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RunnableConnection implements Runnable {

    boolean running;
    private ServerSocket serverSocket;

    RunnableConnection(ServerSocket socket) {
        running = true;
        this.serverSocket = socket;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();

                Thread.sleep(10L);
            }
            catch (IOException e) {}
            catch (InterruptedException e) {}
        }
    }

}
