
package me.heldplayer.mods.chat.client;

import me.heldplayer.mods.chat.CommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void initializeServerConnection() {
        // NOOP on client
        // FIXME
        super.initializeServerConnection();
    }

    @Override
    public void closeServerConnection() {
        // NOOP on client
        // FIXME
        super.closeServerConnection();
    }

}
