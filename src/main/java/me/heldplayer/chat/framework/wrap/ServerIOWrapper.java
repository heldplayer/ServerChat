
package me.heldplayer.chat.framework.wrap;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import me.heldplayer.chat.framework.ConnectionsList;
import me.heldplayer.chat.framework.logging.Log;

public class ServerIOWrapper {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public static Log log = ConnectionsList.log.getSubLog("IO");

    public void close() {
        log.debug("Closing socket %s (%s)", this.socket, this);
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException e) {}
            finally {
                this.socket = null;
            }
        }
        if (this.in != null) {
            try {
                this.in.close();
            }
            catch (IOException e) {}
            finally {
                this.in = null;
            }
        }
        if (this.out != null) {
            try {
                this.out.close();
            }
            catch (IOException e) {}
            finally {
                this.out = null;
            }
        }
    }

    public void setIO(Socket socket) throws IOException {
        if (this.socket != socket) {
            this.close();
        }
        log.debug("Setting socket %s (%s)", socket, this);
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
