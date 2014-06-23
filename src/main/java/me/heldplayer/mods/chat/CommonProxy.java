
package me.heldplayer.mods.chat;

import java.io.File;
import java.io.IOException;

import me.heldplayer.chat.framework.ConnectionsList;
import me.heldplayer.mods.chat.impl.config.ConfigurationProvider;
import net.specialattack.forge.core.SpACoreProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy extends SpACoreProxy {

    public ConnectionsList connections;

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    public void initializeServerConnection() {
        if (this.connections != null) {
            this.connections.stopListening();
        }
        this.connections = new ConnectionsList(new ConfigurationProvider(), new File("./config/" + Objects.MOD_ID + ""));
        try {
            this.connections.startListening();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerConnection() {
        if (this.connections != null) {
            this.connections.stopListening();
        }
    }

}
