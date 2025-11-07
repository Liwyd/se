package com.university.library.modules;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class StudentManager {
    private final List<Student> students;
    private int nextId;
    private Scanner scanner;
    private Supplier<Object> currentUserSupplier;

    public StudentManager() {
        this.students = new ArrayList<>();
        this.nextId = 1;
    }

    public void setInput(Scanner scanner, Supplier<Object> currentUserSupplier) {
        this.scanner = scanner;
        this.currentUserSupplier = currentUserSupplier;
    }

    public Student register() {
        System.out.println("\n--- Student Registration ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        if (students.stream().anyMatch(s -> s.username.equals(username))) {
            System.out.println("This username is already taken!");
            return null;
        }
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm password: ");
        String confirm = scanner.nextLine();
        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match!");
            return null;
        }
        Student s = new Student(nextId++, username, password, true, LocalDate.now());
        students.add(s);
        System.out.println("Registration completed successfully!");
        return s;
    }

    public Student login(String username, String password) {
        return students.stream()
                .filter(s -> s.username.equals(username) && s.password.equals(password) && s.isActive)
                .findFirst().orElse(null);
    }

    public void searchBooks(BookManager bookManager) {
        System.out.println("\n--- Book Search ---");
        System.out.println("1. Search by title");
        System.out.println("2. Search by year");
        System.out.println("3. Search by author");
        System.out.println("4. Combined search");
        System.out.print("Choose: ");
        String choice = scanner.nextLine().trim();
        List<BookManager.Book> results = new ArrayList<>();
        switch (choice) {
            case "1" -> {
                System.out.print("Title: ");
                String t = scanner.nextLine();
                results = bookManager.searchByTitle(t);
            }
            case "2" -> {
                System.out.print("Year: ");
                String y = scanner.nextLine();
                results = bookManager.searchByYear(y);
            }
            case "3" -> {
                System.out.print("Author: ");
                String a = scanner.nextLine();
                results = bookManager.searchByAuthor(a);
            }
            case "4" -> {
                System.out.print("Title (optional): ");
                String t = scanner.nextLine();
                System.out.print("Year (optional): ");
                String y = scanner.nextLine();
                System.out.print("Author (optional): ");
                String a = scanner.nextLine();
                results = bookManager.combinedSearch(t, y, a);
            }
            default -> {
                System.out.println("Invalid choice!");
                return;
            }
        }
        if (results.isEmpty()) {
            System.out.println("No books found!");
        } else {
            System.out.println("\nResults (" + results.size() + " book(s)):");
            for (int i = 0; i < results.size(); i++) {
                var b = results.get(i);
                System.out.println((i + 1) + ". " + b.title() + " - " + b.author() + " (" + b.publicationYear() + ")");
                System.out.println("   Status: " + (b.isAvailable() ? "Available" : "Borrowed"));
                System.out.println("   ID: " + b.id());
                System.out.println();
            }
        }
    }

    public void requestBorrow(BookManager bookManager, BorrowManager borrowManager) {
        System.out.println("\n--- Create Borrow Request ---");
        System.out.print("Book ID: ");
        String idStr = scanner.nextLine();
        int id;
        try { id = Integer.parseInt(idStr.trim()); } catch (Exception ex) { System.out.println("Invalid ID!"); return; }
        var book = bookManager.getBookById(id);
        if (book == null) { System.out.println("Book not found!"); return; }
        if (!book.isAvailable()) { System.out.println("This book is currently borrowed!"); return; }

        System.out.print("Start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();
        System.out.print("End date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();

        LocalDate start;
        LocalDate end;
        LocalDate today = LocalDate.now();
        try { start = LocalDate.parse(startDate); end = LocalDate.parse(endDate); } catch (Exception ex) {
            System.out.println("Invalid date format!");
            return;
        }
        if (start.isBefore(today)) { System.out.println("Start date cannot be before today!"); return; }
        if (!end.isAfter(start)) { System.out.println("End date must be after start date!"); return; }

        var cu = currentUserSupplier.get();
        if (!(cu instanceof Student s)) { System.out.println("Not authenticated as student."); return; }
        var req = borrowManager.createBorrowRequest(s.id, book.id(), startDate, endDate);
        if (req != null) {
            System.out.println("Borrow request submitted successfully!");
        } else {
            System.out.println("Failed to submit request!");
        }
    }

    public void showBorrowRequests(BorrowManager borrowManager) {
        System.out.println("\n--- Your Borrow Requests ---");
        var cu = currentUserSupplier.get();
        if (!(cu instanceof Student s)) { System.out.println("Not authenticated as student."); return; }
        var requests = borrowManager.getStudentRequests(s.id);
        if (requests.isEmpty()) {
            System.out.println("You have no borrow requests.");
            return;
        }
        for (int i = 0; i < requests.size(); i++) {
            var r = requests.get(i);
            System.out.println((i + 1) + ". Book: " + r.bookTitle());
            System.out.println("   Start date: " + r.startDate());
            System.out.println("   End date: " + r.endDate());
            System.out.println("   Status: " + r.status());
            System.out.println();
        }
    }

    public Student getStudentById(int id) { return students.stream().filter(s -> s.id == id).findFirst().orElse(null); }
    public List<Student> getAllStudents() { return students; }
    public List<Student> getActiveStudents() { return students.stream().filter(s -> s.isActive).toList(); }

    public Boolean toggleStudentStatus(int studentId) {
        Student s = getStudentById(studentId);
        if (s == null) return null;
        s.isActive = !s.isActive;
        return s.isActive;
    }

    public String getCurrentUsername(Object student) { return student instanceof Student s ? s.username : ""; }

    public record Student(int id, String username, String password, boolean isActive, LocalDate registrationDate) {}
}


