
package me.heldplayer.chat.framework.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import me.heldplayer.chat.framework.LocalServer;
import me.heldplayer.chat.framework.RemoteServer;

public class PacketPing extends ChatPacket {

    public PacketPing() {}

    @Override
    public void write(DataOutputStream out) throws IOException {}

    @Override
    public void read(DataInputStream in) throws IOException {}

    @Override
    public void onPacket(LocalServer connection) {}

    @Override
    public void onPacket(RemoteServer connection) {}

}
