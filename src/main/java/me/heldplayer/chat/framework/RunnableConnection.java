
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import me.heldplayer.chat.framework.wrap.RunnableStoppable;

public class RunnableConnection extends RunnableStoppable {

    private final ServerSocket serverSocket;
    private final ConnectionsList connectionsList;

    protected RunnableConnection(ConnectionsList connectionsList, ServerSocket socket) {
        this.serverSocket = socket;
        this.connectionsList = connectionsList;
    }

    @Override
    public void doRun() {
        try {
            Socket socket = this.serverSocket.accept();
            System.err.println("==================== Got connection!");
            LocalServer connection = new LocalServer(this.connectionsList, socket);
            this.connectionsList.addConnection(connection);
        }
        catch (SocketException e) {}
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        super.stop();
        try {
            this.serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
