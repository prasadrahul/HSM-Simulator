package com.hsm.simulator.config;

import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import sun.security.pkcs11.SunPKCS11;

@Configuration
public class PKCS11Config {
    public PKCS11Config() throws Exception {
        String config = "name=SoftHSM2\nlibrary=/usr/lib/softhsm/libsofthsm2.so\nslot=0";
        Provider provider = new SunPKCS11(new ByteArrayInputStream(config.getBytes()));
        Security.addProvider(provider);

        KeyStore ks = KeyStore.getInstance("PKCS11", provider);
        ks.load(null, "1234".toCharArray());
    }
}