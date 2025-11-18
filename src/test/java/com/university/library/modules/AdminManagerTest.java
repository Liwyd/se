package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdminManagerTest {
    private AdminManager adminManager;

    @BeforeEach
    void setUp() {
        adminManager = new AdminManager();
    }

    @Test
    void testLoginSuccess() {
        AdminManager.Admin result = adminManager.login("admin", "admin123");
        assertNotNull(result);
        assertEquals("admin", result.username());
        assertEquals("admin123", result.password());
    }

    @Test
    void testLoginFailureWrongPassword() {
        AdminManager.Admin result = adminManager.login("admin", "wrong");
        assertNull(result);
    }

    @Test
    void testLoginFailureWrongUsername() {
        AdminManager.Admin result = adminManager.login("wrong", "admin123");
        assertNull(result);
    }

    @Test
    void testGetCurrentUsername() {
        AdminManager.Admin admin = new AdminManager.Admin(1, "admin", "admin123");
        String username = adminManager.getCurrentUsername(admin);
        assertEquals("admin", username);
    }

    @Test
    void testGetCurrentUsernameWithNull() {
        String username = adminManager.getCurrentUsername(null);
        assertEquals("", username);
    }

    @Test
    void testGetCurrentUsernameWithWrongType() {
        String username = adminManager.getCurrentUsername("not an admin");
        assertEquals("", username);
    }
}

