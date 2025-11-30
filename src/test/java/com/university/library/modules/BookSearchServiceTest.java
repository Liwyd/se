package com.university.library.modules;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario 2: Book Search Service Tests
 */
class BookSearchServiceTest {
    private BookManager bookManager;

    @BeforeEach
    void setUp() {
        bookManager = new BookManager();
        bookManager.setInput(new Scanner(new ByteArrayInputStream("".getBytes())));
        
        // Add test books directly to the list returned by getAllBooks()
        // Since getAllBooks() returns a reference to the internal list, we can add to it
        bookManager.getAllBooks().add(new BookManager.Book(1, "Java Programming", "John Doe", 2020, "123-456", "Programming", true, 1, LocalDate.now()));
        bookManager.getAllBooks().add(new BookManager.Book(2, "Python Basics", "Jane Smith", 2020, "789-012", "Programming", true, 1, LocalDate.now()));
        bookManager.getAllBooks().add(new BookManager.Book(3, "Advanced Java", "John Doe", 2021, "345-678", "Programming", false, 1, LocalDate.now()));
        bookManager.getAllBooks().add(new BookManager.Book(4, "Data Structures", "Jane Smith", 2022, "901-234", "Computer Science", true, 1, LocalDate.now()));
    }

    /**
     * Test 2-1: Search only by title
     * Expected: Returns list of books whose title contains the input string
     */
    @Test
    void testSearchByTitleOnly() {
        List<BookManager.Book> results = bookManager.searchByTitle("Java");
        assertNotNull(results, "Search results should not be null");
        assertEquals(2, results.size(), "Should find 2 books with 'Java' in title");
        // Verify all results contain "Java" in title (case-insensitive)
        results.forEach(book -> 
            assertTrue(book.title().toLowerCase().contains("java"), 
                "All results should contain 'Java' in title")
        );
        // Verify specific books
        assertTrue(results.stream().anyMatch(b -> b.title().equals("Java Programming")));
        assertTrue(results.stream().anyMatch(b -> b.title().equals("Advanced Java")));
    }

    /**
     * Test 2-2: Search with combination of author and publication year
     * Expected: Returns list of books by that author in that specific year
     */
    @Test
    void testSearchByAuthorAndYear() {
        List<BookManager.Book> results = bookManager.combinedSearch(null, "2020", "John Doe");
        assertNotNull(results);
        assertEquals(1, results.size(), "Should find 1 book by John Doe in 2020");
        // Verify all results match author and year
        results.forEach(book -> {
            assertTrue(book.author().toLowerCase().contains("john doe".toLowerCase()),
                "All results should match the author");
            assertEquals(2020, book.publicationYear(), "All results should match the year");
        });
        // Verify it's the correct book
        assertEquals("Java Programming", results.get(0).title());
    }

    /**
     * Test 2-3: Search without any criteria (all parameters are null)
     * Expected: All available books are returned
     */
    @Test
    void testSearchWithAllNullParameters() {
        List<BookManager.Book> results = bookManager.combinedSearch(null, null, null);
        assertNotNull(results);
        // Should return all books when all parameters are null
        List<BookManager.Book> allBooks = bookManager.getAllBooks();
        assertEquals(allBooks.size(), results.size(), 
            "Search with all null parameters should return all books");
    }

    /**
     * Test 2-4: Search that matches no books
     * Expected: An empty list is returned
     */
    @Test
    void testSearchWithNoMatches() {
        List<BookManager.Book> results = bookManager.combinedSearch("NonExistentBook", "9999", "NonExistentAuthor");
        assertNotNull(results);
        assertTrue(results.isEmpty(), "Search with no matches should return empty list");
    }
}

