package com.university.library.controller;

import com.university.library.dto.response.StatisticsResponse;
import com.university.library.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Library statistics APIs")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    
    @GetMapping
    @Operation(summary = "Get library statistics (Public)")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse statistics = statisticsService.getLibraryStatistics();
        return ResponseEntity.ok(statistics);
    }
}

