package com.university.library.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookManager {
    private final List<Book> books;
    private int nextId;
    private Scanner scanner;

    public BookManager() {
        this.books = new ArrayList<>();
        this.nextId = 1;
    }

    public void setInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public Book addBook(EmployeeManager.Employee employee) {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("Publication year: ");
        String yearStr = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();

        int year;
        try { year = Integer.parseInt(yearStr.trim()); } catch (Exception ex) { year = 0; }

        Book book = new Book(nextId++, title, author, year, isbn, category, true, employee.id(), java.time.LocalDate.now());
        books.add(book);
        System.out.println("Book added successfully!");
        return book;
    }

    public Book getBookById(int id) { return books.stream().filter(b -> b.id() == id).findFirst().orElse(null); }

    public List<Book> searchByTitle(String title) {
        String t = title.toLowerCase();
        return books.stream().filter(b -> b.title().toLowerCase().contains(t)).toList();
    }

    public List<Book> searchByYear(String year) {
        return books.stream().filter(b -> Integer.toString(b.publicationYear()).contains(year)).toList();
    }

    public List<Book> searchByAuthor(String author) {
        String a = author.toLowerCase();
        return books.stream().filter(b -> b.author().toLowerCase().contains(a)).toList();
    }

    public List<Book> combinedSearch(String title, String year, String author) {
        return books.stream().filter(b -> {
            boolean titleMatch = title == null || title.isBlank() || b.title().toLowerCase().contains(title.toLowerCase());
            boolean yearMatch = year == null || year.isBlank() || Integer.toString(b.publicationYear()).contains(year);
            boolean authorMatch = author == null || author.isBlank() || b.author().toLowerCase().contains(author.toLowerCase());
            return titleMatch && yearMatch && authorMatch;
        }).toList();
    }

    public boolean editBook(int bookId) {
        Book b = getBookById(bookId);
        if (b == null) {
            System.out.println("Book not found!");
            return false;
        }

        System.out.println("\n--- Edit Book ---");
        System.out.println("Current info:");
        System.out.println("Title: " + b.title());
        System.out.println("Author: " + b.author());
        System.out.println("Publication year: " + b.publicationYear());
        System.out.println("ISBN: " + b.isbn());
        System.out.println("Category: " + b.category());

        System.out.println("\nEnter new values (press Enter to keep current):");
        System.out.print("New title (" + b.title() + "): ");
        String newTitle = scanner.nextLine();
        System.out.print("New author (" + b.author() + "): ");
        String newAuthor = scanner.nextLine();
        System.out.print("New year (" + b.publicationYear() + "): ");
        String newYear = scanner.nextLine();
        System.out.print("New ISBN (" + b.isbn() + "): ");
        String newIsbn = scanner.nextLine();
        System.out.print("New category (" + b.category() + "): ");
        String newCategory = scanner.nextLine();

        String title = newTitle.isBlank() ? b.title() : newTitle;
        String author = newAuthor.isBlank() ? b.author() : newAuthor;
        int year = b.publicationYear();
        if (!newYear.isBlank()) {
            try { year = Integer.parseInt(newYear.trim()); } catch (Exception ignored) {}
        }
        String isbn = newIsbn.isBlank() ? b.isbn() : newIsbn;
        String category = newCategory.isBlank() ? b.category() : newCategory;

        books.remove(b);
        books.add(new Book(b.id(), title, author, year, isbn, category, b.isAvailable(), b.addedBy(), b.addedDate()));
        System.out.println("Book updated successfully!");
        return true;
    }

    public List<Book> getAllBooks() { return books; }
    public List<Book> getAvailableBooks() { return books.stream().filter(Book::isAvailable).toList(); }
    public List<Book> getBorrowedBooks() { return books.stream().filter(b -> !b.isAvailable()).toList(); }

    public boolean setBookAvailability(int bookId, boolean isAvailable) {
        Book b = getBookById(bookId);
        if (b == null) return false;
        books.remove(b);
        books.add(new Book(b.id(), b.title(), b.author(), b.publicationYear(), b.isbn(), b.category(), isAvailable, b.addedBy(), b.addedDate()));
        return true;
    }

    public List<Book> getBooksAddedByEmployee(int employeeId) {
        return books.stream().filter(b -> b.addedBy() == employeeId).toList();
    }

    public record Book(int id, String title, String author, int publicationYear, String isbn, String category,
                       boolean isAvailable, int addedBy, java.time.LocalDate addedDate) {}
}


