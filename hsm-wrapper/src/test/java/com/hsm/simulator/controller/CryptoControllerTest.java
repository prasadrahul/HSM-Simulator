package com.hsm.simulator.controller;

import com.hsm.simulator.model.HsmApiResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class CryptoControllerTest {

    @Container
    private static final GenericContainer<?> app = new GenericContainer<>("hsm-simulator-test:latest")
            .withExposedPorts(8080);

    static String baseUrl;
    static TestRestTemplate restTemplate;

    @BeforeAll
    static void beforeAll() {
        app.start();
        Integer port = app.getMappedPort(8080);
        baseUrl = "http://" + app.getHost() + ":" + port + "/crypto";
        restTemplate = new TestRestTemplate();
    }

    @AfterAll
    static void afterAll() {
        app.stop();
    }

    @Test
    void test_encrypt() {
        Map<String, String> req = new HashMap<>();
        req.put("plainText", "hello");
        ResponseEntity<HsmApiResponse> resp = restTemplate.postForEntity(
                baseUrl + "/encrypt/AES", req, HsmApiResponse.class);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().isSuccess());
        assertNotNull(resp.getBody().getData());
    }

    @Test
    void test_decrypt() {
        // First encrypt, then decrypt
        Map<String, String> req = new HashMap<>();
        req.put("plainText", "Test Message");
        ResponseEntity<HsmApiResponse> encResp = restTemplate.postForEntity(
                baseUrl + "/encrypt/AES", req, HsmApiResponse.class);
        assertNotNull(encResp.getBody());
        assertTrue(encResp.getBody().isSuccess());
        String encrypted = (String) encResp.getBody().getData();
        System.out.println("Encrypted: " + encrypted);

        Map<String, String> decReq = new HashMap<>();
        decReq.put("base64Encrypted", encrypted);
        ResponseEntity<HsmApiResponse> decResp = restTemplate.postForEntity(
                baseUrl + "/decrypt/AES", decReq, HsmApiResponse.class);
        assertNotNull(decResp.getBody());
        assertTrue(decResp.getBody().isSuccess());
        assertEquals("Test Message", decResp.getBody().getData());
    }

    @Test
    void test_sign() {
        Map<String, String> req = new HashMap<>();
        req.put("message", "test");
        ResponseEntity<HsmApiResponse> resp = restTemplate.postForEntity(
                baseUrl + "/sign/RSA", req, HsmApiResponse.class);
        assertTrue(resp.getBody().isSuccess());
        assertNotNull(resp.getBody().getData());
    }

    @Test
    void test_verify() {
        // First sign, then verify
        Map<String, String> req = new HashMap<>();
        req.put("message", "test");
        ResponseEntity<HsmApiResponse> signResp = restTemplate.postForEntity(
                baseUrl + "/sign/RSA", req, HsmApiResponse.class);
        String signature = (String) signResp.getBody().getData();

        Map<String, String> verifyReq = new HashMap<>();
        verifyReq.put("message", "test");
        verifyReq.put("base64Signature", signature);
        ResponseEntity<HsmApiResponse> verifyResp = restTemplate.postForEntity(
                baseUrl + "/verify/RSA", verifyReq, HsmApiResponse.class);
        assertTrue(verifyResp.getBody().isSuccess());
        assertTrue((Boolean) verifyResp.getBody().getData());
    }

    @Test
    void test_generateRandom() {
        ResponseEntity<HsmApiResponse> resp = restTemplate.getForEntity(
                baseUrl + "/random/16", HsmApiResponse.class);
        assertTrue(resp.getBody().isSuccess());
        assertNotNull(resp.getBody().getData());
    }
}