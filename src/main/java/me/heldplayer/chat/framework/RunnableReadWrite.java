
package me.heldplayer.chat.framework;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.wrap.RunnableStoppable;

public final class RunnableReadWrite extends RunnableStoppable {

    private final LocalServer connection;

    private ByteArrayOutputStream boas = new ByteArrayOutputStream(32768);
    private DataOutputStream dos = new DataOutputStream(this.boas); // Like a boas

    protected RunnableReadWrite(LocalServer connection) {
        this.connection = connection;
    }

    @Override
    public void doRun() {
        try {
            if (this.connection.serverIO.isClosed()) {
                System.out.println("Connection lost");

                return;
            }

            if (this.connection.isDisconnecting()) {
                this.stop();
                this.connection.setDisconnecting(false);
            }

            // Read
            DataInputStream in = this.connection.serverIO.getIn();
            while (in.available() > 0) {
                byte[] idBytes = new byte[in.readInt()];
                in.readFully(idBytes);
                String id = new String(idBytes);
                byte[] data = new byte[in.readInt()];
                in.readFully(data);

                System.out.println(" < Got packet id '" + id + "' (" + data.length + " bytes)");

                ChatPacket packet = this.connection.createPacket(id);
                if (packet == null) {
                    throw new RuntimeException("Bad packet in inbound (Id: " + id + "; State: " + this.connection.getState() + ")");
                }
                DataInputStream dis;
                packet.read(dis = new DataInputStream(new ByteArrayInputStream(data)));
                dis.close(); // Close dis

                packet.onPacket(this.connection);
            }

            // Write
            if (this.connection.getOutboundPacketsQueue().size() > 0) {
                DataOutputStream out = this.connection.serverIO.getOut();
                for (ChatPacket packet : this.connection.getOutboundPacketsQueue()) {
                    String id = this.connection.getId(packet);
                    packet.write(this.dos);

                    System.out.println(" > Sending packet id '" + id + "' (" + this.dos.size() + " bytes)");

                    if (id == null) {
                        throw new RuntimeException("Bad packet in outbound (Type: " + packet.getClass() + "; State: " + this.connection.getState() + ")");
                    }
                    byte[] idBytes = id.getBytes();
                    out.writeInt(idBytes.length);
                    out.write(idBytes);

                    out.writeInt(this.boas.size());
                    out.write(this.boas.toByteArray(), 0, this.boas.size());
                    this.boas.reset();
                }
                this.connection.getOutboundPacketsQueue().clear();
            }
        }
        catch (Throwable e) {
            throw new RuntimeException(e);
        }
        finally {
            this.stop();
            this.connection.setDisconnecting(false);
            this.connection.serverIO.close();

            try {
                this.dos.close();
            }
            catch (IOException e) {}
        }
    }

    @Override
    public boolean shouldRun() {
        return super.shouldRun() || this.connection.isDisconnecting();
    }

}
