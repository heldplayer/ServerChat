
package me.heldplayer.chat.framework.wrap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerIOWrapper {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void close() {
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException e) {}
        }
        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException e) {}
        }
        if (this.out != null) {
            try {
                this.out.close();
            }
            catch (IOException e) {}
        }
    }

    public void setIO(Socket socket) throws IOException {
        this.close();
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public boolean isClosed() {
        return this.socket != null ? this.socket.isClosed() : true;
    }

    public boolean isConnected() {
        return this.socket != null ? this.socket.isConnected() : false;
    }

    public DataInputStream getIn() {
        return this.in;
    }

    public DataOutputStream getOut() {
        return this.out;
    }

}
