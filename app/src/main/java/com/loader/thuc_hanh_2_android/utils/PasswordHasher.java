package com.loader.thuc_hanh_2_android.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Ma hoa mat khau bang SHA-256, tra ve chuoi hex 64 ky tu. */
public final class PasswordHasher {

    private PasswordHasher() {
    }

    public static String sha256(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 khong kha dung", e);
        }
    }

    public static boolean matches(String plain, String hashed) {
        return hashed != null && sha256(plain).equals(hashed);
    }
}
