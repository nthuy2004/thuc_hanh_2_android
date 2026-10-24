package com.loader.thuc_hanh_2_android.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordHasherTest {

    @Test
    public void sha256_knownValue() {
        assertEquals("240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9",
                PasswordHasher.sha256("admin123"));
    }

    @Test
    public void sha256_emptyString() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                PasswordHasher.sha256(""));
    }

    @Test
    public void sha256_isDeterministicAnd64Chars() {
        String a = PasswordHasher.sha256("abc");
        assertEquals(a, PasswordHasher.sha256("abc"));
        assertEquals(64, a.length());
    }

    @Test
    public void matches_correctAndWrongPassword() {
        String hashed = PasswordHasher.sha256("admin123");
        assertTrue(PasswordHasher.matches("admin123", hashed));
        assertFalse(PasswordHasher.matches("sai", hashed));
        assertFalse(PasswordHasher.matches("admin123", null));
    }
}
