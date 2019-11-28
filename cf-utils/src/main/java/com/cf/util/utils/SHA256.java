package com.cf.util.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {


    /**
     * @param decript 要加密的字符串
     * @return 加密的字符串
     * SHA1加密
     */
    public  static String sha256(String decript) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(sha256("").toString().toUpperCase());

    }

}
