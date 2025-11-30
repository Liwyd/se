package com.university.library.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentReportResponse {
    private Long studentId;
    private String username;
    private Boolean isActive;
    private Long totalBorrows;
    private Long notReturnedCount;
    private Long delayedCount;
    private List<BorrowRequestResponse> borrowHistory;
}

