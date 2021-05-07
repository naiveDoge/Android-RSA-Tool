package cn.hotdoge.rsatool.methods;


import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.spec.SecretKeySpec;

public class RSA {

    public PrivateKey readPrivateKeyFromString(String key) throws java.security.NoSuchAlgorithmException, java.security.spec.InvalidKeySpecException {
        key = key.replace("-----BEGIN RSA PRIVATE KEY-----\n", "");
        key = key.replace("-----END RSA PRIVATE KEY-----", "");

        byte[] keyDecoded = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyDecoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(keySpec);
    }

    public String generateNewPrivateKey() {
        SecureRandom secureRandom = new SecureRandom();
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, secureRandom);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            byte[] privateKey = keyPair.getPrivate().getEncoded();

            //Log.w("KEY_PUB_FROM_GENERATE", new String(Base64.encode(keyPair.getPublic().getEncoded(), Base64.DEFAULT)));

            return new String(Base64.encode(privateKey, Base64.DEFAULT));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPublicKeyFromPrivateKey(String privateKey) {
        PrivateKey privateKeyObj =  getPrivateKeyObjFromString(privateKey);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            try {
                RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKeyObj, RSAPrivateKeySpec.class);
                RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(rsaPrivateKeySpec.getModulus(), BigInteger.valueOf(65537));
                PublicKey publicKey = keyFactory.generatePublic(rsaPublicKeySpec);

                return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "";
    }

    static PrivateKey getPrivateKeyObjFromString(String key) {
        try {
            byte[] keyEncoded = Base64.decode(key, Base64.DEFAULT);

            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyEncoded);
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                try {
                    return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }
}
