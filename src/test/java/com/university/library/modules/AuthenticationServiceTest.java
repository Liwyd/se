package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario 1: Authentication Service Tests
 */
class AuthenticationServiceTest {
    private StudentManager studentManager;
    private Object currentUser;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        currentUser = null;
        studentManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())), () -> currentUser);
    }

    /**
     * Test 1-1: Register a new user with unique username
     * Expected: register method returns true (non-null Student)
     */
    @Test
    void testRegisterWithUniqueUsername() {
        String input = "student1\npass123\npass123\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        
        StudentManager.Student result = studentManager.register();
        
        assertNotNull(result, "Registration should succeed and return a Student object");
        assertEquals("student1", result.username());
        assertEquals("pass123", result.password());
        assertTrue(result.isActive());
    }

    /**
     * Test 1-2: Register with duplicate username
     * Expected: register method returns false (null)
     */
    @Test
    void testRegisterWithDuplicateUsername() {
        // First registration
        String input1 = "student1\npass123\npass123\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        StudentManager.Student first = studentManager.register();
        assertNotNull(first, "First registration should succeed");
        
        // Second registration with same username
        String input2 = "student1\npass456\npass456\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input2.getBytes())), () -> currentUser);
        StudentManager.Student second = studentManager.register();
        
        assertNull(second, "Registration with duplicate username should fail and return null");
    }

    /**
     * Test 1-3: Login with correct username and password
     * Expected: login method returns true (non-null Student)
     */
    @Test
    void testLoginWithCorrectCredentials() {
        // Register first
        String input1 = "student1\npass123\npass123\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        studentManager.register();
        
        // Login
        StudentManager.Student result = studentManager.login("student1", "pass123");
        
        assertNotNull(result, "Login with correct credentials should succeed");
        assertEquals("student1", result.username());
    }

    /**
     * Test 1-4: Login with correct username but wrong password
     * Expected: login method returns false (null)
     */
    @Test
    void testLoginWithWrongPassword() {
        // Register first
        String input1 = "student1\npass123\npass123\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        studentManager.register();
        
        // Login with wrong password
        StudentManager.Student result = studentManager.login("student1", "wrongpass");
        
        assertNull(result, "Login with wrong password should fail and return null");
    }

    /**
     * Test 1-5: Login with non-existent username
     * Expected: login method returns false (null)
     */
    @Test
    void testLoginWithNonExistentUsername() {
        StudentManager.Student result = studentManager.login("nonexistent", "anypassword");
        
        assertNull(result, "Login with non-existent username should fail and return null");
    }
}

