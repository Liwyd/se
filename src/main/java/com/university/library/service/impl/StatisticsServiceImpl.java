package com.university.library.service.impl;

import com.university.library.dto.response.SummaryStatsResponse;
import com.university.library.entity.BorrowRequest;
import com.university.library.entity.Student;
import com.university.library.repository.BorrowRequestRepository;
import com.university.library.repository.BookRepository;
import com.university.library.repository.StudentRepository;
import com.university.library.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    
    @Override
    public SummaryStatsResponse getSummaryStats() {
        long totalStudents = studentRepository.count();
        List<Student> allStudents = studentRepository.findAll();
        long activeStudents = allStudents.stream()
                .filter(Student::getIsActive)
                .count();
        long inactiveStudents = totalStudents - activeStudents;
        
        long totalBooks = bookRepository.count();
        long totalBorrows = borrowRequestRepository.count();
        long currentBorrows = borrowRequestRepository.findByStatus(BorrowRequest.BorrowStatus.BORROWED).size();
        
        return SummaryStatsResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .inactiveStudents(inactiveStudents)
                .totalBooks(totalBooks)
                .totalBorrows(totalBorrows)
                .currentBorrows(currentBorrows)
                .build();
    }
    
    @Override
    public Object getBorrowStats() {
        List<BorrowRequest> allBorrows = borrowRequestRepository.findAll();
        long totalRequests = allBorrows.size();
        long totalBorrowed = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRequest.BorrowStatus.BORROWED || 
                             b.getStatus() == BorrowRequest.BorrowStatus.RETURNED)
                .count();
        
        List<BorrowRequest> returnedBorrows = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRequest.BorrowStatus.RETURNED)
                .toList();
        
        int averageDays = 0;
        if (!returnedBorrows.isEmpty()) {
            long totalDays = 0;
            for (BorrowRequest b : returnedBorrows) {
                long days = b.getReturnDate().isAfter(b.getStartDate()) ? 
                    java.time.temporal.ChronoUnit.DAYS.between(b.getStartDate(), b.getReturnDate()) : 
                    java.time.temporal.ChronoUnit.DAYS.between(b.getStartDate(), b.getEndDate());
                totalDays += days;
            }
            averageDays = Math.round((float) totalDays / returnedBorrows.size());
        }
        
        long currentBorrows = allBorrows.stream()
                .filter(b -> b.getStatus() == BorrowRequest.BorrowStatus.BORROWED)
                .count();
        
        Map<String, Object> result = Map.of(
            "totalRequests", totalRequests,
            "totalBorrowed", totalBorrowed,
            "averageDays", averageDays,
            "currentBorrows", currentBorrows
        );
        
        return result;
    }
    
    @Override
    public Object getEmployeePerformance(Long employeeId) {
        // This would need an Employee entity and related repositories
        // For now, returning a placeholder implementation
        return Map.of("message", "Employee performance data for ID: " + employeeId);
    }
    
    @Override
    public Object getTopDelayedStudents() {
        List<Student> allStudents = studentRepository.findAll();
        
        // Create a map of student id to delay count
        Map<Long, Long> delayCounts = borrowRequestRepository.findAll().stream()
                .filter(br -> Boolean.TRUE.equals(br.getIsDelayed()))
                .collect(Collectors.groupingBy(
                    br -> br.getStudent().getId(),
                    Collectors.counting()
                ));
        
        // Sort students by delay count in descending order
        List<Map<String, Object>> topDelayed = allStudents.stream()
                .filter(s -> delayCounts.containsKey(s.getId()))
                .sorted((s1, s2) -> {
                    Long s2Count = delayCounts.get(s2.getId());
                    Long s1Count = delayCounts.get(s1.getId());
                    return s2Count.compareTo(s1Count);
                })
                .limit(10)
                .map(s -> Map.of(
                    "id", s.getId(),
                    "username", s.getUsername(),
                    "delayCount", delayCounts.get(s.getId())
                ))
                .collect(Collectors.toList());
        
        return topDelayed;
    }
}