
package me.heldplayer.chat.framework.config;

import java.io.File;
import java.util.UUID;

public interface IServerConfiguration {

    void load(File file);

    void save(File file);

    UUID getServerUUID();

    ServerEntry[] getServers();

    String getHost();

    int getPort();

    boolean isOfflineMode();

}
