
package me.heldplayer.chat.framework;

import java.util.UUID;

public class RemoteServer extends Server {

    public final UUID uuid;

    public RemoteServer(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

}
