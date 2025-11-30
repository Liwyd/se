package com.university.library.service;

import com.university.library.dto.response.StudentReportResponse;
import com.university.library.dto.response.StudentResponse;
import com.university.library.entity.Student;
import com.university.library.exception.ResourceNotFoundException;
import com.university.library.repository.BorrowRequestRepository;
import com.university.library.repository.StudentRepository;
import com.university.library.service.mapper.BorrowRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRequestMapper borrowRequestMapper;
    
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<StudentResponse> getActiveStudents() {
        return studentRepository.findByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return mapToResponse(student);
    }
    
    @Transactional
    public StudentResponse toggleStudentStatus(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        student.setIsActive(!student.getIsActive());
        student = studentRepository.save(student);
        return mapToResponse(student);
    }
    
    public StudentReportResponse getStudentReport(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        var borrowHistory = borrowRequestRepository.findByStudentId(id);
        long totalBorrows = borrowHistory.size();
        long notReturned = borrowHistory.stream()
                .filter(br -> br.getStatus() == com.university.library.entity.BorrowRequest.BorrowStatus.BORROWED)
                .count();
        long delayed = borrowHistory.stream()
                .filter(br -> Boolean.TRUE.equals(br.getIsDelayed()))
                .count();
        
        return StudentReportResponse.builder()
                .studentId(student.getId())
                .username(student.getUsername())
                .isActive(student.getIsActive())
                .totalBorrows(totalBorrows)
                .notReturnedCount(notReturned)
                .delayedCount(delayed)
                .borrowHistory(borrowHistory.stream()
                        .map(borrowRequestMapper::toResponse)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .username(student.getUsername())
                .isActive(student.getIsActive())
                .registrationDate(student.getRegistrationDate())
                .build();
    }
}

