package com.university.library.controller;

import com.university.library.dto.request.ChangePasswordRequest;
import com.university.library.dto.request.LoginRequest;
import com.university.library.dto.request.RegisterRequest;
import com.university.library.dto.response.AuthResponse;
import com.university.library.entity.Employee;
import com.university.library.exception.BadRequestException;
import com.university.library.repository.EmployeeRepository;
import com.university.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {
    
    private final AuthService authService;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new student")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerStudent(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login (Student, Employee, or Admin)")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    @Operation(summary = "Change password (Employee/Admin)")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                           @RequestHeader("Authorization") String token) {
        // Extract user ID from token
        String tokenValue = token.substring(7); // Remove "Bearer " prefix
        Long userId = authService.getUserIdFromToken(tokenValue);
        String role = authService.getRoleFromToken(tokenValue);
        
        if (!authService.validatePasswordChange(userId, role, request.getCurrentPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException("New passwords do not match");
        }
        
        authService.updatePassword(userId, role, request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}