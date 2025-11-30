package com.university.library.repository;

import com.university.library.entity.BorrowRequest;
import com.university.library.entity.BorrowRequest.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRequestRepository extends JpaRepository<BorrowRequest, Long> {
    List<BorrowRequest> findByStudentId(Long studentId);
    List<BorrowRequest> findByStatus(BorrowStatus status);
    List<BorrowRequest> findByBookId(Long bookId);
    List<BorrowRequest> findByApprovedById(Long employeeId);
    
    @Query("SELECT br FROM BorrowRequest br WHERE br.status = :status AND br.approvedById = :employeeId")
    List<BorrowRequest> findByStatusAndApprovedBy(@Param("status") BorrowStatus status, 
                                                   @Param("employeeId") Long employeeId);
    
    @Query("SELECT br FROM BorrowRequest br WHERE br.status = 'PENDING' AND br.startDate <= :today")
    List<BorrowRequest> findPendingRequestsReadyForReview(@Param("today") LocalDate today);
    
    @Query("SELECT br FROM BorrowRequest br WHERE br.studentId = :studentId AND br.isDelayed = true")
    List<BorrowRequest> findDelayedRequestsByStudent(@Param("studentId") Long studentId);
}

