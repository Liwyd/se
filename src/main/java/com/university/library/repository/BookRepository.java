package com.university.library.repository;

import com.university.library.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByPublicationYear(Integer year);
    List<Book> findByIsAvailableTrue();
    List<Book> findByIsAvailableFalse();
    List<Book> findByAddedById(Long employeeId);
    
    boolean existsByIsbn(String isbn);
    
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:year IS NULL OR b.publicationYear = :year) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    List<Book> searchBooks(@Param("title") String title, 
                          @Param("year") String year, 
                          @Param("author") String author,
                          Pageable pageable);
}

