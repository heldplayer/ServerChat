
package me.heldplayer.chat.framework.test;

import java.io.File;
import java.io.IOException;

import me.heldplayer.chat.framework.ConnectionsList;
import me.heldplayer.mods.chat.impl.config.ConfigurationProvider;

public class ConnectionTest {

    static ConnectionsList connections = null;

    public static void main(String[] args) {
        Thread consoleReader = new Thread(new RunnableConsoleReader(), "Console reader Thread");
        consoleReader.setDaemon(true);
        consoleReader.start();

        ConnectionTest.connections = new ConnectionsList(new ConfigurationProvider(), new File("."));
        try {
            ConnectionTest.connections.startListening();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
