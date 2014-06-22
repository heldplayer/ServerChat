
package me.heldplayer.chat.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;
import me.heldplayer.chat.framework.packet.PacketDisconnect;

public class ServerConnection {

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private ConnectionState state = ConnectionState.DISCONNECTED;

    public final ConnectionsList connectionsList;
    public ServerEntry entry;
    RunnableReadWrite runnable;
    Thread thread;

    boolean disconnecting;
    LinkedList<ChatPacket> inboundPackets;
    LinkedList<ChatPacket> outboundPackets;

    public ServerConnection(ConnectionsList connectionsList, Socket socket) throws IOException {
        this.connectionsList = connectionsList;
        this.socket = socket;
        this.state = ConnectionState.AUTHENTICATING;

        this.setInOut(this.socket);

        this.startThread();
    }

    public ServerConnection(ConnectionsList connectionsList, ServerEntry entry) {
        this.connectionsList = connectionsList;

        this.state = ConnectionState.CONNECTING;
        this.entry = entry;

        this.attemptConnect();
    }

    public void attemptConnect() {
        Thread thread = new Thread(new RunnableAttemptConnect(this.connectionsList, this), "Connecting Thread");
        thread.setDaemon(true);
        thread.start();
    }

    protected void setInOut(Socket socket) throws IOException {
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
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void startThread() {
        if (this.thread == null || !this.thread.isAlive()) {
            this.thread = new Thread(this.runnable = new RunnableReadWrite(this.connectionsList, this), "ServerChat read/write thread");
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    public ConnectionState getState() {
        return this.state;
    }

    public void setState(ConnectionState state) throws AuthenticationException {
        if (state != ConnectionState.DISCONNECTED) {
            if (state == ConnectionState.AUTHENTICATED && this.state != ConnectionState.AUTHENTICATING) {
                throw new AuthenticationException(String.format("Bad transition of states, tried to go from %s to %s", this.state, state));
            }
            if (state == ConnectionState.CONNECTED && this.state != ConnectionState.AUTHENTICATED) {
                throw new AuthenticationException(String.format("Bad transition of states, tried to go from %s to %s", this.state, state));
            }
        }
        this.state = state;
    }

    public void addPacket(ChatPacket packet) {
        this.outboundPackets.add(packet);
    }

    public void disconnect(String reason) {
        if (reason != null) {
            this.addPacket(new PacketDisconnect(reason));
        }
        this.disconnecting = true;
        this.connectionsList.removeConnection(this);
    }
}
