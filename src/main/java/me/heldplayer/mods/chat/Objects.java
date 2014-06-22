
package me.heldplayer.mods.chat;

import net.specialattack.forge.core.ModInfo;

import org.apache.logging.log4j.Logger;

/**
 * MystNEIPlugin mod Objects
 * 
 * @author heldplayer
 * 
 */
public final class Objects {

    public static final String MOD_ID = "serverchat";
    public static final String MOD_NAME = "ServerChat";
    public static final String CLIENT_PROXY = "me.heldplayer.mods.chat.client.ClientProxy";
    public static final String SERVER_PROXY = "me.heldplayer.mods.chat.CommonProxy";

    public static final ModInfo MOD_INFO = new ModInfo(Objects.MOD_ID, Objects.MOD_NAME);

    public static Logger log;

}
