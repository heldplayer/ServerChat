
package me.heldplayer.chat.framework.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;

public abstract class ChatPacket {

    public abstract void write(DataOutputStream out) throws IOException;

    public abstract void read(DataInputStream in) throws IOException;

    public abstract void onPacket(LocalServer connection);

    public abstract void onPacket(RemoteServer connection);

}
