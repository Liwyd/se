package com.university.library.modules;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminManager {
    private final List<Admin> admins;
    private Scanner scanner;

    public AdminManager() {
        this.admins = new ArrayList<>();
        this.admins.add(new Admin(1, "admin", "admin123"));
    }

    public void setInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public Admin login(String username, String password) {
        return admins.stream()
                .filter(a -> a.username.equals(username) && a.password.equals(password))
                .findFirst().orElse(null);
    }

    public boolean addEmployee(EmployeeManager employeeManager) {
        System.out.println("\n--- Add New Employee ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Confirm password: ");
        String confirm = scanner.nextLine();

        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match!");
            return false;
        }

        var employee = employeeManager.addEmployee(username, password);
        return employee != null;
    }

    public void showEmployeePerformance(EmployeeManager employeeManager, BookManager bookManager, BorrowManager borrowManager) {
        System.out.println("\n--- Employee Performance ---");
        var employees = employeeManager.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("No employees registered.");
            return;
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (var e : employees) {
            int booksAdded = bookManager.getBooksAddedByEmployee(e.id()).size();
            int booksBorrowed = borrowManager.getBooksBorrowedByEmployee(e.id()).size();
            int booksReturned = borrowManager.getBooksReturnedByEmployee(e.id()).size();
            System.out.println("\nEmployee: " + e.username());
            System.out.println("Added on: " + fmt.format(e.addedDate()));
            System.out.println("Registered books: " + booksAdded);
            System.out.println("Books borrowed: " + booksBorrowed);
            System.out.println("Books received: " + booksReturned);
        }
    }

    public void showBorrowStatistics(BorrowManager borrowManager) {
        System.out.println("\n--- Borrow Statistics ---");
        var all = borrowManager.getAllBorrows();
        int totalRequests = all.size();
        long totalBorrowed = all.stream().filter(b -> b.status().equals("borrowed") || b.status().equals("returned")).count();

        var returned = all.stream().filter(b -> b.status().equals("returned")).toList();
        int averageDays = 0;
        if (!returned.isEmpty()) {
            long totalDays = 0;
            for (var b : returned) {
                LocalDate start = LocalDate.parse(b.startDate());
                LocalDate ret = LocalDate.parse(b.returnDate());
                totalDays += java.time.temporal.ChronoUnit.DAYS.between(start, ret);
            }
            averageDays = Math.round((float) totalDays / returned.size());
        }

        long current = all.stream().filter(b -> b.status().equals("borrowed")).count();

        System.out.println("Total borrow requests: " + totalRequests);
        System.out.println("Total borrows performed: " + totalBorrowed);
        System.out.println("Average borrow days: " + averageDays + " day(s)");
        System.out.println("Currently borrowed books: " + current);
    }

    public void showStudentStatistics(StudentManager studentManager, BorrowManager borrowManager) {
        System.out.println("\n--- Student Statistics ---");
        var allStudents = studentManager.getAllStudents();
        var activeStudents = studentManager.getActiveStudents();

        System.out.println("Total students: " + allStudents.size());
        System.out.println("Active students: " + activeStudents.size());
        System.out.println("Inactive students: " + (allStudents.size() - activeStudents.size()));

        record DelayItem(String username, int delays) {}
        List<DelayItem> delayItems = new ArrayList<>();
        for (var s : allStudents) {
            var history = borrowManager.getStudentHistory(s.id());
            int delays = (int) history.stream().filter(BorrowRecord::isDelayed).count();
            if (delays > 0) {
                delayItems.add(new DelayItem(s.username(), delays));
            }
        }
        delayItems.sort((a, b) -> Integer.compare(b.delays(), a.delays()));

        System.out.println("\nTop 10 students with most delays:");
        if (delayItems.isEmpty()) {
            System.out.println("No student has delays.");
        } else {
            int idx = 1;
            for (var it : delayItems.stream().limit(10).toList()) {
                System.out.println(idx + ". " + it.username() + ": " + it.delays() + " delay(s)");
                idx++;
            }
        }

        System.out.println("\nDetailed student statistics:");
        for (var s : allStudents) {
            var history = borrowManager.getStudentHistory(s.id());
            long notReturned = history.stream().filter(b -> b.status().equals("borrowed")).count();
            long delayed = history.stream().filter(BorrowRecord::isDelayed).count();

            System.out.println("\n" + s.username() + ":");
            System.out.println("  Total borrows: " + history.size());
            System.out.println("  Not returned: " + notReturned);
            System.out.println("  With delay: " + delayed);
        }
    }

    public String getCurrentUsername(Object admin) {
        return admin instanceof Admin a ? a.username : "";
    }

    public record Admin(int id, String username, String password) {}
}


