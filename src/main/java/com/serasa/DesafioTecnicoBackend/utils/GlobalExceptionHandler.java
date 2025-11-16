package com.serasa.DesafioTecnicoBackEnd.utils;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@OpenAPIDefinition
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex,
                                                                             HttpServletRequest request) {

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("status", ex.getStatusCode().value());
        errorBody.put("message", ex.getReason());
        errorBody.put("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode()).body(errorBody);
    }
}