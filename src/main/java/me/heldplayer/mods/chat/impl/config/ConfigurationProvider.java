
package me.heldplayer.mods.chat.impl.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.UUID;

import me.heldplayer.chat.framework.config.IServerConfiguration;
import me.heldplayer.chat.framework.config.ServerEntry;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

public class ConfigurationProvider implements IServerConfiguration {

    private static Gson gson = new Gson();
    private static KeyPairGenerator keyGen;

    static {
        try {
            keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(1024, random);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failled getting KeyPairGenerator", e);
        }
    }

    private ServerConfiguration configuration;

    @Override
    public void load(File file) {
        try {
            configuration = gson.fromJson(new FileReader(file), ServerConfiguration.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed reading configuration", e);
        }

        if (configuration.getKeyPair() == null) {
            System.out.println("Generated KeyPair");
            configuration.setKeyPair(keyGen.generateKeyPair());
        }
    }

    @Override
    public void save(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                gson.toJson(gson.toJsonTree(configuration), new JsonWriter(new FileWriter(file)));
            }
            catch (IOException e) {
                throw new RuntimeException("Failed saving configuration", e);
            }
        }
    }

    @Override
    public UUID getServerUUID() {
        return configuration.getUuid();
    }

    @Override
    public ServerEntry[] getServers() {
        return configuration.getServerEntries();
    }

    @Override
    public String getHost() {
        return configuration.getHost();
    }

    @Override
    public int getPort() {
        return configuration.getPort();
    }

    @Override
    public boolean isOfflineMode() {
        // FIXME
        return false;
    }

}
