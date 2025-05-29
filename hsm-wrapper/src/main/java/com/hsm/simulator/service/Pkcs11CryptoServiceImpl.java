package com.hsm.simulator.service;

import com.hsm.simulator.dto.Pkcs11ObjectInfo;
import com.hsm.simulator.dto.Pkcs11SlotInfo;
import com.hsm.simulator.util.CryptoUtils;
import com.hsm.simulator.util.Pkcs11ToolHandler;
import com.hsm.simulator.config.Pkcs11Config;
import com.hsm.simulator.exception.CryptoException;
import com.hsm.simulator.model.CryptoAlgorithm;
import com.hsm.simulator.util.CryptoConstants;
import com.hsm.simulator.util.AsymmetricKeyType;
import com.hsm.simulator.util.SymmetricKeyType;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.AuthProvider;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class Pkcs11CryptoServiceImpl implements Pkcs11CryptoService {

    private final AuthProvider hsmProvider;
    private KeyPair rsaKeyPair;
    private SecretKey pbkKeyPair;
    private SecretKey hmacKey;
    private SecretKey cmacKey;
    private final String hsmPin;
    private final String slotOrSlotId;
    private final Pkcs11ToolHandler pkcs11ToolHandler;
    private final CryptoUtils cryptoUtils;

    public Pkcs11CryptoServiceImpl(Pkcs11Config pkcs11Config, Pkcs11ToolHandler pkcs11ToolHandler, CryptoUtils cryptoUtils) {
        this.hsmProvider = pkcs11Config.getPkcs11Provider();
        this.hsmPin = pkcs11Config.getHsmPin();
        this.slotOrSlotId = pkcs11Config.getSlotOrSlotId();
        this.pkcs11ToolHandler = pkcs11ToolHandler;
        this.cryptoUtils = cryptoUtils;
    }

    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(CryptoConstants.RSA_ALGORITHM, hsmProvider);
        keyPairGen.initialize(2048);
        rsaKeyPair = keyPairGen.generateKeyPair();

        // Initialize HMAC key
        hmacKey = KeyGenerator.getInstance(CryptoConstants.HMAC_ALGORITHM, hsmProvider).generateKey();

        // Initialize CMAC key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(CryptoConstants.AES_ALGORITHM, hsmProvider);
        keyGenerator.init(128); // 128-bit AES key
        cmacKey = keyGenerator.generateKey();
    }


    public String encrypt(CryptoAlgorithm algorithm, String plainText) throws CryptoException {
        try {
            return switch (algorithm) {
                case RSA -> cryptoUtils.encryptWithRsa(rsaKeyPair, plainText, hsmProvider);
                case AES -> cryptoUtils.encryptWithAes(cmacKey, plainText, hsmProvider);
                default -> throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
            };
        } catch (Exception e) {
            throw new CryptoException("Encryption failed : " + e.getMessage());
        }
    }


    public String decrypt(CryptoAlgorithm algorithm, String base64Encrypted) throws CryptoException {
        byte[] base64EncryptedDecoded = Base64.getDecoder().decode(base64Encrypted.trim());
        try {
            return switch (algorithm) {
                case RSA -> cryptoUtils.decryptWithRsa(rsaKeyPair,base64EncryptedDecoded,hsmProvider);
                case AES -> cryptoUtils.decryptWithAes(cmacKey,base64EncryptedDecoded,hsmProvider);
                default -> throw new CryptoException("Unsupported algorithm: " + algorithm);
            };
        } catch (Exception e) {
            throw new CryptoException("Decryption failed : "+ e.getMessage());
        }
    }


    public String sign(CryptoAlgorithm signType, String message) throws CryptoException {
        return switch (signType) {
            case HMAC -> cryptoUtils.signMessageWithHmac(message, hmacKey, hsmProvider);
            case CMAC -> cryptoUtils.signMessageWithCmac(message, cmacKey, hsmProvider);
            case RSA -> cryptoUtils.signMessageWithRsa(message, rsaKeyPair, hsmProvider);
            default -> throw new CryptoException("Unsupported signType: " + signType);
        };
    }


    public boolean verify(CryptoAlgorithm verifyType, String message, String base64Signature) throws CryptoException {
        byte[] base64SignatureDecoded = Base64.getDecoder().decode(base64Signature.trim());
        return switch (verifyType) {
            case HMAC -> cryptoUtils.verifyMessageWithHmac(message, base64SignatureDecoded, hmacKey, hsmProvider);
            case CMAC -> cryptoUtils.verifyMessageWithCmac(message, base64SignatureDecoded, cmacKey, hsmProvider);
            case RSA -> cryptoUtils.verifyMessageWithRsa(message, base64SignatureDecoded, rsaKeyPair, hsmProvider);
            default -> throw new CryptoException("Unsupported verifyType: " + verifyType);
        };
    }


    public String generateRandom(int byteCount) throws CryptoException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance(CryptoConstants.PKCS11_PROVIDER, hsmProvider);
            byte[] randomBytes = new byte[byteCount];
            secureRandom.nextBytes(randomBytes);
            return Base64.getEncoder().encodeToString(randomBytes);
        } catch (Exception e) {
            throw new CryptoException("Random generation failed :" + e.getMessage());
        }
    }

    public List<String> generateKeyPair(String algorithm, int keySize, String ecCurve, String alias) throws CryptoException {
        try {

            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm, hsmProvider);

            if ("EC".equalsIgnoreCase(algorithm)) {
                if (ecCurve == null || ecCurve.isEmpty()) {
                    throw new CryptoException("Curve name required for EC algorithm i.e secp256r1");
                }
                keyPairGen.initialize(new ECGenParameterSpec(ecCurve));
            } else {
                keyPairGen.initialize(keySize);
            }

            KeyPair keyPair = keyPairGen.generateKeyPair();

            // Generate self-signed cert
            X509Certificate x509Certificate = cryptoUtils.generateSelfSignedCertificate(keyPair, algorithm);

            // Load PKCS#11 KeyStore
            KeyStore keyStore = KeyStore.getInstance("PKCS11", hsmProvider);
            keyStore.load(null, hsmPin.toCharArray());

            if (keyStore.containsAlias(alias)) {
                throw new CryptoException("Alias '" + alias + "' already exists in the keystore");
            }

            KeyStore.PrivateKeyEntry entry = new KeyStore.PrivateKeyEntry(
                    keyPair.getPrivate(),
                    new Certificate[]{x509Certificate}
            );
            KeyStore.ProtectionParameter protection = new KeyStore.PasswordProtection(hsmPin.toCharArray());
            keyStore.setEntry(alias, entry, protection);

            List<String> result = new ArrayList<>();

            result.add("Algorithm: " + keyPair.getPrivate().getAlgorithm());
            result.add("Key Type: " + keyPair.getClass().getSimpleName());
            result.add("KeyStore Type: " + keyStore.getType());
            result.add("Format: " + keyPair.getPublic().getFormat());
            result.add("Key Size: " + keySize + " bits");
            result.add("Alias: " + alias);
            result.add("Certificate (Base64): " + Base64.getEncoder().encodeToString(x509Certificate.getEncoded()));

            return result;

        } catch (Exception e) {
            throw new CryptoException("Key generation failed : " + e.getMessage());
        }
    }


    public List<String> generateKey(String algorithm, int keySize, String alias) throws CryptoException {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm, hsmProvider);
            keyGen.init(keySize);
            SecretKey secretKey = keyGen.generateKey();

            // Save the key in the keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS11", hsmProvider);
            keyStore.load(null, hsmPin.toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(hsmPin.toCharArray());

            if (keyStore.containsAlias(alias)) {
                throw new CryptoException("Alias '" + alias + "' already exists in the keystore");
            }

            keyStore.setEntry(alias, secretKeyEntry, protectionParam);

            List<String> result = new ArrayList<>();

            result.add("Algorithm: " + secretKey.getAlgorithm());
            result.add("Key Type: " + secretKey.getClass().getSimpleName());
            result.add("KeyStore Type: " + keyStore.getType());
            result.add("Format: " + secretKey.getFormat());
            result.add("Key Size: " + keySize + " bits");
            result.add("Alias: " + alias);

            return result;

        } catch (Exception e) {
            throw new CryptoException("Key generation failed : " + e.getMessage());
        }
    }


    public List<String> generateAsymmetricKeys(AsymmetricKeyType asymmetricKeyType, String id, String keyPairLabel) throws CryptoException {

        try {
            return pkcs11ToolHandler.generateKeyPair(slotOrSlotId, hsmPin, asymmetricKeyType, id, keyPairLabel);
        } catch (IOException e) {
            throw new CryptoException("KeyPair generation and import failed !", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CryptoException("KeyPair generation and import failed due to an interruption", e);
        }
    }

    public List<String> generateSymmetricKeys(SymmetricKeyType symmetricKeyType, String id, String keyPairLabel) throws CryptoException {
        try {
            return pkcs11ToolHandler.generateKeys(slotOrSlotId, hsmPin, symmetricKeyType, id, keyPairLabel);
        } catch (IOException e) {
            throw new CryptoException("Key generation and import failed !", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CryptoException("Key generation and import failed due to an interruption", e);
        }
    }

    public List<String> getListOfKeys(String type) throws CryptoException {
        try {
            return pkcs11ToolHandler.getListOfKeys(slotOrSlotId, hsmPin, type);
        } catch (IOException e) {
            throw new CryptoException("Failed to list keys !", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CryptoException("Failed to list keys due to an interruption", e);
        }
    }

    public Map<String, List<String>> getMechanisms() throws CryptoException {
        try {
            Set<Provider.Service> services = hsmProvider.getServices();
            Map<String, List<String>> mechanisms = new HashMap<>();
            for (Provider.Service service : services) {
                mechanisms
                        .computeIfAbsent(service.getType(), k -> new ArrayList<>())
                        .add(service.getAlgorithm());
            }
            return mechanisms;
        } catch (Exception e) {
            throw new CryptoException("Failed to retrieve mechanisms", e);
        }
    }

    public List<Pkcs11SlotInfo> getListOfSlots() throws CryptoException {
        try {
            return pkcs11ToolHandler.getListOfSlots(slotOrSlotId, hsmPin);

        } catch (IOException e) {
            throw new CryptoException("Failed to retrieve slots!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CryptoException("Failed to retrieve slots due to an interruption", e);
        }

    }

    public List<Pkcs11ObjectInfo> getListOfKeysFromSlot(int slotId) throws CryptoException {
        try {
            return pkcs11ToolHandler.getObjectsListInSlot(slotId,hsmPin);
        } catch (IOException e) {
            throw new CryptoException("Failed to list keys from slot " + slotId + "!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CryptoException("Failed to list keys from slot " + slotId + " due to an interruption", e);
        }
    }

    public String signMessageWithGiveKeyAlias(String keyAlias, String message,String signatureAlgorithm) throws CryptoException {
        try {
            return cryptoUtils.signMessageWithGivenKeyAlias(message, keyAlias,signatureAlgorithm, hsmPin, hsmProvider);

        } catch (Exception e) {
            throw new CryptoException("Signing failed : " + e.getMessage());
        }
    }

    public String verifyMessageWithGiveKeyAlias(String keyAlias, String message,String base64Signature, String signatureAlgorithm) throws CryptoException{
        try {
            return cryptoUtils.verifyMessageWithGivenKeyAlias(message, base64Signature, keyAlias, signatureAlgorithm, hsmPin, hsmProvider);
        } catch (Exception e) {
            throw new CryptoException("Verification failed : " + e.getMessage());
        }
    }



}
