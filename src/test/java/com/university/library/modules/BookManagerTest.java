package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BookManagerTest {
    private BookManager bookManager;

    @BeforeEach
    void setUp() {
        bookManager = new BookManager();
        bookManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())));
    }

    @Test
    void testGetBookByIdNotFound() {
        BookManager.Book book = bookManager.getBookById(999);
        assertNull(book);
    }

    @Test
    void testSearchByTitleEmpty() {
        List<BookManager.Book> results = bookManager.searchByTitle("Java");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchByYearEmpty() {
        List<BookManager.Book> results = bookManager.searchByYear("2020");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchByAuthorEmpty() {
        List<BookManager.Book> results = bookManager.searchByAuthor("John Doe");
        assertTrue(results.isEmpty());
    }

    @Test
    void testCombinedSearchWithNulls() {
        List<BookManager.Book> results = bookManager.combinedSearch(null, null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    void testCombinedSearchWithBlanks() {
        List<BookManager.Book> results = bookManager.combinedSearch("", "", "");
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllBooksEmpty() {
        List<BookManager.Book> allBooks = bookManager.getAllBooks();
        assertTrue(allBooks.isEmpty());
    }

    @Test
    void testGetAvailableBooksEmpty() {
        List<BookManager.Book> available = bookManager.getAvailableBooks();
        assertTrue(available.isEmpty());
    }

    @Test
    void testGetBorrowedBooksEmpty() {
        List<BookManager.Book> borrowed = bookManager.getBorrowedBooks();
        assertTrue(borrowed.isEmpty());
    }

    @Test
    void testSetBookAvailabilityNotFound() {
        boolean result = bookManager.setBookAvailability(999, false);
        assertFalse(result);
    }

    @Test
    void testGetBooksAddedByEmployeeEmpty() {
        List<BookManager.Book> books = bookManager.getBooksAddedByEmployee(1);
        assertTrue(books.isEmpty());
    }
}
