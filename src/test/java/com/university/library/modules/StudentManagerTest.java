package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class StudentManagerTest {
    private StudentManager studentManager;
    private Object currentUser;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        currentUser = null;
        studentManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())), () -> currentUser);
    }

    @Test
    void testRegister() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        assertNotNull(student);
        assertEquals("student1", student.username());
        assertEquals("pass1", student.password());
        assertTrue(student.isActive());
    }

    @Test
    void testRegisterDuplicateUsername() {
        String input1 = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        studentManager.register();
        
        String input2 = "student1\npass2\npass2\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input2.getBytes())), () -> currentUser);
        StudentManager.Student student2 = studentManager.register();
        assertNull(student2);
    }

    @Test
    void testRegisterPasswordMismatch() {
        String input = "student1\npass1\npass2\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        assertNull(student);
    }

    @Test
    void testLoginSuccess() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        studentManager.register();
        
        StudentManager.Student result = studentManager.login("student1", "pass1");
        assertNotNull(result);
        assertEquals("student1", result.username());
    }

    @Test
    void testLoginFailureWrongPassword() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        studentManager.register();
        
        StudentManager.Student result = studentManager.login("student1", "wrong");
        assertNull(result);
    }

    @Test
    void testLoginFailureInactiveStudent() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        // Make student inactive using toggle method
        studentManager.toggleStudentStatus(student.id());
        
        StudentManager.Student result = studentManager.login("student1", "pass1");
        assertNull(result);
    }

    @Test
    void testGetStudentById() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        
        StudentManager.Student found = studentManager.getStudentById(student.id());
        assertNotNull(found);
        assertEquals(student.id(), found.id());
    }

    @Test
    void testGetStudentByIdNotFound() {
        StudentManager.Student found = studentManager.getStudentById(999);
        assertNull(found);
    }

    @Test
    void testGetAllStudents() {
        String input1 = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        studentManager.register();
        
        String input2 = "student2\npass2\npass2\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input2.getBytes())), () -> currentUser);
        studentManager.register();
        
        List<StudentManager.Student> all = studentManager.getAllStudents();
        assertEquals(2, all.size());
    }

    @Test
    void testGetActiveStudents() {
        String input1 = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        studentManager.register();
        
        String input2 = "student2\npass2\npass2\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input2.getBytes())), () -> currentUser);
        StudentManager.Student student2 = studentManager.register();
        studentManager.toggleStudentStatus(student2.id());
        
        List<StudentManager.Student> active = studentManager.getActiveStudents();
        assertEquals(1, active.size());
        assertEquals("student1", active.get(0).username());
    }

    @Test
    void testToggleStudentStatus() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        assertTrue(student.isActive());
        
        Boolean newStatus = studentManager.toggleStudentStatus(student.id());
        assertFalse(newStatus);
        // Verify by trying to login (inactive students can't login)
        StudentManager.Student loginResult = studentManager.login("student1", "pass1");
        assertNull(loginResult);
        
        newStatus = studentManager.toggleStudentStatus(student.id());
        assertTrue(newStatus);
        // Verify by login (active students can login)
        loginResult = studentManager.login("student1", "pass1");
        assertNotNull(loginResult);
    }

    @Test
    void testToggleStudentStatusNotFound() {
        Boolean result = studentManager.toggleStudentStatus(999);
        assertNull(result);
    }

    @Test
    void testGetCurrentUsername() {
        String input = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        
        String username = studentManager.getCurrentUsername(student);
        assertEquals("student1", username);
    }

    @Test
    void testGetCurrentUsernameWithNull() {
        String username = studentManager.getCurrentUsername(null);
        assertEquals("", username);
    }

    @Test
    void testGetCurrentUsernameWithWrongType() {
        String username = studentManager.getCurrentUsername("not a student");
        assertEquals("", username);
    }
}
