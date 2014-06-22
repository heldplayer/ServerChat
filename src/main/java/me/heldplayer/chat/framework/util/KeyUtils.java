
package me.heldplayer.chat.framework.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.apache.commons.io.input.ReaderInputStream;

public final class KeyUtils {

    private KeyUtils() {}

    public static byte[] getSignature(PrivateKey key, String input) {
        return getSignature(key, new ReaderInputStream(new StringReader(input)));
    }

    public static boolean verifySignature(PublicKey key, String input, byte[] sig) {
        return verifySignature(key, new ReaderInputStream(new StringReader(input)), sig);
    }

    public static byte[] getSignature(PrivateKey key, InputStream input) {
        try {
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initSign(key);

            BufferedInputStream in = new BufferedInputStream(input);
            byte[] buffer = new byte[1024];
            while (in.available() != 0) {
                int length = in.read(buffer);
                signature.update(buffer, 0, length);
            }
            in.close();

            return signature.sign();
        }
        catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifySignature(PublicKey key, InputStream input, byte[] sig) {
        try {
            Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
            signature.initVerify(key);

            BufferedInputStream in = new BufferedInputStream(input);
            byte[] buffer = new byte[1024];
            while (in.available() != 0) {
                int length = in.read(buffer);
                signature.update(buffer, 0, length);
            }
            in.close();

            return signature.verify(sig);
        }
        catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

}
