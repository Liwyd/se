package com.university.library.exceptions;

public class InvalidStudentStatusException extends RuntimeException {
    public InvalidStudentStatusException(String message) {
        super(message);
    }
}

