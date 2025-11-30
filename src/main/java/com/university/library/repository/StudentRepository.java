package com.university.library.repository;

import com.university.library.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    boolean existsByUsername(String username);
    List<Student> findByIsActiveTrue();
    List<Student> findByIsActiveFalse();
}

