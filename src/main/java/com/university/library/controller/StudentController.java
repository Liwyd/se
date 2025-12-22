package com.university.library.controller;

import com.university.library.dto.response.StudentResponse;
import com.university.library.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management APIs")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get student profile")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long id) {
        StudentResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Toggle student status (active/inactive)")
    public ResponseEntity<StudentResponse> toggleStudentStatus(@PathVariable Long id) {
        StudentResponse response = studentService.toggleStudentStatus(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/borrow-history")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get student borrow history")
    public ResponseEntity<Object> getStudentBorrowHistory(@PathVariable Long id) {
        Object response = studentService.getStudentReport(id);
        return ResponseEntity.ok(response);
    }
}

