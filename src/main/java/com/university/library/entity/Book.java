package com.university.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 100)
    private String author;
    
    @Column(nullable = false)
    private Integer publicationYear;
    
    @Column(unique = true, nullable = false, length = 50)
    private String isbn;
    
    @Column(length = 100)
    private String category;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by", nullable = false)
    private Employee addedBy;
    
    @Column(nullable = false)
    private LocalDate addedDate;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @Builder.Default
    private List<BorrowRequest> borrowRequests = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (addedDate == null) {
            addedDate = LocalDate.now();
        }
    }
}

