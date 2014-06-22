
package me.heldplayer.mods.chat.impl.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import me.heldplayer.chat.framework.config.IServerConfiguration;
import me.heldplayer.chat.framework.config.ServerEntry;
import me.heldplayer.chat.framework.util.KeyUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ConfigurationProvider implements IServerConfiguration {

    private static Gson gson = new Gson();

    private ServerConfiguration configuration;

    @Override
    public void load(File file) {
        if (!file.exists()) {
            this.configuration = new ServerConfiguration();
            this.save(file);
        }
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
            reader.setLenient(true);
            JsonElement element = Streams.parse(reader);
            this.configuration = new ServerConfiguration(element.getAsJsonObject());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed reading configuration", e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {}
            }
        }

        if (this.configuration.getKeyPair() == null) {
            System.out.println("Generated KeyPair");
            this.configuration.setKeyPair(KeyUtils.keyGen.generateKeyPair());
        }
    }

    @Override
    public void save(File file) {
        try {
            ConfigurationProvider.gson.toJson(this.configuration.toJson(), new JsonWriter(new FileWriter(file)));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed saving configuration", e);
        }
    }

    @Override
    public UUID getServerUUID() {
        return this.configuration.getUuid();
    }

    @Override
    public ServerEntry[] getServers() {
        return this.configuration.getServerEntries();
    }

    @Override
    public String getHost() {
        return this.configuration.getHost();
    }

    @Override
    public int getPort() {
        return this.configuration.getPort();
    }

    @Override
    public boolean isOfflineMode() {
        // FIXME
        return false;
    }

    @Override
    public PrivateKey getPrivateKey() {
        return this.configuration.keyPair.getPrivate();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.configuration.keyPair.getPublic();
    }

}
