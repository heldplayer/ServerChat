
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
        if (this.connection.isDisconnecting()) {
            this.stop();
        }

        try {
            if (this.connection.serverIO.isClosed()) {
                this.connection.log.info("Connection lost");

                return;
            }

            if (this.connection.isDisconnecting()) {
                this.stop();
                this.connection.setDisconnecting(false);
            }

            // Read
            DataInputStream in = this.connection.serverIO.getIn();
            while (in.available() > 0) {
                String id = in.readUTF();
                byte[] data = new byte[in.readInt()];
                in.readFully(data);

                this.connection.log.trace("< Got packet id '%s' (%s bytes)", id, data.length);

                ChatPacket packet = this.connection.createPacket(id);
                if (packet == null) {
                    throw new RuntimeException(String.format("Bad packet in inbound (Id: %s; State: %s)", id, this.connection.getState()));
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

                    this.connection.log.trace("> Sending packet id '%s' (%s bytes)", id, this.dos.size());

                    if (id == null) {
                        throw new RuntimeException(String.format("Bad packet in outbound (Type: %s; State: %s)", packet.getClass(), this.connection.getState()));
                    }
                    out.writeUTF(id);

                    out.writeInt(this.boas.size());
                    out.write(this.boas.toByteArray(), 0, this.boas.size());
                    this.boas.reset();
                }
                this.connection.getOutboundPacketsQueue().clear();
            }
        }
        catch (Throwable e) {
            this.connection.disconnectServer("Error in connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean shouldRun() {
        return super.shouldRun() || this.connection.isDisconnecting();
    }

    @Override
    public void stop() {
        super.stop();
        this.connection.serverIO.close();

        try {
            this.dos.close();
        }
        catch (IOException e) {}
    }

}
