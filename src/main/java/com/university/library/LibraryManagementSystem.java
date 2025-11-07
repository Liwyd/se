package com.university.library;

import com.university.library.modules.*;

import java.util.Scanner;

public class LibraryManagementSystem {
    private final StudentManager studentManager;
    private final BookManager bookManager;
    private final EmployeeManager employeeManager;
    private final AdminManager adminManager;
    private final GuestManager guestManager;
    private final BorrowManager borrowManager;

    private Object currentUser;
    private String currentUserType;

    private final Scanner scanner;

    public LibraryManagementSystem() {
        this.studentManager = new StudentManager();
        this.bookManager = new BookManager();
        this.employeeManager = new EmployeeManager();
        this.adminManager = new AdminManager();
        this.guestManager = new GuestManager();
        this.borrowManager = new BorrowManager();
        this.currentUser = null;
        this.currentUserType = null;
        this.borrowManager.setManagers(this.studentManager, this.bookManager);
        this.scanner = new Scanner(System.in);
        // Wire managers with input source where needed
        this.studentManager.setInput(scanner, () -> currentUser);
        this.employeeManager.setInput(scanner, () -> currentUser);
        this.adminManager.setInput(scanner);
        this.guestManager.setInput(scanner);
        this.bookManager.setInput(scanner);
        this.borrowManager.setInput(scanner);
    }

    public void start() {
        System.out.println("=== University Library Management System ===\n");
        showMainMenu();
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Continue as Guest");
            System.out.println("2. Student Registration");
            System.out.println("3. Student Login");
            System.out.println("4. Employee Login");
            System.out.println("5. Admin Login");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> guestMenu();
                case "2" -> studentRegistration();
                case "3" -> studentLogin();
                case "4" -> employeeLogin();
                case "5" -> adminLogin();
                case "6" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void guestMenu() {
        while (true) {
            System.out.println("\n--- Guest Menu ---");
            System.out.println("1. View registered students count");
            System.out.println("2. Search book by title");
            System.out.println("3. View statistics");
            System.out.println("4. Back to main menu");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> guestManager.showRegisteredStudentsCount(studentManager);
                case "2" -> guestManager.searchBooks(bookManager);
                case "3" -> guestManager.showStatistics(studentManager, bookManager, borrowManager);
                case "4" -> { return; }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void studentRegistration() {
        var student = this.studentManager.register();
        if (student != null) {
            System.out.println("Registration successful! You can now log in.");
        }
    }

    private void studentLogin() {
        System.out.println("\n--- Student Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        var student = this.studentManager.login(username, password);
        if (student != null) {
            this.currentUser = student;
            this.currentUserType = "student";
            studentMenu();
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private void studentMenu() {
        while (true) {
            System.out.println("\n--- Student Menu (" + this.studentManager.getCurrentUsername(currentUser) + ") ---");
            System.out.println("1. Search books");
            System.out.println("2. Create borrow request");
            System.out.println("3. View borrow requests");
            System.out.println("4. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> this.studentManager.searchBooks(this.bookManager);
                case "2" -> this.studentManager.requestBorrow(this.bookManager, this.borrowManager);
                case "3" -> this.studentManager.showBorrowRequests(this.borrowManager);
                case "4" -> { this.currentUser = null; this.currentUserType = null; return; }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void employeeLogin() {
        System.out.println("\n--- Employee Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        var employee = this.employeeManager.login(username, password);
        if (employee != null) {
            this.currentUser = employee;
            this.currentUserType = "employee";
            employeeMenu();
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private void employeeMenu() {
        while (true) {
            System.out.println("\n--- Employee Menu (" + this.employeeManager.getCurrentUsername(currentUser) + ") ---");
            System.out.println("1. Change password");
            System.out.println("2. Add book");
            System.out.println("3. Search and edit book");
            System.out.println("4. Review borrow requests");
            System.out.println("5. View student report");
            System.out.println("6. Toggle student active status");
            System.out.println("7. Receive returned book");
            System.out.println("8. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> this.employeeManager.changePassword(this.currentUser);
                case "2" -> this.employeeManager.addBook(this.bookManager, this.currentUser);
                case "3" -> this.employeeManager.searchAndEditBook(this.bookManager);
                case "4" -> this.employeeManager.reviewBorrowRequests(this.borrowManager, this.currentUser);
                case "5" -> this.employeeManager.showStudentReport(this.studentManager, this.borrowManager);
                case "6" -> this.employeeManager.toggleStudentStatus(this.studentManager);
                case "7" -> this.employeeManager.returnBook(this.borrowManager);
                case "8" -> { this.currentUser = null; this.currentUserType = null; return; }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private void adminLogin() {
        System.out.println("\n--- Admin Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        var admin = this.adminManager.login(username, password);
        if (admin != null) {
            this.currentUser = admin;
            this.currentUserType = "admin";
            adminMenu();
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu (" + this.adminManager.getCurrentUsername(currentUser) + ") ---");
            System.out.println("1. Add employee");
            System.out.println("2. View employee performance");
            System.out.println("3. View borrow statistics");
            System.out.println("4. View student statistics");
            System.out.println("5. Logout");

            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> this.adminManager.addEmployee(this.employeeManager);
                case "2" -> this.adminManager.showEmployeePerformance(this.employeeManager, this.bookManager, this.borrowManager);
                case "3" -> this.adminManager.showBorrowStatistics(this.borrowManager);
                case "4" -> this.adminManager.showStudentStatistics(this.studentManager, this.borrowManager);
                case "5" -> { this.currentUser = null; this.currentUserType = null; return; }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        new LibraryManagementSystem().start();
    }
}


