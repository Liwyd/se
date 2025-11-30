package com.university.library.dto.response;

import com.university.library.entity.BorrowRequest.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRequestResponse {
    private Long id;
    private Long studentId;
    private String studentUsername;
    private Long bookId;
    private String bookTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private BorrowStatus status;
    private LocalDate requestDate;
    private Long approvedBy;
    private LocalDate approvedDate;
    private LocalDate returnDate;
    private Boolean isDelayed;
}

