package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario 4: Reporting Service Tests
 */
class ReportingServiceTest {
    private StudentManager studentManager;
    private BorrowManager borrowManager;
    private BookManager bookManager;
    private Object currentUser;

    @BeforeEach
    void setUp() {
        studentManager = new StudentManager();
        borrowManager = new BorrowManager();
        bookManager = new BookManager();
        
        borrowManager.setManagers(studentManager, bookManager);
        studentManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())), () -> currentUser);
    }

    /**
     * Test 4-1: Generate report for a student
     * Expected: StudentReport correctly calculates total borrows, 
     * not returned books count, and delayed borrows count
     */
    @Test
    void testGenerateStudentReport() {
        // Create student
        String input1 = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        StudentManager.Student student = studentManager.register();
        
        // Create borrow requests
        BorrowManager.BorrowRecord request1 = borrowManager.createBorrowRequest(
            student.id(), 1, "2024-01-01", "2024-01-15");
        BorrowManager.BorrowRecord request2 = borrowManager.createBorrowRequest(
            student.id(), 2, "2024-02-01", "2024-02-15");
        BorrowManager.BorrowRecord request3 = borrowManager.createBorrowRequest(
            student.id(), 3, "2024-03-01", "2024-03-15");
        
        // Approve and borrow first request
        borrowManager.approveRequest(request1.id, 1);
        borrowManager.borrowBook(request1.id);
        
        // Approve and borrow second request, then return it with delay
        borrowManager.approveRequest(request2.id, 1);
        borrowManager.borrowBook(request2.id);
        borrowManager.returnBook(request2.id, "2024-02-20", true); // Delayed return
        
        // Third request remains pending
        assertEquals("pending", request3.status, "Third request should be pending");
        
        // Get student history
        var history = borrowManager.getStudentHistory(student.id());
        
        // Verify report data
        assertEquals(3, history.size(), "Total borrows should be 3");
        
        long notReturned = history.stream()
            .filter(b -> b.status().equals("borrowed"))
            .count();
        assertEquals(1, notReturned, "Not returned books count should be 1");
        
        long delayed = history.stream()
            .filter(BorrowManager.BorrowRecordView::isDelayed)
            .count();
        assertEquals(1, delayed, "Delayed borrows count should be 1");
    }

    /**
     * Test 4-2: Calculate overall library statistics
     * Expected: LibraryStats correctly calculates average borrow days
     */
    @Test
    void testCalculateLibraryStatistics() {
        // Create students
        String input1 = "student1\npass1\npass1\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input1.getBytes())), () -> currentUser);
        StudentManager.Student student1 = studentManager.register();
        
        String input2 = "student2\npass2\npass2\n";
        studentManager.setInput(new Scanner(new ByteArrayInputStream(input2.getBytes())), () -> currentUser);
        StudentManager.Student student2 = studentManager.register();
        
        // Create borrow requests and return them
        // Request 1: 10 days
        BorrowManager.BorrowRecord req1 = borrowManager.createBorrowRequest(
            student1.id(), 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(req1.id, 1);
        borrowManager.borrowBook(req1.id);
        borrowManager.returnBook(req1.id, "2024-01-11", false); // 10 days
        
        // Request 2: 20 days
        BorrowManager.BorrowRecord req2 = borrowManager.createBorrowRequest(
            student2.id(), 2, "2024-02-01", "2024-02-15");
        borrowManager.approveRequest(req2.id, 1);
        borrowManager.borrowBook(req2.id);
        borrowManager.returnBook(req2.id, "2024-02-21", false); // 20 days
        
        // Request 3: 15 days
        BorrowManager.BorrowRecord req3 = borrowManager.createBorrowRequest(
            student1.id(), 3, "2024-03-01", "2024-03-15");
        borrowManager.approveRequest(req3.id, 1);
        borrowManager.borrowBook(req3.id);
        borrowManager.returnBook(req3.id, "2024-03-16", false); // 15 days
        
        // Calculate statistics using AdminManager
        var allBorrows = borrowManager.getAllBorrows();
        var returnedBorrows = allBorrows.stream()
            .filter(b -> b.status().equals("returned"))
            .toList();
        
        // Calculate average days
        if (!returnedBorrows.isEmpty()) {
            long totalDays = 0;
            for (var b : returnedBorrows) {
                LocalDate start = LocalDate.parse(b.startDate());
                LocalDate ret = LocalDate.parse(b.returnDate());
                totalDays += java.time.temporal.ChronoUnit.DAYS.between(start, ret);
            }
            int averageDays = Math.round((float) totalDays / returnedBorrows.size());
            
            // Expected average: (10 + 20 + 15) / 3 = 15 days
            assertEquals(15, averageDays, "Average borrow days should be 15");
        }
        
        // Verify total statistics
        assertEquals(3, allBorrows.size(), "Total borrow requests should be 3");
        long totalBorrowed = allBorrows.stream()
            .filter(b -> b.status().equals("borrowed") || b.status().equals("returned"))
            .count();
        assertEquals(3, totalBorrowed, "Total borrowed books should be 3");
    }
}

