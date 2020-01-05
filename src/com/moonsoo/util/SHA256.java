package com.moonsoo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    public static String getSha256(String input){
        StringBuffer result = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] salt = "Hello! it is salt.".getBytes();
            digest.reset();
            digest.update(salt);
            byte[] chars = digest.digest(input.getBytes("UTF-8"));
            for(int i = 0; i < chars.length; i++) {
                String hex = Integer.toHexString(0xff & chars[i]);
                result.append(hex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
