package com.hsm.simulator.controller;

import com.hsm.simulator.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/auth/login", consumes = "multipart/form-data")
    public ResponseEntity<String> handleLogin(
            @RequestPart String username,
            @RequestPart String password,
            @RequestPart(required = false) MultipartFile keyStore,
            @RequestPart(required = false) String keyStorePassword,
            @RequestPart(required = false) MultipartFile trustStore,
            @RequestPart(required = false) String trustStorePassword) {
        boolean isAuthenticated = loginService.handleLogin(username, password, keyStore, keyStorePassword, trustStore, trustStorePassword);
        if (isAuthenticated) {
            return ResponseEntity.ok("Authenticated successfully");
        } else {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}
