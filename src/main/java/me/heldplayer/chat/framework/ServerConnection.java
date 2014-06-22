
package me.heldplayer.chat.framework;

import java.net.Socket;

public class ServerConnection {

    private Socket socket;

    public static enum ConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        RECONNECTING,
        ERRORED;
    }

}
