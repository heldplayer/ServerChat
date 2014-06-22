
package me.heldplayer.mods.chat.impl.config;

import java.security.KeyPair;
import java.util.Iterator;
import java.util.UUID;

import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.util.KeyUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

public class ServerConfiguration {

    @SerializedName("uuid")
    UUID uuid;
    @SerializedName("server-entries")
    ServerEntry[] serverEntries;
    @SerializedName("host")
    String host;
    @SerializedName("port")
    int port;
    @SerializedName("key-pair")
    KeyPair keyPair;

    public ServerConfiguration() {}

    public ServerConfiguration(JsonObject element) {
        this.uuid = UUID.fromString(element.get("uuid").getAsString());
        this.host = element.get("host").getAsString();
        this.port = element.get("port").getAsInt();

        JsonArray keyPair = element.get("key-pair").getAsJsonArray();
        byte[] data = new byte[keyPair.size()];
        Iterator<JsonElement> i = keyPair.iterator();
        int index = 0;
        while (i.hasNext()) {
            JsonElement e = i.next();
            data[index++] = e.getAsByte();
        }
        this.keyPair = KeyUtils.deserializeKey(data);

        // TODO: load servers
    }

    public JsonElement toJson() {
        JsonObject result = new JsonObject();
        result.add("uuid", new JsonPrimitive(this.uuid.toString()));
        result.add("host", new JsonPrimitive(this.host));
        result.add("port", new JsonPrimitive(this.port));

        JsonArray keyPair = new JsonArray();
        byte[] data = KeyUtils.serializeKey(this.keyPair);
        for (byte part : data) {
            keyPair.add(new JsonPrimitive(part));
        }
        result.add("key-pair", keyPair);

        // TODO: save servers

        return result;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public ServerEntry[] getServerEntries() {
        return this.serverEntries;
    }

    public void setServerEntries(ServerEntry[] serverEntries) {
        this.serverEntries = serverEntries;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("port must be between 0 and 65535");
        }
        this.port = port;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

}
