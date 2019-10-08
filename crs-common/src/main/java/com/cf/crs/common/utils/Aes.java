package com.cf.crs.common.utils;

/**
 * Created by ww on 2017/4/19.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class Aes {

    private static final Logger log = LogManager.getLogger(Aes.class);

    public static String encrypt(String input, String key) {
        byte[] crypted = null;
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            log.info("加密失败", e);
        }
        Base64.Encoder encoder = java.util.Base64.getEncoder();

        return encoder.encodeToString(crypted);
    }

    public static String decrypt(String input, String key) {
        byte[] output = null;
        try {
            java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(decoder.decode(input));
            if (output == null || output.length < 1) {
                return null;
            }
            return new String(output, "utf-8");
        } catch (Exception e) {
            log.error("解密失败", e);
//            System.out.println(e.toString());
            return null;
        }
//        return new String(output);
    }

    public static void main(String[] args) throws Exception {
        String key = "Ee82Pzeg579BwQYG";
        String data = "123qwe";
        System.out.println(Aes.decrypt("h+Y6010MNPmIf6C+lwkhbA==", key));

        //System.out.println(Aes.encrypt("h+Y6010MNPmIf6C+lwkhbA==", "81092197,1"));
//        System.out.println(Aes.encryptSrc("81092197,1", key));

        System.out.println(Aes.encrypt("84000866,1", "FmbVKoOEUyJkIJmB"));

        System.out.println(Aes.decrypt("Ea2TY8n24ahl82lTzUrrqg==", "FmbVKoOEUyJkIJmB"));

        //System.out.println(Aes.encryptSrc(Aes.encrypt(data, key), key));
    }

    /**
     * @param sSrc     原文
     * @param password 密钥：Ee82Pzeg579BwQYG
     * @return
     * @throws Exception
     */
    public static String encryptSrc(String sSrc, String password) throws Exception {
        if (password == null) {
            return null;
        }
        // 判断Key是否为16位
        if (password.length() != 16) {
            return null;
        }
        byte[] crypted = null;

        try {
            SecretKeySpec skey = new SecretKeySpec(password.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(sSrc.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        if (crypted == null) {
            return null;
        }else{

            return Base64Utils.encodeToString(crypted);
        }
    }


}
