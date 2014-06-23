
package me.heldplayer.chat.framework;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.packet.ChatPacket;

public class RunnableReadWrite implements Runnable {

    boolean running;
    private final ConnectionsList connectionsList;
    private final ServerConnection connection;

    RunnableReadWrite(ConnectionsList connectionsList, ServerConnection connection) {
        this.running = true;
        this.connection = connection;
        this.connectionsList = connectionsList;
    }

    @Override
    public void run() {
        try {
            while (this.running || this.connection.disconnecting) {
                if (this.connection.socket.isClosed()) {
                    System.out.println("Connection lost");

                    return;
                }

                if (this.connection.disconnecting) {
                    this.running = false;
                    this.connection.disconnecting = false;
                }

                // Read
                DataInputStream in = this.connection.in;
                while (in.available() > 0) {
                    byte[] idBytes = new byte[in.readInt()];
                    in.readFully(idBytes);
                    String id = new String(idBytes);

                    System.out.println("Got packet id '" + id + "'");

                    ChatPacket packet = this.connection.getState().createPacket(id);
                    if (packet == null) {
                        throw new RuntimeException("Bad packet received from server");
                    }
                    packet.read(in);

                    packet.onPacket(this.connection);
                }

                // Write
                if (this.connection.outboundPackets.size() > 0) {
                    DataOutputStream out = this.connection.out;
                    for (ChatPacket packet : this.connection.outboundPackets) {
                        String id = this.connection.getState().getPacketName(packet.getClass());

                        System.out.println("Sending packet id '" + id + "'");

                        if (id == null) {
                            throw new RuntimeException("Bad packet sent to server");
                        }
                        byte[] idBytes = id.getBytes();
                        out.writeInt(idBytes.length);
                        out.write(idBytes);
                        packet.write(out);
                    }
                    this.connection.outboundPackets.clear();
                }

                Thread.sleep(10L);
            }

        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        finally {
            this.running = false;
            this.connection.disconnecting = false;

            if (this.connection.in != null) {
                try {
                    this.connection.in.close();
                }
                catch (IOException e) {}
            }
            if (this.connection.out != null) {
                try {
                    this.connection.out.close();
                }
                catch (IOException e) {}
            }
            if (this.connection.socket != null) {
                try {
                    this.connection.socket.close();
                }
                catch (IOException e) {}
            }
        }
    }

}
