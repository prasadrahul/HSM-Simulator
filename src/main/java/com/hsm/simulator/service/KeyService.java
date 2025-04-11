package com.hsm.simulator.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;

@Service
public class KeyService {
    public ResponseEntity<String> generateKey() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SunPKCS11-SoftHSM2");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            return ResponseEntity.ok("RSA key pair generated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Key generation failed: " + e.getMessage());
        }
    }
}