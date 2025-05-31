package com.hsm.simulator.util;

import com.hsm.simulator.exception.CryptoException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.math.BigInteger;
import java.security.AuthProvider;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;

@Component
public class CryptoUtils {

    //RSA
    public String encryptWithRsa(KeyPair rsaKeyPair, String plainText, AuthProvider hsmProvider) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher;
        cipher = Cipher.getInstance(CryptoConstants.RSA_TRANSFORMATION, hsmProvider);
        cipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.getPublic());
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decryptWithRsa(KeyPair rsaKeyPair, byte[] base64EncryptedDecoded, AuthProvider hsmProvider) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CryptoConstants.RSA_TRANSFORMATION, hsmProvider);
        cipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
        byte[] decryptedRsa = cipher.doFinal(base64EncryptedDecoded);
        return new String(decryptedRsa);
    }

    //AES
    public String encryptWithAes(SecretKey cmacKey, String plainText, AuthProvider hsmProvider) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(CryptoConstants.AES_TRANSFORMATION, hsmProvider);
        byte[] ivAes = new byte[16];
        new SecureRandom().nextBytes(ivAes);
        IvParameterSpec ivSpecAes = new IvParameterSpec(ivAes);
        cipher.init(Cipher.ENCRYPT_MODE, cmacKey, ivSpecAes);
        byte[] encryptedAes = cipher.doFinal(plainText.getBytes());
        byte[] combinedAes = new byte[ivAes.length + encryptedAes.length];
        System.arraycopy(ivAes, 0, combinedAes, 0, ivAes.length);
        System.arraycopy(encryptedAes, 0, combinedAes, ivAes.length, encryptedAes.length);
        return Base64.getEncoder().encodeToString(combinedAes);
    }

    public String decryptWithAes(SecretKey cmacKey, byte[] base64EncryptedDecoded, AuthProvider hsmProvider) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        byte[] ivAes = new byte[16];
        byte[] ciphertextAes = new byte[base64EncryptedDecoded.length - 16];
        System.arraycopy(base64EncryptedDecoded, 0, ivAes, 0, 16);
        System.arraycopy(base64EncryptedDecoded, 16, ciphertextAes, 0, ciphertextAes.length);
        Cipher cipher = Cipher.getInstance(CryptoConstants.AES_TRANSFORMATION, hsmProvider);
        cipher.init(Cipher.DECRYPT_MODE, cmacKey, new IvParameterSpec(ivAes));
        byte[] decryptedAes = cipher.doFinal(ciphertextAes);
        return new String(decryptedAes);
    }

    // Signing and Verification
    public String signMessageWithGivenKeyAlias(String message, String keyAlias, String signatureAlgorithm, String hsmPin, AuthProvider hsmProvider) throws CryptoException {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS11", hsmProvider);
            keyStore.load(null, hsmPin.toCharArray());

            //Fetch the key from the keystore for signing
            Key key = keyStore.getKey(keyAlias, hsmPin.toCharArray());
            if (key == null) {
                throw new CryptoException("No private key found for alias: " + keyAlias);
            }

            Signature signature = Signature.getInstance(signatureAlgorithm, hsmProvider);
            signature.initSign((java.security.PrivateKey) key);
            signature.update(message.getBytes());
            byte[] sigBytes = signature.sign();
            return Base64.getEncoder().encodeToString(sigBytes);
        } catch (Exception e) {
            throw new CryptoException("Signing failed with algorithm " + signatureAlgorithm + " : " + e.getMessage());
        }
    }

    public String verifyMessageWithGivenKeyAlias(String message, String base64SignatureDecoded, String keyAlias, String signatureAlgorithm, String hsmPin, AuthProvider hsmProvider) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS11", hsmProvider);
            keyStore.load(null, hsmPin.toCharArray());

            Certificate certificate = keyStore.getCertificate(keyAlias);
            if (certificate == null) {
                throw new CryptoException("No certificate found for alias: " + keyAlias);
            }

            Signature signature = Signature.getInstance(signatureAlgorithm, hsmProvider);
            signature.initVerify(certificate.getPublicKey());
            signature.update(message.getBytes());
            return signature.verify(base64SignatureDecoded) ? "Message Verified Successfully" : "Message Verification Failed";
        } catch (Exception e) {
            throw new CryptoException("Verification failed with algorithm " + signatureAlgorithm + " : " + e.getMessage());
        }
    }


    public String signMessageWithRsa(String message, KeyPair rsaKeyPair, AuthProvider hsmProvider) throws CryptoException {
        try {
            Signature signature = Signature.getInstance(CryptoConstants.RSA_SIGNATURE_ALGORITHM, hsmProvider);
            signature.initSign(rsaKeyPair.getPrivate());
            signature.update(message.getBytes());
            byte[] sigBytes = signature.sign();
            return Base64.getEncoder().encodeToString(sigBytes);
        } catch (Exception e) {
            throw new CryptoException("RSA signing failed : " + e.getMessage());
        }
    }

    public String signMessageWithCmac(String message,SecretKey cmacKey, AuthProvider hsmProvider) throws CryptoException {
        try {
            Mac mac = Mac.getInstance(CryptoConstants.CMAC_ALGORITHM, hsmProvider);
            mac.init(cmacKey);
            byte[] macBytes = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(macBytes);
        } catch (Exception e) {
            throw new CryptoException("CMAC signing failed : " + e.getMessage());
        }
    }

    public String signMessageWithHmac(String message, SecretKey hmacKey, AuthProvider hsmProvider) throws CryptoException {
        try {
            Mac mac = Mac.getInstance(CryptoConstants.HMAC_ALGORITHM, hsmProvider);
            mac.init(hmacKey);
            byte[] macBytes = mac.doFinal(message.getBytes());
            return Base64.getEncoder().encodeToString(macBytes);
        } catch (Exception e) {
            throw new CryptoException("HMAC signing failed : " + e.getMessage());
        }
    }

    public boolean verifyMessageWithRsa(String message, byte[] base64SignatureDecoded, KeyPair rsaKeyPair, AuthProvider hsmProvider) {
        try {
            Signature signature = Signature.getInstance(CryptoConstants.RSA_SIGNATURE_ALGORITHM, hsmProvider);
            signature.initVerify(rsaKeyPair.getPublic());
            signature.update(message.getBytes());
            return signature.verify(base64SignatureDecoded);
        } catch (Exception e) {
            throw new CryptoException("RSA verification failed : " + e.getMessage());
        }
    }

    public boolean verifyMessageWithCmac(String message, byte[] base64SignatureDecoded, SecretKey cmacKey, AuthProvider hsmProvider) {
        try {
            Mac mac = Mac.getInstance(CryptoConstants.CMAC_ALGORITHM, hsmProvider);
            mac.init(cmacKey);
            byte[] macBytes = mac.doFinal(message.getBytes());
            return MessageDigest.isEqual(macBytes, base64SignatureDecoded);
        } catch (Exception e) {
            throw new CryptoException("CMAC verification failed : " + e.getMessage());
        }
    }

    public boolean verifyMessageWithHmac(String message, byte[] base64SignatureDecoded, SecretKey hmacKey, AuthProvider hsmProvider) {
        try {
            Mac mac = Mac.getInstance(CryptoConstants.HMAC_ALGORITHM, hsmProvider);
            mac.init(hmacKey);
            byte[] macBytes = mac.doFinal(message.getBytes());
            return MessageDigest.isEqual(macBytes, base64SignatureDecoded);
        } catch (Exception e) {
            throw new CryptoException("HMAC verification failed : " + e.getMessage());
        }
    }


    // Generate self-signed certificate
    public X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String algorithm) throws Exception {
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + 365L * 24 * 60 * 60 * 1000); // 1 year

        X500Name dnName = new X500Name("CN=Test, OU=HSM, O=YourOrg, L=City, ST=State, C=IN");
        BigInteger certSerialNumber = new BigInteger(Long.toString(now)); // Using current time as serial

        ContentSigner contentSigner = new JcaContentSignerBuilder(getSignatureAlgorithm(algorithm))
                .build(keyPair.getPrivate());

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerialNumber, startDate, endDate, dnName, keyPair.getPublic());

        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        return new JcaX509CertificateConverter().getCertificate(certHolder);
    }

    private String getSignatureAlgorithm(String keyAlgorithm) {
        return switch (keyAlgorithm.toUpperCase()) {
            case "RSA" -> "SHA256withRSA";
            case "EC" -> "SHA256withECDSA";
            case "DSA" -> "SHA256withDSA";
            default -> throw new IllegalArgumentException("Unsupported key algorithm: " + keyAlgorithm);
        };
    }

}
