
package me.heldplayer.chat.framework.packet;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class ChatPacket {

    private static HashMap<String, Class<? extends ChatPacket>> namesToPackets = new HashMap<String, Class<? extends ChatPacket>>();

    public static void registerPacket(String name, Class<? extends ChatPacket> clazz) {
        if (namesToPackets.containsKey(name)) {
            throw new IllegalArgumentException(String.format("%s is already registered for %s when trying to register it as %s", namesToPackets.get(name), name, clazz));
        }

        namesToPackets.put(name, clazz);
    }

    public static ChatPacket createPacket(String name) {
        Class<? extends ChatPacket> clazz = namesToPackets.get(name);

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
