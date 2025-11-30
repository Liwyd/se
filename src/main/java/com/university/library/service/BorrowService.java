package com.university.library.service;

import com.university.library.dto.request.BorrowRequestDto;
import com.university.library.dto.response.BorrowRequestResponse;
import com.university.library.entity.Book;
import com.university.library.entity.BorrowRequest;
import com.university.library.entity.Employee;
import com.university.library.entity.Student;
import com.university.library.entity.BorrowRequest.BorrowStatus;
import com.university.library.exception.BadRequestException;
import com.university.library.exception.ResourceNotFoundException;
import com.university.library.repository.BookRepository;
import com.university.library.repository.BorrowRequestRepository;
import com.university.library.repository.EmployeeRepository;
import com.university.library.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowService {
    
    private final BorrowRequestRepository borrowRequestRepository;
    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final EmployeeRepository employeeRepository;
    
    @Transactional
    public BorrowRequestResponse createBorrowRequest(BorrowRequestDto request, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!student.getIsActive()) {
            throw new BadRequestException("Student account is inactive");
        }
        
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        
        if (!book.getIsAvailable()) {
            throw new BadRequestException("Book is not available");
        }
        
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Start date cannot be in the past");
        }
        
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }
        
        BorrowRequest borrowRequest = BorrowRequest.builder()
                .student(student)
                .book(book)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BorrowStatus.PENDING)
                .build();
        
        borrowRequest = borrowRequestRepository.save(borrowRequest);
        return mapToResponse(borrowRequest);
    }
    
    @Transactional
    public BorrowRequestResponse approveRequest(Long requestId, Long employeeId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));
        
        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new BadRequestException("Request is not in PENDING status");
        }
        
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        
        request.setStatus(BorrowStatus.APPROVED);
        request.setApprovedBy(employee);
        request.setApprovedDate(LocalDate.now());
        
        request = borrowRequestRepository.save(request);
        return mapToResponse(request);
    }
    
    @Transactional
    public BorrowRequestResponse rejectRequest(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));
        
        if (request.getStatus() != BorrowStatus.PENDING) {
            throw new BadRequestException("Request is not in PENDING status");
        }
        
        request.setStatus(BorrowStatus.REJECTED);
        request = borrowRequestRepository.save(request);
        return mapToResponse(request);
    }
    
    @Transactional
    public BorrowRequestResponse borrowBook(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));
        
        if (request.getStatus() != BorrowStatus.APPROVED) {
            throw new BadRequestException("Request must be APPROVED before borrowing");
        }
        
        request.setStatus(BorrowStatus.BORROWED);
        request.getBook().setIsAvailable(false);
        
        request = borrowRequestRepository.save(request);
        return mapToResponse(request);
    }
    
    @Transactional
    public BorrowRequestResponse returnBook(Long requestId) {
        BorrowRequest request = borrowRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow request not found"));
        
        if (request.getStatus() != BorrowStatus.BORROWED) {
            throw new BadRequestException("Book is not currently borrowed");
        }
        
        request.setStatus(BorrowStatus.RETURNED);
        request.setReturnDate(LocalDate.now());
        
        if (LocalDate.now().isAfter(request.getEndDate())) {
            request.setIsDelayed(true);
        }
        
        request.getBook().setIsAvailable(true);
        
        request = borrowRequestRepository.save(request);
        return mapToResponse(request);
    }
    
    public List<BorrowRequestResponse> getStudentRequests(Long studentId) {
        return borrowRequestRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<BorrowRequestResponse> getPendingRequests() {
        return borrowRequestRepository.findByStatus(BorrowStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private BorrowRequestResponse mapToResponse(BorrowRequest request) {
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

