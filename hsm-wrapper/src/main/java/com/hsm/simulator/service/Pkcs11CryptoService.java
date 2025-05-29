package com.hsm.simulator.service;

import com.hsm.simulator.dto.Pkcs11ObjectInfo;
import com.hsm.simulator.dto.Pkcs11SlotInfo;
import com.hsm.simulator.exception.CryptoException;
import com.hsm.simulator.model.CryptoAlgorithm;
import com.hsm.simulator.util.AsymmetricKeyType;
import com.hsm.simulator.util.SymmetricKeyType;

import java.util.List;
import java.util.Map;

public interface Pkcs11CryptoService {
    String encrypt(CryptoAlgorithm algorithm, String plainText) throws CryptoException;
    String decrypt(CryptoAlgorithm algorithm, String base64Encrypted) throws CryptoException;
    String sign(CryptoAlgorithm signType, String message) throws CryptoException;
    boolean verify(CryptoAlgorithm verifyType, String message, String base64Signature) throws CryptoException;
    String generateRandom(int byteCount) throws CryptoException;
    List<String> generateKeyPair(String algorithm, int keySize, String ecCurve, String alias) throws CryptoException;
    List<String> generateKey(String algorithm,int keySize, String alias ) throws CryptoException;
    List<String> generateAsymmetricKeys(AsymmetricKeyType asymmetricKeyType,String id, String keyPairLabel) throws CryptoException;
    List<String> generateSymmetricKeys(SymmetricKeyType symmetricKeyType, String id, String keyPairLabel) throws CryptoException;
    List<String> getListOfKeys(String type) throws CryptoException;
    Map<String, List<String>> getMechanisms() throws CryptoException;
    List<Pkcs11SlotInfo> getListOfSlots() throws CryptoException;
    List<Pkcs11ObjectInfo> getListOfKeysFromSlot(int slotId) throws CryptoException;
    String signMessageWithGiveKeyAlias(String keyAlias, String message, String signatureAlgorithm) throws CryptoException;
    String verifyMessageWithGiveKeyAlias(String keyAlias, String message,String base64Signature, String signatureAlgorithm) throws CryptoException;
}
