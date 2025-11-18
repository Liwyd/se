package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GuestManagerTest {
    private GuestManager guestManager;
    private StudentManager studentManager;
    private BookManager bookManager;
    private BorrowManager borrowManager;

    @BeforeEach
    void setUp() {
        guestManager = new GuestManager();
        studentManager = new StudentManager();
        bookManager = new BookManager();
        borrowManager = new BorrowManager();
        
        guestManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())));
        borrowManager.setManagers(studentManager, bookManager);
    }

    @Test
    void testShowRegisteredStudentsCount() {
        // This method prints to console, so we just verify it doesn't throw
        assertDoesNotThrow(() -> guestManager.showRegisteredStudentsCount(studentManager));
    }

    @Test
    void testSearchBooks() {
        // This method uses Scanner input, so we test with a mock input
        String input = "Test\n";
        guestManager.setInput(new Scanner(new ByteArrayInputStream(input.getBytes())));
        assertDoesNotThrow(() -> guestManager.searchBooks(bookManager));
    }

    @Test
    void testShowStatistics() {
        // This method prints to console, so we just verify it doesn't throw
        assertDoesNotThrow(() -> guestManager.showStatistics(studentManager, bookManager, borrowManager));
    }
}
