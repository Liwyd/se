package com.university.library.controller;

import com.university.library.dto.request.BorrowRequestDto;
import com.university.library.dto.response.BorrowRequestResponse;
import com.university.library.security.JwtTokenProvider;
import com.university.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@Tag(name = "Borrows", description = "Borrow management APIs")
public class BorrowController {
    
    private final BorrowService borrowService;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/request")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Create a borrow request")
    public ResponseEntity<BorrowRequestResponse> createBorrowRequest(
            @Valid @RequestBody BorrowRequestDto request,
            @RequestHeader("Authorization") String token) {
        Long studentId = tokenProvider.getUserIdFromToken(token.substring(7));
        BorrowRequestResponse response = borrowService.createBorrowRequest(request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/requests/pending")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get pending borrow requests")
    public ResponseEntity<List<BorrowRequestResponse>> getPendingRequests() {
        List<BorrowRequestResponse> requests = borrowService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }
    
    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Approve a borrow request")
    public ResponseEntity<BorrowRequestResponse> approveRequest(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long employeeId = tokenProvider.getUserIdFromToken(token.substring(7));
        BorrowRequestResponse response = borrowService.approveRequest(id, employeeId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Reject a borrow request")
    public ResponseEntity<BorrowRequestResponse> rejectRequest(@PathVariable Long id) {
        BorrowRequestResponse response = borrowService.rejectRequest(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Return a borrowed book")
    public ResponseEntity<BorrowRequestResponse> returnBook(@PathVariable Long id) {
        BorrowRequestResponse response = borrowService.returnBook(id);
        return ResponseEntity.ok(response);
    }
}

