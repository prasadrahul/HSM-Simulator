package com.hsm.simulator.controller;

import com.hsm.simulator.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    @Autowired
    private KeyService keyService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateKey() {
        return keyService.generateKey();
    }
}