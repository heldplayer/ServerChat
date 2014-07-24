package me.heldplayer.mods.chat;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import net.specialattack.forge.core.ModInfo;
import net.specialattack.forge.core.SpACoreMod;
import net.specialattack.forge.core.SpACoreProxy;
import net.specialattack.forge.core.config.Config;
import net.specialattack.forge.core.packet.PacketHandler;

@Mod(modid = Objects.MOD_ID, name = Objects.MOD_NAME)
public class ModServerChat extends SpACoreMod {

    @Instance(value = Objects.MOD_ID)
    public static ModServerChat instance;
    @SidedProxy(clientSide = Objects.CLIENT_PROXY, serverSide = Objects.SERVER_PROXY)
    public static CommonProxy proxy;

    public static PacketHandler packetHandler;

    //// SpACore Objects
    // Integrator references
    //public static ConfigValue<Boolean> hideTechnicalBlocks;

    @Override
    @SuppressWarnings("unchecked")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Objects.log = event.getModLog();

        ModServerChat.packetHandler = new PacketHandler("ServerChat");

        // Config
        //hideTechnicalBlocks = new ConfigValue<Boolean>("hideTechnicalBlocks", Configuration.CATEGORY_GENERAL, Side.CLIENT, Boolean.TRUE, "Should technical blocks be hidden?");
        this.config = new Config(event.getSuggestedConfigurationFile());
        //this.config.addConfigKey(hideTechnicalBlocks);

        super.preInit(event);
    }

    @Override
    public ModInfo getModInfo() {
        return Objects.MOD_INFO;
    }

    @Override
    public SpACoreProxy getProxy() {
        return ModServerChat.proxy;
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        ModServerChat.proxy.initializeServerConnection();
    }

    @EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        ModServerChat.proxy.closeServerConnection();
    }

}
