package com.university.library.controller;

import com.university.library.dto.request.RegisterRequest;
import com.university.library.entity.Employee;
import com.university.library.exception.BadRequestException;
import com.university.library.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/employees")
    @Operation(summary = "Add a new employee")
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        Employee employee = Employee.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        
        employee = employeeRepository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }
    
}

