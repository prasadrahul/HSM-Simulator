package com.hsm.simulator.exception;

import com.hsm.simulator.model.HsmApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CryptoException.class)
    public ResponseEntity<HsmApiResponse<Object>> handleCryptoException(CryptoException ex) {
        return ResponseEntity.badRequest().body(new HsmApiResponse<>(false, ex.getMessage(), null));
    }
}