package com.university.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private Integer publicationYear;
    private String isbn;
    private String category;
    private Boolean isAvailable;
    private Long addedBy;
    private LocalDate addedDate;
}

