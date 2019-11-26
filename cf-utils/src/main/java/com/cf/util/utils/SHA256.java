package com.cf.util.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    public static byte[] sha(String text) {
        // 是否是有效字符串
        if (text != null && text.length() > 0) {
            try {
                // SHA 加密开始
                // 创建加密对象 并傳入加密類型
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                // 传入要加密的字符串
                messageDigest.update(text.getBytes());
                // 得到 byte 類型结果
                return messageDigest.digest();

            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(sha("dfjkdfjdjfd").toString().toUpperCase());

    }

}
