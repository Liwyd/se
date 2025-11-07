package com.university.library.modules;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BorrowManager {
    private final List<BorrowRecord> borrows;
    private int nextId;
    private Scanner scanner;
    private StudentManager studentManager;
    private BookManager bookManager;

    public BorrowManager() {
        this.borrows = new ArrayList<>();
        this.nextId = 1;
    }

    public void setManagers(StudentManager studentManager, BookManager bookManager) {
        this.studentManager = studentManager;
        this.bookManager = bookManager;
    }

    public void setInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public BorrowRecord createBorrowRequest(int studentId, int bookId, String startDate, String endDate) {
        BorrowRecord r = new BorrowRecord(nextId++, studentId, bookId, startDate, endDate,
                "pending", LocalDate.now().toString(), null, null, null, false);
        borrows.add(r);
        return r;
    }

    public boolean approveRequest(int borrowId, int employeeId) {
        BorrowRecord b = getBorrowById(borrowId);
        if (b != null && b.status.equals("pending")) {
            b.status = "approved";
            b.approvedBy = employeeId;
            b.approvedDate = LocalDate.now().toString();
            return true;
        }
        return false;
    }

    public boolean rejectRequest(int borrowId) {
        BorrowRecord b = getBorrowById(borrowId);
        if (b != null && b.status.equals("pending")) {
            b.status = "rejected";
            return true;
        }
        return false;
    }

    public boolean borrowBook(int borrowId) {
        BorrowRecord b = getBorrowById(borrowId);
        if (b != null && b.status.equals("approved")) {
            b.status = "borrowed";
            return true;
        }
        return false;
    }

    public boolean returnBook(int borrowId, String returnDate, boolean isDelayed) {
        BorrowRecord b = getBorrowById(borrowId);
        if (b != null && b.status.equals("borrowed")) {
            b.status = "returned";
            b.returnDate = returnDate;
            b.isDelayed = isDelayed;
            return true;
        }
        return false;
    }

    public List<BorrowRecordView> getStudentRequests(int studentId) {
        return borrows.stream()
                .filter(b -> b.studentId == studentId)
                .map(this::toView)
                .toList();
    }

    public List<BorrowRecordView> getStudentHistory(int studentId) {
        return borrows.stream()
                .filter(b -> b.studentId == studentId)
                .map(this::toView)
                .toList();
    }

    public List<BorrowRecordView> getPendingRequests() {
        return borrows.stream()
                .filter(b -> b.status.equals("pending"))
                .map(this::toView)
                .toList();
    }

    public BorrowRecord getBorrowById(int borrowId) {
        return borrows.stream().filter(b -> b.id == borrowId).findFirst().orElse(null);
    }

    public List<BorrowRecordView> getAllBorrows() {
        return borrows.stream().map(this::toView).toList();
    }

    public List<BorrowRecord> getBooksBorrowedByEmployee(int employeeId) {
        return borrows.stream().filter(b -> employeeId == nullSafe(b.approvedBy) && b.status.equals("borrowed")).toList();
    }

    public List<BorrowRecord> getBooksReturnedByEmployee(int employeeId) {
        return borrows.stream().filter(b -> employeeId == nullSafe(b.approvedBy) && b.status.equals("returned")).toList();
    }

    private int nullSafe(Integer value) { return value == null ? -1 : value; }

    private BorrowRecordView toView(BorrowRecord b) {
        String bookTitle = "Book " + b.bookId;
        String studentUsername = "Student " + b.studentId;
        var book = bookManager != null ? bookManager.getBookById(b.bookId) : null;
        if (book != null) bookTitle = book.title();
        var student = studentManager != null ? studentManager.getStudentById(b.studentId) : null;
        if (student != null) studentUsername = student.username();
        return new BorrowRecordView(b.id, b.studentId, b.bookId, b.startDate, b.endDate, b.status,
                b.requestDate, b.approvedBy, b.approvedDate, b.returnDate, b.isDelayed, bookTitle, studentUsername);
    }

    public static class BorrowRecord {
        public int id;
        public int studentId;
        public int bookId;
        public String startDate;
        public String endDate;
        public String status; // pending, approved, borrowed, returned, rejected
        public String requestDate;
        public Integer approvedBy;
        public String approvedDate;
        public String returnDate;
        public boolean isDelayed;

        public BorrowRecord(int id, int studentId, int bookId, String startDate, String endDate, String status,
                            String requestDate, Integer approvedBy, String approvedDate, String returnDate, boolean isDelayed) {
            this.id = id;
            this.studentId = studentId;
            this.bookId = bookId;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
            this.requestDate = requestDate;
            this.approvedBy = approvedBy;
            this.approvedDate = approvedDate;
            this.returnDate = returnDate;
            this.isDelayed = isDelayed;
        }
    }

    public record BorrowRecordView(int id, int studentId, int bookId, String startDate, String endDate, String status,
                                   String requestDate, Integer approvedBy, String approvedDate, String returnDate, boolean isDelayed,
                                   String bookTitle, String studentUsername) {}
}


