package com.university.library.service;

import com.university.library.dto.response.StatisticsResponse;
import com.university.library.dto.response.StudentDelayInfo;
import com.university.library.entity.BorrowRequest;
import com.university.library.entity.BorrowRequest.BorrowStatus;
import com.university.library.repository.BookRepository;
import com.university.library.repository.BorrowRequestRepository;
import com.university.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    
    public StatisticsResponse getLibraryStatistics() {
        long totalStudents = studentRepository.count();
        long activeStudents = studentRepository.findByIsActiveTrue().size();
        long inactiveStudents = totalStudents - activeStudents;
        
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.findByIsAvailableTrue().size();
        long borrowedBooks = totalBooks - availableBooks;
        
        long totalBorrowRequests = borrowRequestRepository.count();
        long currentBorrows = borrowRequestRepository.findByStatus(BorrowStatus.BORROWED).size();
        
        // Calculate average borrow days
        List<BorrowRequest> returnedRequests = borrowRequestRepository.findByStatus(BorrowStatus.RETURNED);
        double averageDays = 0.0;
        if (!returnedRequests.isEmpty()) {
            long totalDays = returnedRequests.stream()
                    .filter(br -> br.getReturnDate() != null && br.getStartDate() != null)
                    .mapToLong(br -> ChronoUnit.DAYS.between(br.getStartDate(), br.getReturnDate()))
                    .sum();
            averageDays = (double) totalDays / returnedRequests.size();
        }
        
        // Top 10 students with most delays
        List<StudentDelayInfo> topDelayed = studentRepository.findAll().stream()
                .map(student -> {
                    long delayCount = borrowRequestRepository.findDelayedRequestsByStudent(student.getId()).size();
                    return new StudentDelayInfo(student.getUsername(), delayCount);
                })
                .filter(info -> info.getDelayCount() > 0)
                .sorted((a, b) -> Long.compare(b.getDelayCount(), a.getDelayCount()))
                .limit(10)
                .collect(Collectors.toList());
        
        return StatisticsResponse.builder()
                .totalStudents(totalStudents)
                .activeStudents(activeStudents)
                .inactiveStudents(inactiveStudents)
                .totalBooks(totalBooks)
                .availableBooks(availableBooks)
                .borrowedBooks(borrowedBooks)
                .totalBorrowRequests(totalBorrowRequests)
                .currentBorrows(currentBorrows)
                .averageBorrowDays(averageDays)
                .topDelayedStudents(topDelayed)
                .build();
    }
}

