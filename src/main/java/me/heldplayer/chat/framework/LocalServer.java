
package me.heldplayer.chat.framework;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;
import me.heldplayer.chat.framework.packet.PacketDisconnect;
import me.heldplayer.chat.framework.wrap.ServerIOWrapper;
import me.heldplayer.chat.framework.wrap.ThreadWrapper;

public class LocalServer extends Server {

    public final ServerIOWrapper serverIO = new ServerIOWrapper();
    public final ThreadWrapper thread = new ThreadWrapper();
    private ConnectionState state = ConnectionState.DISCONNECTED;

    public final ConnectionsList connectionsList;
    public ServerEntry entry;
    private ArrayList<RemoteServer> remoteConnections = new ArrayList<RemoteServer>();

    private boolean disconnecting;
    private Queue<ChatPacket> outboundPackets = new ConcurrentLinkedQueue<ChatPacket>();

    public LocalServer(ConnectionsList connectionsList, Socket socket) throws IOException {
        this.connectionsList = connectionsList;
        this.setInOut(socket);
        this.state = ConnectionState.AUTHENTICATING;

        this.entry = new ServerEntry();
        String[] ip = socket.getInetAddress().toString().split("/");
        if (ip[0].isEmpty()) {
            this.entry.setIp(ip[1]);
        }
        else {
            this.entry.setIp(ip[0]);
        }

        this.startThread();
    }

    public LocalServer(ConnectionsList connectionsList, ServerEntry entry) {
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
        this.serverIO.setIO(socket);
    }

    public void startThread() {
        this.thread.start(new RunnableReadWrite(this), "ServerChat read/write thread");
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

    public void disconnectServer(String reason) {
        System.err.println("Disconnecting server for '" + reason + "'");
        if (reason != null) {
            this.addPacket(new PacketDisconnect(reason, false));
        }
        this.disconnecting = true;
        this.connectionsList.removeConnection(this);
    }

    public void kickServer(String reason) {
        System.err.println("Kicking server for '" + reason + "'");
        if (reason != null) {
            this.addPacket(new PacketDisconnect(reason, true));
        }
        this.disconnecting = true;
        this.connectionsList.removeConnection(this);
        this.connectionsList.removeServer(this.getUuid());
    }

    @Override
    public UUID getUuid() {
        if (this.entry != null) {
            return this.entry.getUuid();
        }
        return null;
    }

    public void addRemoteConnection(RemoteServer connection) {
        this.remoteConnections.add(connection);
    }

    public void removeRemoteConnection(RemoteServer connection) {
        this.remoteConnections.remove(connection);
    }

    public List<RemoteServer> getRemoteConnections() {
        return this.remoteConnections;
    }

    public boolean isDisconnecting() {
        return disconnecting;
    }

    public void setDisconnecting(boolean disconnecting) {
        this.disconnecting = disconnecting;
    }

    public Queue<ChatPacket> getOutboundPacketsQueue() {
        return outboundPackets;
    }

}
