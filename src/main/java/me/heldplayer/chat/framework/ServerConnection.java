
package me.heldplayer.chat.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.auth.PacketDisconnect;

public class ServerConnection {

    Socket socket;
    DataInputStream in;
    DataOutputStream out;
    private ConnectionState state = ConnectionState.DISCONNECTED;

    public final ConnectionsList connectionsList;
    RunnableReadWrite runnable;
    Thread thread;

    boolean disconnecting;
    LinkedList<ChatPacket> inboundPackets;
    LinkedList<ChatPacket> outboundPackets;

    public ServerConnection(ConnectionsList connectionsList, Socket socket) throws IOException {
        this.connectionsList = connectionsList;
        this.socket = socket;
        this.in = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
        this.state = ConnectionState.AUTHENTICATING;

        this.startThread();
    }

    public ServerConnection(ConnectionsList connectionsList, ServerEntry entry) {
        // TODO: attempt to connect
        this.connectionsList = connectionsList;
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
        if (state == ConnectionState.AUTHENTICATED && this.state != ConnectionState.AUTHENTICATING) {
            throw new AuthenticationException(String.format("Bad transition of states, tried to go from %s to %s", this.state, state));
        }
        if (state == ConnectionState.CONNECTED && this.state != ConnectionState.AUTHENTICATED) {
            throw new AuthenticationException(String.format("Bad transition of states, tried to go from %s to %s", this.state, state));
        }
        this.state = state;
    }

    public void addPacket(ChatPacket packet) {
        this.outboundPackets.add(packet);
    }

    public void disconnect(String reason) {
        this.addPacket(new PacketDisconnect(reason));
        this.disconnecting = true;
    }

}
