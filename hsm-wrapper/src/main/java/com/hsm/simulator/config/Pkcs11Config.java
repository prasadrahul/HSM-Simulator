package com.hsm.simulator.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AuthProvider;
import java.security.KeyStore;
import java.security.Security;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class Pkcs11Config {

    private AuthProvider pkcs11Provider;

    @Value("${pkcs11.config.filepath}")
    private String configName;

    @Value("${pkcs11.pin}")
    private String pin;

    @Value("${pkcs11.tool.path}")
    private String pkcs11ToolPath;

    @Value("${pkcs11.module.path}")
    private String pkcs11ModulePath;

    public AuthProvider getPkcs11Provider() {
        return pkcs11Provider;
    }

    public String getHsmPin() {
        return pin;
    }

    public String getPkcs11ToolPath() {
        return pkcs11ToolPath;
    }

    public String getPkcs11ModulePath() {
        return pkcs11ModulePath;
    }

    public String getSlotOrSlotId() {
        try (BufferedReader reader = new BufferedReader(new FileReader(configName))) {
            Properties properties = new Properties();
            properties.load(reader);
            String slotLine =  properties.getProperty("slot") != null
                    ? properties.getProperty("slot")
                    : String.valueOf(getSlotDetailsFromPkcs11Tool());
            return slotLine.split("[\\s]+")[0].trim();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read PKCS#11 configuration file", e);
        }
    }


    @PostConstruct
    public void initProvider() {

        try {
            this.pkcs11Provider = (AuthProvider) Security.getProvider("SunPKCS11").configure(configName);
            Security.addProvider(pkcs11Provider);

            KeyStore ks = KeyStore.getInstance("PKCS11", pkcs11Provider);
            ks.load(null, pin.toCharArray());

            // List aliases (keys)
            java.util.Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                System.out.println("Key alias: " + alias);
            }

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to initialize PKCS#11 provider", ex);
        }
    }


    private int getSlotDetailsFromPkcs11Tool() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(pkcs11ToolPath, "--module", pkcs11ModulePath, "--list-slots");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                String currentSlotHex = null;

                Pattern slotPattern = Pattern.compile("^Slot \\d+ \\((0x[0-9a-fA-F]+)\\):");
                Pattern labelPattern = Pattern.compile("token label\\s*:\\s*(.*)");

                while ((line = reader.readLine()) != null) {
                    Matcher slotMatcher = slotPattern.matcher(line);
                    if (slotMatcher.find()) {
                        currentSlotHex = slotMatcher.group(1);
                    } else {
                        Matcher labelMatcher = labelPattern.matcher(line);
                        if (labelMatcher.find()) {
                            String label = labelMatcher.group(1).trim();
                            if (label.equals("Docker-Token") && currentSlotHex != null) {
                                return Integer.parseUnsignedInt(currentSlotHex.substring(2), 16);
                            }
                        }
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("pkcs11-tool exited with code " + exitCode);
                }

            } catch (Exception e){
                throw new RuntimeException("Failed to read slot details", e);
            }
            return -1; // Default value if no matching slot is found
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute pkcs11-tool", e);
        }
    }
}