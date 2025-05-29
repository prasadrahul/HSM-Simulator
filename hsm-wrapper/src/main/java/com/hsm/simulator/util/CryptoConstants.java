package com.hsm.simulator.util;

public class CryptoConstants {

    private CryptoConstants() {
        // Prevent instantiation
    }


    public static final String AES_ALGORITHM = "AES";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String HMAC_ALGORITHM = "HmacSHA256";
    public static final String CMAC_ALGORITHM = "AESCMAC";
    public static final String AES_TRANSFORMATION ="AES/CBC/PKCS5Padding";
    public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    public static final String RSA_SIGNATURE_ALGORITHM = "SHA256withRSA";
    public static final String PKCS11_PROVIDER = "PKCS11";
}
