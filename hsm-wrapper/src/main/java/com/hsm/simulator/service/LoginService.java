package com.hsm.simulator.service;

import org.springframework.web.multipart.MultipartFile;

public interface LoginService {

    boolean handleLogin(String username, String password,
                        MultipartFile keyStore, String keyStorePassword,
                        MultipartFile trustStore, String trustStorePassword);
}
