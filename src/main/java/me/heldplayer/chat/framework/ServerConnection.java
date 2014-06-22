
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import me.heldplayer.chat.framework.config.ServerEntry;

public class ServerConnection {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private ConnectionState state = ConnectionState.DISCONNECTED;

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.in = this.socket.getInputStream();
        this.out = this.socket.getOutputStream();
    }

    public ServerConnection(ServerEntry entry) {
        // TODO: attempt to connect
    }

    public ConnectionState getState() {
        return this.state;
    }

    public void setState(ConnectionState state) {
        this.state = state;
    }

}
