package com.university.library.service.mapper;

import com.university.library.dto.response.BorrowRequestResponse;
import com.university.library.entity.BorrowRequest;
import org.springframework.stereotype.Component;

@Component
public class BorrowRequestMapper {
    
    public BorrowRequestResponse toResponse(BorrowRequest request) {
        return BorrowRequestResponse.builder()
                .id(request.getId())
                .studentId(request.getStudent().getId())
                .studentUsername(request.getStudent().getUsername())
                .bookId(request.getBook().getId())
                .bookTitle(request.getBook().getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .requestDate(request.getRequestDate())
                .approvedBy(request.getApprovedBy() != null ? request.getApprovedBy().getId() : null)
                .approvedDate(request.getApprovedDate())
                .returnDate(request.getReturnDate())
                .isDelayed(request.getIsDelayed())
                .build();
    }
}

