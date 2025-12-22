package com.university.library.controller;

import com.university.library.dto.request.BookRequest;
import com.university.library.dto.request.BookSearchRequest;
import com.university.library.dto.response.BookResponse;
import com.university.library.security.JwtTokenProvider;
import com.university.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management APIs")
public class BookController {
    
    private final BookService bookService;
    private final JwtTokenProvider tokenProvider;
    
    @GetMapping
    @Operation(summary = "Get all books with optional search and filter")
    public ResponseEntity<List<BookResponse>> getAllBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        BookSearchRequest request = BookSearchRequest.builder()
                .title(title)
                .author(author)
                .year(year != null ? String.valueOf(year) : null)
                .build();
                
        Pageable pageable = PageRequest.of(page, size);
        List<BookResponse> books = bookService.searchBooks(request, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/search")
    @Operation(summary = "Advanced book search (by title, author, year)")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @ModelAttribute BookSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        List<BookResponse> books = bookService.searchBooks(request, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Add a new book")
    public ResponseEntity<BookResponse> addBook(@Valid @RequestBody BookRequest request,
                                                @RequestHeader("Authorization") String token) {
        Long employeeId = tokenProvider.getUserIdFromToken(token.substring(7));
        BookResponse book = bookService.addBook(request, employeeId);
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Update a book")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                    @Valid @RequestBody BookRequest request) {
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(book);
    }
    
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Update book availability")
    public ResponseEntity<Void> setBookAvailability(@PathVariable Long id,
                                                    @RequestParam Boolean isAvailable) {
        bookService.setBookAvailability(id, isAvailable);
        return ResponseEntity.ok().build();
    }
}

