package com.hello.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */
public class PasswordUtil {
    
    private static final String ALGORITHM = "SHA-256";
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * 生成盐值
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 加密密码
     */
    public static String encryptPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }
    
    /**
     * 验证密码
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String newHashedPassword = encryptPassword(password, salt);
        return newHashedPassword.equals(hashedPassword);
    }
    
    /**
     * 加密密码（使用随机盐值）
     */
    public static String encryptPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = encryptPassword(password, salt);
        return salt + ":" + hashedPassword;
    }
    
    /**
     * 验证密码（从组合字符串中提取盐值）
     */
    public static boolean verifyPassword(String password, String saltAndHash) {
        if (saltAndHash == null || !saltAndHash.contains(":")) {
            return false;
        }
        String[] parts = saltAndHash.split(":", 2);
        if (parts.length != 2) {
            return false;
        }
        String salt = parts[0];
        String hashedPassword = parts[1];
        return verifyPassword(password, salt, hashedPassword);
    }
} 