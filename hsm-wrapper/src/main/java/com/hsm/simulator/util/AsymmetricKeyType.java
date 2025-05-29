package com.hsm.simulator.util;

public enum AsymmetricKeyType {

    RSA_512("rsa:512"),
    RSA_1024("rsa:1024"),
    RSA_2048("rsa:2048"),
    RSA_4096("rsa:4096"),
    RSA_8192("rsa:8192"),
    RSA_16384("rsa:16384"),

    EC_112("EC:secp112r1"),
    EC_256("EC:prime256v1"),
    EC_384("EC:secp384r1"),
    EC_521("EC:secp521r1"),

    DH_512("dh:512"),
    DH_1024("dh:1024"),
    DH_2048("dh:2048"),
    DH_4096("dh:4096"),
    DH_8192("dh:8192"),
    DH_10000("dh:10000"),

    DSA_512("dsa:512"),
    DSA_1024("dsa:1024");

    private final String keySpec;

    AsymmetricKeyType(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return keySpec;
    }
}