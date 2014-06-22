
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
        while (this.running || this.connection.disconnecting) {
            try {
                // Read
                DataInputStream in = this.connection.in;
                while (in.available() > 0) {
                    byte[] idBytes = new byte[in.readInt()];
                    in.readFully(idBytes);
                    String id = new String(idBytes);

                    ChatPacket packet = this.connection.getState().createPacket(id);
                    if (packet == null) {
                        throw new RuntimeException("Bad packet received from server");
                    }
                    packet.read(in);
                    this.connection.inboundPackets.add(packet);
                }

                // Write
                if (this.connection.outboundPackets.size() > 0) {
                    DataOutputStream out = this.connection.out;
                    for (ChatPacket packet : this.connection.outboundPackets) {
                        String id = this.connection.getState().getPacketName(packet.getClass());
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

                if (this.connection.inboundPackets.size() > 0) {
                    for (ChatPacket packet : this.connection.inboundPackets) {
                        packet.onPacket(this.connection);
                    }
                    this.connection.inboundPackets.clear();
                }

                Thread.sleep(10L);
            }
            catch (InterruptedException e) {}
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
            finally {
                try {
                    this.connection.in.close();
                }
                catch (IOException e) {}
                try {
                    this.connection.out.close();
                }
                catch (IOException e) {}
                try {
                    this.connection.socket.close();
                }
                catch (IOException e) {}
            }
            if (this.connection.disconnecting) {
                this.running = false;
            }
        }

        this.connection.disconnecting = false;
    }

}
