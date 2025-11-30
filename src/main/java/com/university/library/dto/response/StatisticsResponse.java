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
public class StatisticsResponse {
    private Long totalStudents;
    private Long activeStudents;
    private Long inactiveStudents;
    private Long totalBooks;
    private Long availableBooks;
    private Long borrowedBooks;
    private Long totalBorrowRequests;
    private Long currentBorrows;
    private Double averageBorrowDays;
    private List<StudentDelayInfo> topDelayedStudents;
}

