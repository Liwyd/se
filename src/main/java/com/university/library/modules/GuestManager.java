package com.university.library.modules;

import java.util.List;
import java.util.Scanner;

public class GuestManager {
    private Scanner scanner;

    public void setInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showRegisteredStudentsCount(StudentManager studentManager) {
        int total = studentManager.getAllStudents().size();
        int active = studentManager.getActiveStudents().size();
        System.out.println("\n--- Students Count ---");
        System.out.println("Total registered students: " + total);
        System.out.println("Active students: " + active);
        System.out.println("Inactive students: " + (total - active));
    }

    public void searchBooks(BookManager bookManager) {
        System.out.println("\n--- Book Search (Guest) ---");
        System.out.print("Book title: ");
        String term = scanner.nextLine();
        List<BookManager.Book> results = bookManager.searchByTitle(term);
        if (results.isEmpty()) {
            System.out.println("No books found!");
        } else {
            System.out.println("\nResults (" + results.size() + " book(s)):");
            for (int i = 0; i < results.size(); i++) {
                var b = results.get(i);
                System.out.println((i + 1) + ". " + b.title());
                System.out.println("   Author: " + b.author());
                System.out.println("   Year: " + b.publicationYear());
                System.out.println("   ISBN: " + b.isbn());
                System.out.println("   Category: " + b.category());
                System.out.println();
            }
        }
    }

    public void showStatistics(StudentManager studentManager, BookManager bookManager, BorrowManager borrowManager) {
        System.out.println("\n--- Statistics ---");
        int totalStudents = studentManager.getAllStudents().size();
        int totalBooks = bookManager.getAllBooks().size();
        int totalBorrows = borrowManager.getAllBorrows().size();
        long currentBorrows = borrowManager.getAllBorrows().stream().filter(b -> b.status().equals("borrowed")).count();

        System.out.println("Total students: " + totalStudents);
        System.out.println("Total books: " + totalBooks);
        System.out.println("Total borrows: " + totalBorrows);
        System.out.println("Currently borrowed: " + currentBorrows);

        var latest = borrowManager.getAllBorrows().stream()
                .filter(b -> b.status().equals("borrowed"))
                .sorted((a, b) -> b.startDate().compareTo(a.startDate()))
                .limit(5)
                .toList();
        System.out.println("\nLatest borrowed books:");
        if (latest.isEmpty()) {
            System.out.println("No active borrows.");
        } else {
            for (int i = 0; i < latest.size(); i++) {
                var br = latest.get(i);
                System.out.println((i + 1) + ". " + br.bookTitle() + " - " + br.studentUsername());
                System.out.println("   Start date: " + br.startDate());
            }
        }
    }
}


