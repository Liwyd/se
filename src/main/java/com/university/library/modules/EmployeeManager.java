package com.university.library.modules;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class EmployeeManager {
    private final List<Employee> employees;
    private int nextId;
    private Scanner scanner;
    private Supplier<Object> currentUserSupplier;

    public EmployeeManager() {
        this.employees = new ArrayList<>();
        this.nextId = 1;
    }

    public void setInput(Scanner scanner, Supplier<Object> currentUserSupplier) {
        this.scanner = scanner;
        this.currentUserSupplier = currentUserSupplier;
    }

    public Employee addEmployee(String username, String password) {
        if (employees.stream().anyMatch(e -> e.username().equals(username))) {
            System.out.println("This username is already taken!");
            return null;
        }
        Employee e = new Employee(nextId++, username, password, LocalDate.now());
        employees.add(e);
        System.out.println("Employee added successfully!");
        return e;
    }

    public Employee login(String username, String password) {
        return employees.stream()
                .filter(e -> e.username().equals(username) && e.password().equals(password))
                .findFirst().orElse(null);
    }

    public boolean changePassword(Object employeeObj) {
        if (!(employeeObj instanceof Employee employee)) return false;
        System.out.println("\n--- Change Password ---");
        System.out.print("Current password: ");
        String current = scanner.nextLine();
        if (!current.equals(employee.password())) {
            System.out.println("Current password is incorrect!");
            return false;
        }
        System.out.print("New password: ");
        String np = scanner.nextLine();
        System.out.print("Confirm new password: ");
        String cp = scanner.nextLine();
        if (!np.equals(cp)) {
            System.out.println("Passwords do not match!");
            return false;
        }
        // Create new Employee with updated password and replace in list
        Employee updatedEmployee = new Employee(employee.id(), employee.username(), np, employee.addedDate());
        int index = employees.indexOf(employee);
        if (index >= 0) {
            employees.set(index, updatedEmployee);
        }
        System.out.println("Password changed successfully!");
        return true;
    }

    public BookManager.Book addBook(BookManager bookManager, Object employeeObj) {
        if (!(employeeObj instanceof Employee employee)) return null;
        return bookManager.addBook(employee);
    }

    public void searchAndEditBook(BookManager bookManager) {
        System.out.println("\n--- Search and Edit Book ---");
        System.out.print("Title or author: ");
        String term = scanner.nextLine();
        var results = bookManager.getAllBooks().stream()
                .filter(b -> b.title().toLowerCase().contains(term.toLowerCase()) || b.author().toLowerCase().contains(term.toLowerCase()))
                .toList();
        if (results.isEmpty()) {
            System.out.println("No books found!");
            return;
        }
        System.out.println("\nSearch results:");
        for (int i = 0; i < results.size(); i++) {
            var b = results.get(i);
            System.out.println((i + 1) + ". " + b.title() + " - " + b.author() + " (" + b.publicationYear() + ")");
            System.out.println("   ID: " + b.id());
        }
        System.out.print("Enter item number to edit (0 to cancel): ");
        String choice = scanner.nextLine();
        int idx;
        try { idx = Integer.parseInt(choice.trim()) - 1; } catch (Exception ex) { idx = -1; }
        if (idx >= 0 && idx < results.size()) {
            bookManager.editBook(results.get(idx).id());
        }
    }

    public void reviewBorrowRequests(BorrowManager borrowManager, Object employeeObj) {
        if (!(employeeObj instanceof Employee employee)) return;
        System.out.println("\n--- Review Borrow Requests ---");
        var today = LocalDate.now();
        var pending = borrowManager.getPendingRequests().stream()
                .filter(r -> LocalDate.parse(r.startDate()).compareTo(today) <= 0)
                .toList();
        if (pending.isEmpty()) {
            System.out.println("No borrow requests to review.");
            return;
        }
        System.out.println("Reviewable requests (" + pending.size() + "):");
        for (int i = 0; i < pending.size(); i++) {
            var r = pending.get(i);
            System.out.println((i + 1) + ". Student: " + r.studentUsername());
            System.out.println("   Book: " + r.bookTitle());
            System.out.println("   Start date: " + r.startDate());
            System.out.println("   End date: " + r.endDate());
            System.out.println();
        }
        System.out.print("Enter request number to approve (0 to cancel): ");
        String choice = scanner.nextLine();
        int idx;
        try { idx = Integer.parseInt(choice.trim()) - 1; } catch (Exception ex) { idx = -1; }
        if (idx >= 0 && idx < pending.size()) {
            var req = pending.get(idx);
            System.out.print("Approve (y) or reject (n): ");
            String action = scanner.nextLine().trim().toLowerCase();
            if (action.equals("y")) {
                borrowManager.approveRequest(req.id(), employee.id());
                System.out.println("Request approved!");
            } else if (action.equals("n")) {
                borrowManager.rejectRequest(req.id());
                System.out.println("Request rejected!");
            }
        }
    }

    public void showStudentReport(StudentManager studentManager, BorrowManager borrowManager) {
        System.out.println("\n--- Student Report ---");
        System.out.print("Student ID: ");
        String idStr = scanner.nextLine();
        int id;
        try { id = Integer.parseInt(idStr.trim()); } catch (Exception ex) { System.out.println("Invalid ID!"); return; }
        var student = studentManager.getStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        var history = borrowManager.getStudentHistory(id);
        System.out.println("\nStudent: " + student.username());
        System.out.println("Status: " + (student.isActive() ? "Active" : "Inactive"));
        System.out.println("Registration date: " + student.registrationDate());
        System.out.println("\nBorrow stats:");
        System.out.println("Total borrows: " + history.size());
        long notReturned = history.stream().filter(b -> b.status().equals("borrowed")).count();
        System.out.println("Not returned: " + notReturned);
        long delayed = history.stream().filter(BorrowManager.BorrowRecordView::isDelayed).count();
        System.out.println("With delay: " + delayed);

        System.out.println("\nBorrow History:");
        if (history.isEmpty()) {
            System.out.println("No borrows recorded.");
        } else {
            for (int i = 0; i < history.size(); i++) {
                var br = history.get(i);
                System.out.println((i + 1) + ". " + br.bookTitle());
                System.out.println("   Start date: " + br.startDate());
                System.out.println("   End date: " + br.endDate());
                System.out.println("   Status: " + br.status());
                if (br.isDelayed()) {
                    System.out.println("   ⚠ Delayed return");
                }
                System.out.println();
            }
        }
    }

    public void toggleStudentStatus(StudentManager studentManager) {
        System.out.println("\n--- Toggle Student Active Status ---");
        System.out.print("Student ID: ");
        String idStr = scanner.nextLine();
        int id;
        try { id = Integer.parseInt(idStr.trim()); } catch (Exception ex) { System.out.println("Invalid ID!"); return; }
        var student = studentManager.getStudentById(id);
        if (student == null) {
            System.out.println("Student not found!");
            return;
        }
        Boolean newStatus = studentManager.toggleStudentStatus(id);
        if (newStatus != null) {
            System.out.println("Student is now " + (newStatus ? "Active" : "Inactive") + ".");
        }
    }

    public void returnBook(BorrowManager borrowManager) {
        System.out.println("\n--- Receive Returned Book ---");
        System.out.print("Borrow ID: ");
        String idStr = scanner.nextLine();
        int id;
        try { id = Integer.parseInt(idStr.trim()); } catch (Exception ex) { System.out.println("Invalid ID!"); return; }
        var borrow = borrowManager.getBorrowById(id);
        if (borrow == null) {
            System.out.println("Borrow record not found!");
            return;
        }
        if (!"borrowed".equals(borrow.status)) {
            System.out.println("This borrow is not currently active!");
            return;
        }
        String returnDate = LocalDate.now().toString();
        boolean isDelayed = LocalDate.parse(returnDate).isAfter(LocalDate.parse(borrow.endDate));
        borrowManager.returnBook(id, returnDate, isDelayed);
        System.out.println("Book received successfully!");
        if (isDelayed) {
            System.out.println("⚠ This book was returned late!");
        }
    }

    public String getCurrentUsername(Object employee) {
        return employee instanceof Employee e ? e.username() : "";
    }

    public List<Employee> getAllEmployees() { return employees; }
    public Employee getEmployeeById(int id) { return employees.stream().filter(e -> e.id() == id).findFirst().orElse(null); }

    public record Employee(int id, String username, String password, LocalDate addedDate) {}
}


