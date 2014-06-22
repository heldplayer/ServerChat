
package me.heldplayer.chat.framework.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;

import org.apache.commons.io.input.ReaderInputStream;

public final class KeyUtils {

    private KeyUtils() {}

    private static SecureRandom rand = new SecureRandom();
    public static KeyPairGenerator keyGen;

    static {
        try {
            KeyUtils.keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            KeyUtils.keyGen.initialize(1024, random);
        }
        catch (Throwable e) {
            throw new RuntimeException("Failled getting KeyPairGenerator", e);
        }
    }

    public static String getRandomChallenge() {
        return new BigInteger(130, KeyUtils.rand).toString(32);
    }

    public static byte[] serializeKey(KeyPair key) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(key);
            byte[] res = bos.toByteArray();

            oos.close();
            bos.close();

            return res;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException e) {}
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {}
            }
        }
    }

    public static KeyPair deserializeKey(byte[] data) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();

            ois.close();
            bis.close();

            return (KeyPair) obj;
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException e) {}
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {}
            }
        }
    }

    public static byte[] getSignature(PrivateKey key, String input) {
        return KeyUtils.getSignature(key, new ReaderInputStream(new StringReader(input)));
    }

    public static boolean verifySignature(PublicKey key, String input, byte[] sig) {
        return KeyUtils.verifySignature(key, new ReaderInputStream(new StringReader(input)), sig);
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
