
package me.heldplayer.chat.framework.test;

import java.io.IOException;

import me.heldplayer.chat.framework.ConnectionsList;
import me.heldplayer.mods.chat.impl.config.ConfigurationProvider;

public class ConnectionTest {

    public static void main(String[] args) {
        ConnectionsList connections = new ConnectionsList(new ConfigurationProvider());
        try {
            connections.startListening();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
