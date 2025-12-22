package com.university.library.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryStatsResponse {
    private long totalStudents;
    private long totalBooks;
    private long totalBorrows;
    private long currentBorrows;
    private long activeStudents;
    private long inactiveStudents;
}