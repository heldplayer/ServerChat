
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.ConnectionState;

/**
 * Sent when a server disconnects, indicates that an attempt to reconnect should
 * not be made
 */
public class PacketAuthComplete extends ChatPacket {

    public PacketAuthComplete() {}

    @Override
    public void write(DataOutputStream out) throws IOException {}

    @Override
    public void read(DataInputStream in) throws IOException {}

    @Override
    public void onPacket(ServerConnection connection) {
        if (connection.getState() == ConnectionState.AUTHENTICATED) {
            try {
                connection.setState(ConnectionState.CONNECTED);
            }
            catch (AuthenticationException e) {
                connection.kickServer(e.getMessage());
                return;
            }
            connection.addPacket(new PacketAuthComplete());
            connection.connectionsList.synchronizeData(connection);
            connection.connectionsList.save();
        }
    }

}
