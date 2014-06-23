
package me.heldplayer.mods.chat.impl.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import me.heldplayer.chat.framework.config.ServerEntry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

public class ServerConfiguration {

    @SerializedName("uuid")
    private UUID uuid;
    @SerializedName("server-entries")
    private List<ServerEntry> serverEntries;
    @SerializedName("host")
    private String host;
    @SerializedName("port")
    private int port;

    public ServerConfiguration() {
        this.uuid = UUID.randomUUID();
        this.serverEntries = new ArrayList<ServerEntry>();
        this.host = "";
        this.port = 37606;
    }

    public ServerConfiguration(JsonObject element) {
        this.uuid = UUID.fromString(element.get("uuid").getAsString());
        this.host = element.get("host").getAsString();
        this.port = element.get("port").getAsInt();

        JsonArray serverEntries = element.get("server-entries").getAsJsonArray();
        this.serverEntries = new ArrayList<ServerEntry>();
        Iterator<JsonElement> i = serverEntries.iterator();
        while (i.hasNext()) {
            JsonObject entryObj = i.next().getAsJsonObject();
            ServerEntry entry = new ServerEntry();
            entry.setIp(entryObj.get("ip").getAsString());
            entry.setPort(entryObj.get("port").getAsInt());
            entry.setUuid(UUID.fromString(entryObj.get("uuid").getAsString()));
            this.serverEntries.add(entry);
        }
    }

    public JsonElement toJson() {
        JsonObject result = new JsonObject();
        result.add("uuid", new JsonPrimitive(this.uuid.toString()));
        result.add("host", new JsonPrimitive(this.host));
        result.add("port", new JsonPrimitive(this.port));

        JsonArray serverEntries = new JsonArray();
        for (ServerEntry entry : this.serverEntries) {
            JsonObject entryObj = new JsonObject();
            entryObj.add("ip", new JsonPrimitive(entry.getIp()));
            entryObj.add("port", new JsonPrimitive(entry.getPort()));
            entryObj.add("uuid", new JsonPrimitive(entry.getUuid().toString()));
            serverEntries.add(entryObj);
        }
        result.add("server-entries", serverEntries);

        return result;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<ServerEntry> getServerEntries() {
        return this.serverEntries;
    }

    public void setServerEntries(List<ServerEntry> serverEntries) {
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

}
