
package me.heldplayer.mods.chat.impl.config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
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
    private KeyPair keypair;

    @Override
    public void load(File saveDir) {
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File configFile = new File(saveDir, "config.cfg");

        if (!configFile.exists() || !configFile.isFile()) {
            try {
                configFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            this.configuration = new ServerConfiguration();
        }
        else {
            JsonReader reader = null;
            try {
                reader = new JsonReader(new FileReader(configFile));
                reader.setLenient(true);
                JsonElement element = Streams.parse(reader);
                this.configuration = new ServerConfiguration(element.getAsJsonObject());
            }
            catch (IOException e) {
                throw new RuntimeException("Failed reading configuration", e);
            }
            catch (IllegalStateException e) {
                this.configuration = new ServerConfiguration();
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {}
                }
            }
        }

        File keyFile = new File(saveDir, "key");

        if (!keyFile.exists() || !keyFile.isFile()) {
            try {
                keyFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            this.keypair = KeyUtils.keyGen.generateKeyPair();
            System.out.println("Generated KeyPair");
        }
        else {
            DataInputStream in = null;
            try {
                in = new DataInputStream(new FileInputStream(keyFile));

                byte[] data = new byte[in.readInt()];
                in.read(data);

                this.keypair = KeyUtils.deserializeKey(data);
            }
            catch (IOException e) {
                throw new RuntimeException("Failed reading keys", e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    @Override
    public void save(File saveDir) {
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        File configFile = new File(saveDir, "config.cfg");

        if (!configFile.exists() || !configFile.isFile()) {
            try {
                configFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        JsonWriter writer = null;
        try {
            JsonElement element = this.configuration.toJson();
            writer = new JsonWriter(new FileWriter(configFile));
            writer.setIndent("  ");
            ConfigurationProvider.gson.toJson(element, writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed saving configuration", e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        File keyFile = new File(saveDir, "key");

        if (!keyFile.exists() || !keyFile.isFile()) {
            try {
                keyFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(keyFile));

            byte[] data = KeyUtils.serializeKey(this.keypair);
            out.writeInt(data.length);
            out.write(data);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed saving keys", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {}
            }
        }
    }

    @Override
    public UUID getServerUUID() {
        return this.configuration.getUuid();
    }

    @Override
    public List<ServerEntry> getServers() {
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
        return this.keypair.getPrivate();
    }

    @Override
    public PublicKey getPublicKey() {
        return this.keypair.getPublic();
    }

}
