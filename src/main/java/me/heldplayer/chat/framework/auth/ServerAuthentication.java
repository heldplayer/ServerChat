
package me.heldplayer.chat.framework.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public final class ServerAuthentication {

    public static final boolean DEBUG = true;

    private ServerAuthentication() {}

    public static boolean verifyIdentity(UUID uuid, String challenge, byte[] signature) {
        if (ServerAuthentication.DEBUG) {
            return true;
        }
        OutputStream out = null;
        InputStream in = null;

        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://serverchat@dsiwars.specialattack.net:7611/serverchat/verify").openConnection();
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("Content-Length", Integer.toString(signature.length));
            con.setDoOutput(true);

            out = con.getOutputStream();
            out.write(signature);

            in = con.getInputStream();

            con.connect();
            int length = -1;
            for (Entry<String, List<String>> entry : con.getHeaderFields().entrySet()) {
                if (entry.getKey().equalsIgnoreCase("Content-Length")) {
                    length = Integer.parseInt(entry.getValue().get(0));
                    break;
                }
            }
            if (length == -1) {
                return false;
            }

            byte[] response = new byte[length];
            int read = in.read(response);
            if (read != length) {
                return false;
            }

            return Arrays.equals(signature, response);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {}
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
    }

}
