package com.university.library.service;

import com.university.library.dto.request.BookRequest;
import com.university.library.dto.request.BookSearchRequest;
import com.university.library.dto.response.BookResponse;
import com.university.library.entity.Book;
import com.university.library.entity.Employee;
import com.university.library.exception.BadRequestException;
import com.university.library.exception.ResourceNotFoundException;
import com.university.library.repository.BookRepository;
import com.university.library.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    
    private final BookRepository bookRepository;
    private final EmployeeRepository employeeRepository;
    
    @Transactional
    public BookResponse addBook(BookRequest request, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Book with this ISBN already exists");
        }
        
        Book book = Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publicationYear(request.getPublicationYear())
                .isbn(request.getIsbn())
                .category(request.getCategory())
                .isAvailable(true)
                .addedBy(employee)
                .build();
        
        book = bookRepository.save(book);
        return mapToResponse(book);
    }
    
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        return mapToResponse(book);
    }
    
    public List<BookResponse> searchBooks(BookSearchRequest request, Pageable pageable) {
        List<Book> books = bookRepository.searchBooks(
                request.getTitle(),
                request.getYear(),
                request.getAuthor(),
                pageable
        );
        return books.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByIsAvailableTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        
        book = bookRepository.save(book);
        return mapToResponse(book);
    }
    
    @Transactional
    public void setBookAvailability(Long id, Boolean isAvailable) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setIsAvailable(isAvailable);
        bookRepository.save(book);
    }
    
    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .isAvailable(book.getIsAvailable())
                .addedBy(book.getAddedBy().getId())
                .addedDate(book.getAddedDate())
                .build();
    }
}