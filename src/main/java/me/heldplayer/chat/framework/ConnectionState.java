
package me.heldplayer.chat.framework;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.auth.PacketAuthChallenge;

public enum ConnectionState {

    DISCONNECTED,
    CONNECTING,
    AUTHENTICATING {
        {
            this.registerPacket("authChallenge", PacketAuthChallenge.class);
        }
    },
    CONNECTED {
        {
            //this.registerPacket(null, null);
        }
    },
    RECONNECTING,
    ERRORED;

    private HashMap<String, Class<? extends ChatPacket>> namesToPackets = new HashMap<String, Class<? extends ChatPacket>>();

    ConnectionState registerPacket(String name, Class<? extends ChatPacket> clazz) {
        if (this.namesToPackets.containsKey(name)) {
            throw new IllegalArgumentException(String.format("%s is already registered for %s when trying to register it as %s", this.namesToPackets.get(name), name, clazz));
        }

        this.namesToPackets.put(name, clazz);

        return this;
    }

    public ChatPacket createPacket(String name) {
        Class<? extends ChatPacket> clazz = this.namesToPackets.get(name);

        if (clazz == null) {
            return null;
        }

        try {
            Constructor<? extends ChatPacket> constructor = clazz.getDeclaredConstructor();
            return constructor.newInstance();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

}
