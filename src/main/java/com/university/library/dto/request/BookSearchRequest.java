package com.university.library.dto.request;

import lombok.Data;

@Data
public class BookSearchRequest {
    private String title;
    private String year;
    private String author;
}

