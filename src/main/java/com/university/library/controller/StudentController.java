package com.university.library.controller;

import com.university.library.dto.response.StudentReportResponse;
import com.university.library.dto.response.StudentResponse;
import com.university.library.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    
    private final StudentService studentService;
    
    @GetMapping("/public/count")
    @Operation(summary = "Get student count (Public)")
    public ResponseEntity<StudentCountResponse> getStudentCount() {
        List<StudentResponse> all = studentService.getAllStudents();
        List<StudentResponse> active = studentService.getActiveStudents();
        return ResponseEntity.ok(new StudentCountResponse(
                all.size(),
                active.size(),
                all.size() - active.size()
        ));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get all students")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get active students")
    public ResponseEntity<List<StudentResponse>> getActiveStudents() {
        List<StudentResponse> students = studentService.getActiveStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }
    
    @GetMapping("/{id}/report")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Get student report")
    public ResponseEntity<StudentReportResponse> getStudentReport(@PathVariable Long id) {
        StudentReportResponse report = studentService.getStudentReport(id);
        return ResponseEntity.ok(report);
    }
    
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Toggle student active status")
    public ResponseEntity<StudentResponse> toggleStudentStatus(@PathVariable Long id) {
        StudentResponse student = studentService.toggleStudentStatus(id);
        return ResponseEntity.ok(student);
    }
    
    private record StudentCountResponse(long total, long active, long inactive) {}
}

