
package me.heldplayer.chat.framework;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import me.heldplayer.chat.framework.packet.ChatPacket;
import me.heldplayer.chat.framework.packet.auth.PacketAuthChallenge;
import me.heldplayer.chat.framework.packet.auth.PacketAuthChallengeResponse;
import me.heldplayer.chat.framework.packet.auth.PacketAuthenticationComplete;
import me.heldplayer.chat.framework.packet.auth.PacketDisconnect;

public enum ConnectionState {

    DISCONNECTED,
    CONNECTING {
        {
            this.registerPacket("disconnect", PacketDisconnect.class);
        }
    },
    AUTHENTICATING {
        {
            this.registerPacket("disconnect", PacketDisconnect.class);
            this.registerPacket("authChallenge", PacketAuthChallenge.class);
            this.registerPacket("authChallengeResponse", PacketAuthChallengeResponse.class);
        }
    },
    AUTHENTICATED {
        {
            this.registerPacket("disconnect", PacketDisconnect.class);
            this.registerPacket("authChallenge", PacketAuthChallenge.class);
            this.registerPacket("authChallengeResponse", PacketAuthChallengeResponse.class);
            this.registerPacket("authComplete", PacketAuthenticationComplete.class);
        }
    },
    CONNECTED {
        {
            this.registerPacket("disconnect", PacketDisconnect.class);
            this.registerPacket("authComplete", PacketAuthenticationComplete.class);
        }
    },
    RECONNECTING,
    ERRORED;

    private HashMap<String, Class<? extends ChatPacket>> namesToPackets = new HashMap<String, Class<? extends ChatPacket>>();
    private HashMap<Class<? extends ChatPacket>, String> packetsToNames = new HashMap<Class<? extends ChatPacket>, String>();

    ConnectionState registerPacket(String name, Class<? extends ChatPacket> clazz) {
        if (this.namesToPackets.containsKey(name)) {
            throw new IllegalArgumentException(String.format("%s is already registered for %s when trying to register it as %s", this.namesToPackets.get(name), name, clazz));
        }

        this.namesToPackets.put(name, clazz);
        this.packetsToNames.put(clazz, name);

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

    public String getPacketName(Class<? extends ChatPacket> clazz) {
        return this.packetsToNames.get(clazz);
    }
}
