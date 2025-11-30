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
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private LocalDate addedDate;
    
    @OneToMany(mappedBy = "addedBy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Book> booksAdded = new ArrayList<>();
    
    @OneToMany(mappedBy = "approvedBy")
    @Builder.Default
    private List<BorrowRequest> approvedRequests = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (addedDate == null) {
            addedDate = LocalDate.now();
        }
    }
}

