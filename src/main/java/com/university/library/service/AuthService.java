package com.university.library.service;

import com.university.library.dto.request.LoginRequest;
import com.university.library.dto.request.RegisterRequest;
import com.university.library.dto.response.AuthResponse;
import com.university.library.entity.Admin;
import com.university.library.entity.Employee;
import com.university.library.entity.Student;
import com.university.library.exception.BadRequestException;
import com.university.library.exception.ResourceNotFoundException;
import com.university.library.exception.UnauthorizedException;
import com.university.library.repository.AdminRepository;
import com.university.library.repository.EmployeeRepository;
import com.university.library.repository.StudentRepository;
import com.university.library.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final StudentRepository studentRepository;
    private final EmployeeRepository employeeRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    
    @Transactional
    public AuthResponse registerStudent(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        
        Student student = Student.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();
        
        student = studentRepository.save(student);
        
        String token = tokenProvider.generateToken(student.getUsername(), "STUDENT", student.getId());
        
        return AuthResponse.builder()
                .token(token)
                .username(student.getUsername())
                .role("STUDENT")
                .userId(student.getId())
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        // Try student
        Student student = studentRepository.findByUsername(request.getUsername())
                .orElse(null);
        if (student != null && passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            if (!student.getIsActive()) {
                throw new UnauthorizedException("Student account is inactive");
            }
            String token = tokenProvider.generateToken(student.getUsername(), "STUDENT", student.getId());
            return AuthResponse.builder()
                    .token(token)
                    .username(student.getUsername())
                    .role("STUDENT")
                    .userId(student.getId())
                    .build();
        }
        
        // Try employee
        Employee employee = employeeRepository.findByUsername(request.getUsername())
                .orElse(null);
        if (employee != null && passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            String token = tokenProvider.generateToken(employee.getUsername(), "EMPLOYEE", employee.getId());
            return AuthResponse.builder()
                    .token(token)
                    .username(employee.getUsername())
                    .role("EMPLOYEE")
                    .userId(employee.getId())
                    .build();
        }
        
        // Try admin
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElse(null);
        if (admin != null && passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            String token = tokenProvider.generateToken(admin.getUsername(), "ADMIN", admin.getId());
            return AuthResponse.builder()
                    .token(token)
                    .username(admin.getUsername())
                    .role("ADMIN")
                    .userId(admin.getId())
                    .build();
        }
        
        throw new UnauthorizedException("Invalid username or password");
    }
    
    public String getRoleFromToken(String token) {
        return tokenProvider.getRoleFromToken(token);
    }
    
    public Long getUserIdFromToken(String token) {
        return tokenProvider.getUserIdFromToken(token);
    }
    
    public boolean validatePasswordChange(Long userId, String role, String currentPassword) {
        switch (role) {
            case "EMPLOYEE":
                Employee employee = employeeRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
                return passwordEncoder.matches(currentPassword, employee.getPassword());
            case "ADMIN":
                Admin admin = adminRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
                return passwordEncoder.matches(currentPassword, admin.getPassword());
            default:
                throw new UnauthorizedException("Only employees and admins can change password");
        }
    }
    
    @Transactional
    public void updatePassword(Long userId, String role, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        switch (role) {
            case "EMPLOYEE":
                Employee employee = employeeRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
                employee.setPassword(encodedPassword);
                employeeRepository.save(employee);
                break;
            case "ADMIN":
                Admin admin = adminRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
                admin.setPassword(encodedPassword);
                adminRepository.save(admin);
                break;
            default:
                throw new UnauthorizedException("Only employees and admins can change password");
        }
    }
}