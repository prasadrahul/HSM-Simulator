package com.hsm.simulator.util;

public enum SymmetricKeyType {

    AES_128("AES:16"),
    AES_192("AES:24"),
    AES_256("AES:32"),

    DES("DES:8"),
    DES3("DES3:24");

    private final String keySpec;

    SymmetricKeyType(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return keySpec;
    }



}
