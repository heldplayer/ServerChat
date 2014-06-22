
package me.heldplayer.chat.framework.auth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public final class ServerAuthentication {

    private ServerAuthentication() {}

    public static boolean verifyIdentity(UUID uuid, String challenge, byte[] signature) {
        OutputStream out = null;
        InputStream in = null;

        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://serverchat@dsiwars.specialattack.net:7611/serverchat/verify").openConnection();
            con.setRequestProperty("Content-Type", "application/octet-stream");
            con.setRequestProperty("Content-Length", Integer.toString(signature.length));
            con.setDoOutput(true);

            out = con.getOutputStream();
            out.write(signature);

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

            in = con.getInputStream();
            byte[] response = new byte[length];
            int read = in.read(response);
            if (read != length) {
                return false;
            }

            return Arrays.equals(signature, response);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {}
            try {
                in.close();
            }
            catch (IOException e) {}
        }
        return false;
    }

}
