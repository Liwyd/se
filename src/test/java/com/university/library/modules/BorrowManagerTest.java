package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowManagerTest {
    private BorrowManager borrowManager;
    private StudentManager studentManager;
    private BookManager bookManager;

    @BeforeEach
    void setUp() {
        borrowManager = new BorrowManager();
        studentManager = new StudentManager();
        bookManager = new BookManager();
        
        borrowManager.setManagers(studentManager, bookManager);
    }

    @Test
    void testCreateBorrowRequest() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        assertNotNull(record);
        assertEquals(1, record.studentId);
        assertEquals(1, record.bookId);
        assertEquals("pending", record.status);
        assertEquals("2024-01-01", record.startDate);
        assertEquals("2024-01-15", record.endDate);
    }

    @Test
    void testApproveRequest() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        boolean result = borrowManager.approveRequest(record.id, 1);
        assertTrue(result);
        assertEquals("approved", record.status);
        assertEquals(Integer.valueOf(1), record.approvedBy);
        assertNotNull(record.approvedDate);
    }

    @Test
    void testApproveRequestNotFound() {
        boolean result = borrowManager.approveRequest(999, 1);
        assertFalse(result);
    }

    @Test
    void testApproveRequestNotPending() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        record.status = "approved";
        boolean result = borrowManager.approveRequest(record.id, 1);
        assertFalse(result);
    }

    @Test
    void testRejectRequest() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        boolean result = borrowManager.rejectRequest(record.id);
        assertTrue(result);
        assertEquals("rejected", record.status);
    }

    @Test
    void testRejectRequestNotFound() {
        boolean result = borrowManager.rejectRequest(999);
        assertFalse(result);
    }

    @Test
    void testBorrowBook() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        boolean result = borrowManager.borrowBook(record.id);
        assertTrue(result);
        assertEquals("borrowed", record.status);
    }

    @Test
    void testBorrowBookNotApproved() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        boolean result = borrowManager.borrowBook(record.id);
        assertFalse(result);
    }

    @Test
    void testReturnBook() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        borrowManager.borrowBook(record.id);
        boolean result = borrowManager.returnBook(record.id, "2024-01-10", false);
        assertTrue(result);
        assertEquals("returned", record.status);
        assertEquals("2024-01-10", record.returnDate);
    }

    @Test
    void testReturnBookDelayed() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        borrowManager.borrowBook(record.id);
        boolean result = borrowManager.returnBook(record.id, "2024-01-20", true);
        assertTrue(result);
        assertTrue(record.isDelayed);
    }

    @Test
    void testGetStudentRequests() {
        borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.createBorrowRequest(1, 1, "2024-02-01", "2024-02-15");
        List<BorrowManager.BorrowRecordView> requests = borrowManager.getStudentRequests(1);
        assertEquals(2, requests.size());
    }

    @Test
    void testGetStudentHistory() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        borrowManager.borrowBook(record.id);
        borrowManager.returnBook(record.id, "2024-01-10", false);
        
        List<BorrowManager.BorrowRecordView> history = borrowManager.getStudentHistory(1);
        assertEquals(1, history.size());
        assertEquals("returned", history.get(0).status());
    }

    @Test
    void testGetPendingRequests() {
        borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        BorrowManager.BorrowRecord record2 = borrowManager.createBorrowRequest(1, 1, "2024-02-01", "2024-02-15");
        borrowManager.approveRequest(record2.id, 1);
        
        List<BorrowManager.BorrowRecordView> pending = borrowManager.getPendingRequests();
        assertEquals(1, pending.size());
        assertEquals("pending", pending.get(0).status());
    }

    @Test
    void testGetBorrowById() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        BorrowManager.BorrowRecord found = borrowManager.getBorrowById(record.id);
        assertNotNull(found);
        assertEquals(record.id, found.id);
    }

    @Test
    void testGetAllBorrows() {
        borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.createBorrowRequest(1, 1, "2024-02-01", "2024-02-15");
        List<BorrowManager.BorrowRecordView> all = borrowManager.getAllBorrows();
        assertEquals(2, all.size());
    }

    @Test
    void testGetBooksBorrowedByEmployee() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        borrowManager.borrowBook(record.id);
        
        List<BorrowManager.BorrowRecord> borrowed = borrowManager.getBooksBorrowedByEmployee(1);
        assertEquals(1, borrowed.size());
    }

    @Test
    void testGetBooksReturnedByEmployee() {
        BorrowManager.BorrowRecord record = borrowManager.createBorrowRequest(1, 1, "2024-01-01", "2024-01-15");
        borrowManager.approveRequest(record.id, 1);
        borrowManager.borrowBook(record.id);
        borrowManager.returnBook(record.id, "2024-01-10", false);
        
        List<BorrowManager.BorrowRecord> returned = borrowManager.getBooksReturnedByEmployee(1);
        assertEquals(1, returned.size());
    }
}
