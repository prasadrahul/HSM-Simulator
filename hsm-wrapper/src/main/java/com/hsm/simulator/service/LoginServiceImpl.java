package com.hsm.simulator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LoginServiceImpl implements LoginService {

    public boolean handleLogin(String username, String password,
                               MultipartFile keyStore, String keyStorePassword,
                               MultipartFile trustStore, String trustStorePassword) {
        return "admin".equals(username) && "password".equals(password);
    }
}
