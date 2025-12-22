package com.university.library.controller;

import com.university.library.dto.response.SummaryStatsResponse;
import com.university.library.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Statistics and reports APIs")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping("/stats/summary")
    @Operation(summary = "Get summary statistics")
    public ResponseEntity<SummaryStatsResponse> getSummaryStats() {
        SummaryStatsResponse response = statisticsService.getSummaryStats();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/borrows")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get advanced borrow statistics")
    public ResponseEntity<Object> getBorrowStats() {
        Object response = statisticsService.getBorrowStats();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/employees/{id}/performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get employee performance report")
    public ResponseEntity<Object> getEmployeePerformance(@PathVariable Long id) {
        Object response = statisticsService.getEmployeePerformance(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats/top-delayed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get top delayed students")
    public ResponseEntity<Object> getTopDelayedStudents() {
        Object response = statisticsService.getTopDelayedStudents();
        return ResponseEntity.ok(response);
    }
}