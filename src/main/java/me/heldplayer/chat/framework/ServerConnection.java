
package me.heldplayer.chat.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
    private ArrayList<RemoteConnection> remoteConnections = new ArrayList<RemoteConnection>();
    RunnableReadWrite runnable;
    Thread thread;

    boolean disconnecting;
    LinkedList<ChatPacket> outboundPackets = new LinkedList<ChatPacket>();

    public ServerConnection(ConnectionsList connectionsList, Socket socket) throws IOException {
        this.connectionsList = connectionsList;
        this.setInOut(socket);
        this.state = ConnectionState.AUTHENTICATING;

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
        this.socket = socket;
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
        System.out.println(String.format("Transitioning state from %s to %s", this.state, state));
        this.state = state;
    }

    public void addPacket(ChatPacket packet) {
        this.outboundPackets.add(packet);
    }

    public void disconnect(String reason) {
        System.out.println("Disconnecting server for '" + reason + "'");
        if (reason != null) {
            this.addPacket(new PacketDisconnect(reason));
        }
        this.disconnecting = true;
        this.connectionsList.removeConnection(this);
    }

    public UUID getUuid() {
        if (this.entry != null) {
            return this.entry.getUuid();
        }
        return null;
    }

    public void addRemoteConnection(RemoteConnection connection) {
        this.remoteConnections.add(connection);
    }

    public void removeRemoteConnection(RemoteConnection connection) {
        this.remoteConnections.remove(connection);
    }

    public List<RemoteConnection> getRemoteConnections() {
        return this.remoteConnections;
    }

}
