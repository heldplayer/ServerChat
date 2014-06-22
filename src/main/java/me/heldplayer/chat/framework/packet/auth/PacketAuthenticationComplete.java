
package me.heldplayer.chat.framework.packet.auth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.ConnectionState;
import me.heldplayer.chat.framework.ServerConnection;
import me.heldplayer.chat.framework.auth.AuthenticationException;
import me.heldplayer.chat.framework.packet.ChatPacket;

/**
 * Sent when a server disconnects, indicates that an attempt to reconnect should
 * not be made
 */
public class PacketAuthenticationComplete extends ChatPacket {

    public PacketAuthenticationComplete() {}

    @Override
    public void write(DataOutputStream out) throws IOException {}

    @Override
    public void read(DataInputStream in) throws IOException {}

    @Override
    public void onPacket(ServerConnection connection) {
        if (connection.getState() == ConnectionState.AUTHENTICATING) {
            try {
                connection.setState(ConnectionState.CONNECTED);
            }
            catch (AuthenticationException e) {
                connection.disconnect(e.getMessage());
                return;
            }
            connection.addPacket(new PacketAuthenticationComplete());
            // TODO: synchronize information
        }
    }

}
