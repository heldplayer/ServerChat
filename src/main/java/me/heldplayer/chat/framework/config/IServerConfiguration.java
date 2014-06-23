
package me.heldplayer.chat.framework.config;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;

public interface IServerConfiguration {

    void load(File file);

    void save(File file);

    UUID getServerUUID();

    List<ServerEntry> getServers();

    String getHost();

    int getPort();

    boolean isOfflineMode();

    PrivateKey getPrivateKey();

    PublicKey getPublicKey();

}
