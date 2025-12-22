package com.university.library.service;

import com.university.library.dto.response.SummaryStatsResponse;

public interface StatisticsService {
    
    SummaryStatsResponse getSummaryStats();
    
    Object getBorrowStats();
    
    Object getEmployeePerformance(Long employeeId);
    
    Object getTopDelayedStudents();
}
}

