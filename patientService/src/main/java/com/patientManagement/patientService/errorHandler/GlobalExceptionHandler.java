package com.patientManagement.patientService.errorHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.info("Email already exists: {}", ex.getMessage());
        error.put("message", "Email already exists");
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(PatientNotExists.class)
    public ResponseEntity<Map<String, String>> handlePatientNotExists(PatientNotExists ex){
        Map<String, String> error = new HashMap<>();
        Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);
        logger.error("patient don't exists : {}", ex.getMessage());
        error.put("message","Patient doesn't exists");
        return ResponseEntity.badRequest().body(error);
    }
}
