package com.hsm.simulator.controller;

import com.hsm.simulator.dto.Pkcs11ObjectInfo;
import com.hsm.simulator.dto.Pkcs11SlotInfo;
import com.hsm.simulator.exception.CryptoException;
import com.hsm.simulator.model.HsmApiResponse;
import com.hsm.simulator.model.CryptoAlgorithm;
import com.hsm.simulator.service.Pkcs11CryptoService;
import com.hsm.simulator.util.AsymmetricKeyType;
import com.hsm.simulator.util.SymmetricKeyType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CryptoController {

    private final Pkcs11CryptoService cryptoService;

    @Autowired
    public CryptoController(Pkcs11CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @Operation(
            summary = "Encrypt a message",
            description = "Encrypts the provided plain text using the specified algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the plain text to be encrypted",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"plainText\": \"example text\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "algorithm", description = "The cryptographic algorithm to use for encryption", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Encryption completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/encrypt/{algorithm}")
    public ResponseEntity<HsmApiResponse<String>> encrypt(
            @PathVariable CryptoAlgorithm algorithm,
            @RequestBody Map<String, String> request) {
        String plainText = request.get("plainText");
        if (plainText == null || plainText.isEmpty()) {
            throw new CryptoException("Plain text must not be empty");
        }
        String encrypted = cryptoService.encrypt(algorithm, plainText);
        return ResponseEntity.ok(new HsmApiResponse<>(true, "Encrypted successfully", encrypted));
    }

    @Operation(
            summary = "Decrypt a message",
            description = "Decrypts the provided base64-encoded encrypted text using the specified algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the base64-encoded encrypted text",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"base64Encrypted\": \"base64EncodedText\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "algorithm", description = "The cryptographic algorithm to use for decryption", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Decryption completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/decrypt/{algorithm}")
    public HsmApiResponse<String> decrypt(@PathVariable CryptoAlgorithm algorithm, @RequestBody Map<String, String> request) {
        String base64Encrypted = request.get("base64Encrypted");
        if (base64Encrypted == null || base64Encrypted.isEmpty()) {
            return new HsmApiResponse<>(false, "Encrypted text must not be empty", null);
        }
        String decrypted = cryptoService.decrypt(algorithm, base64Encrypted);
        return new HsmApiResponse<>(true, "Decrypted successfully", decrypted);
    }

    @Operation(
            summary = "Sign a message",
            description = "Signs the provided message using the specified algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the message to be signed",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"message\": \"example message\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "signType", description = "The cryptographic algorithm to use for signing", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Signing completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/sign/{signType}")
    public HsmApiResponse<String> sign(@PathVariable CryptoAlgorithm signType, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return new HsmApiResponse<>(false, "Message must not be empty", null);
        }
        String signature = cryptoService.sign(signType, message);
        return new HsmApiResponse<>(true, "Signed successfully", signature);
    }

    @Operation(
            summary = "Verify a digital signature",
            description = "Verifies the provided digital signature for the given message using the specified algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the message and base64-encoded signature",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"message\": \"example message\", \"base64Signature\": \"base64EncodedSignature\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "verifyType", description = "The cryptographic algorithm to use for verification", required = true)
            },
            responses = {

                    @ApiResponse(responseCode = "200", description = "Verification completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/verify/{verifyType}")
    public HsmApiResponse<Boolean> verify(@PathVariable CryptoAlgorithm verifyType, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String base64Signature = request.get("base64Signature");
        if (message == null || message.isEmpty() || base64Signature == null || base64Signature.isEmpty()) {
            return new HsmApiResponse<>(false, "Message and signature must not be empty", null);
        }
        boolean verified = cryptoService.verify(verifyType, message, base64Signature);
        return new HsmApiResponse<>(true, "Verification completed", verified);
    }

    @Operation(
            summary = "Generate random bytes",
            description = "Generates a specified number of random bytes.",
            parameters = {
                    @Parameter(name = "byteCount", description = "The number of random bytes to generate", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Random bytes generated successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @GetMapping("/random/{byteCount}")
    public HsmApiResponse<String> generateRandom(@PathVariable int byteCount) {
        String random = cryptoService.generateRandom(byteCount);
        return new HsmApiResponse<>(true, "Random generated", random);
    }


    @Operation(
            summary = "Generate a key pair",
            description = "Generates a key pair based on the provided parameters.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the parameters for key pair generation",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"algorithm\": \"RSA\", \"keySize\": 2048, \"ecCurve\": \"secp256r1\", \"alias\": \"keyPairAlias\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Key pair generated successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/generateKeyPair")
    public HsmApiResponse<List<String>> generateKeyPair(@RequestBody Map<String, Object> request) {
        String algorithm = (String) request.get("algorithm");
        int keySize = (int) request.get("keySize");
        String ecCurve = (String) request.get("ecCurve");
        String alias = (String) request.get("alias");
        List<String> keyPairList = cryptoService.generateKeyPair(algorithm, keySize,ecCurve, alias);
        return new HsmApiResponse<>(true, "Key pair generated", keyPairList);
    }

    @Operation(
            summary = "Generate a symmetric key",
            description = "Generates a symmetric key based on the provided parameters.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the parameters for symmetric key generation",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"algorithm\": \"AES\", \"keySize\": 256, \"alias\": \"symmetricKeyAlias\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Symmetric key generated successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/generateKey")
    public HsmApiResponse<List<String>> generateKey(@RequestBody Map<String, Object> request) {
        String algorithm = (String) request.get("algorithm");
        int keySize = (int) request.get("keySize");
        String alias = (String) request.get("alias");
        List<String> keyList = cryptoService.generateKey(algorithm, keySize, alias);
        return new HsmApiResponse<>(true, "Symmetric key generated", keyList);
    }


    //Key Generation Usings PKCS11Tool

    @Operation(
            summary = "Generate an asymmetric key pair",
            description = "Generates an asymmetric key pair based on the provided parameters.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the parameters for asymmetric key pair generation",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"keyType\": \"RSA_2048\", \"id\": \"01\", \"label\": \"AsyKeyPairLabel\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asymmetric key pair generated successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/tool/generateAsymmetricKeyPair")
    public HsmApiResponse<List<String>> generateAsymmetricKeyPair(@RequestBody Map<String, Object> request) {
        AsymmetricKeyType asymmetricKeyType = AsymmetricKeyType.valueOf((String) request.get("keyType"));
        String id = (String) request.get("id");
        String label = (String) request.get("label");
        List<String> keyPairList = cryptoService.generateAsymmetricKeys(asymmetricKeyType,id, label);
        return new HsmApiResponse<>(true, "Key pair generated", keyPairList);
    }


    @Operation(
            summary = "Generate symmetric keys",
            description = "Generates symmetric keys based on the provided parameters.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the parameters for symmetric key generation",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"keyType\": \"AES_128\", \"id\": \"02\", \"label\": \"SymKeyLabel\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Symmetric keys generated successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/tool/generateSymmetricKey")
    public HsmApiResponse<List<String>> generateSymmetricKey(@RequestBody Map<String, Object> request) {
        SymmetricKeyType symmetricKeyType = SymmetricKeyType.valueOf((String) request.get("keyType"));
        String id = (String) request.get("id");
        String label = (String) request.get("label");
        List<String> keyList = cryptoService.generateSymmetricKeys(symmetricKeyType,id, label);
        return new HsmApiResponse<>(true, "Symmetric key generated", keyList);
    }


    @Operation(
            summary = "List all keys",
            description = "Lists all available keys of the specified type.",
            parameters = {
                    @Parameter(name = "type", description = "The type of keys to list (e.g.,'cert' 'privkey' 'pubkey' 'secrkey' 'data')", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Keys retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @GetMapping("/tool/listKeys")
    public HsmApiResponse<List<String>> listKeys(@RequestParam(required = false) String type) {
        List<String> keys = cryptoService.getListOfKeys(type);
        return new HsmApiResponse<>(true, "Keys retrieved", keys);
    }


    @Operation(
        summary = "List all mechanisms",
        description = "Lists all available mechanisms for the specified key type.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Mechanisms retrieved successfully",
                    content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
        }
    )
    @GetMapping("/tool/listMechanisms")
    public HsmApiResponse<Map<String, List<String>>> listMechanisms() {

        Map<String, List<String>> mechanisms = cryptoService.getMechanisms();
        return new HsmApiResponse<>(true, "Mechanisms retrieved", mechanisms);
    }


    @Operation(
            summary = "List all keys in a specific slot",
            description = "Lists all keys available in the specified slot.",
            parameters = {
                    @Parameter(name = "slotId", description = "The ID of the slot to list keys from", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Keys in slot retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @GetMapping("/slots/{slotId}/keys")
    public HsmApiResponse<List<Pkcs11ObjectInfo>> listSlotKeys(@PathVariable int slotId) {
        List<Pkcs11ObjectInfo> keysInSlot = cryptoService.getListOfKeysFromSlot(slotId);
        return new HsmApiResponse<>(true, "Keys in slot retrieved", keysInSlot);
    }

    @Operation(
            summary = "List all slots",
            description = "Lists all available slots in the PKCS#11 module.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Slots retrieved successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @GetMapping("/slots")
    public HsmApiResponse<List<Pkcs11SlotInfo>> listSlots() {
        List<Pkcs11SlotInfo> slotList =  cryptoService.getListOfSlots();
        return new HsmApiResponse<>(true, "Slots retrieved", slotList);
    }

    @Operation(
            summary = "Sign a message with a specific key alias",
            description = "Signs the provided message using the specified key alias and SHA256withECDSA algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the message to be signed",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"message\": \"example message\", \"signAlgo\": \"SHA256withRSA\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "selectedKeyAlias", description = "The alias of the key to use for signing", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Signing completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/data/sign/{selectedKeyAlias}")
    public HsmApiResponse<String> sign(@PathVariable String selectedKeyAlias, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String signatureAlgorithm = request.get("signAlgo") != null ? request.get("signAlgo") : "SHA256withECDSA";
        if (message == null || message.isEmpty()) {
            return new HsmApiResponse<>(false, "Message must not be empty", null);
        }
        String signature = cryptoService.signMessageWithGiveKeyAlias(selectedKeyAlias, message, signatureAlgorithm);
        return new HsmApiResponse<>(true, "Signed successfully", signature);
    }

    @Operation(
            summary = "Verify a signature with a specific key alias",
            description = "Verifies the provided base64-encoded signature for the given message using the specified key alias and SHA256withECDSA algorithm.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Request payload containing the message and base64-encoded signature",
                    required = true,
                    content = @Content(
                            schema = @Schema(
                                    example = "{ \"message\": \"example message\", \"base64Signature\": \"base64EncodedSignature\", \"verifyAlgo\": \"SHA256withECDSA\" }"
                            )
                    )
            ),
            parameters = {
                    @Parameter(name = "selectedKeyAlias", description = "The alias of the key to use for verification", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Verification completed successfully",
                            content = @Content(schema = @Schema(implementation = HsmApiResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input provided")
            }
    )
    @PostMapping("/data/verify/{selectedKeyAlias}")
    public HsmApiResponse<String> verifySignature(@PathVariable String selectedKeyAlias, @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String base64Signature = request.get("base64Signature");
        String signatureAlgorithm = request.get("verifyAlgo") != null ? request.get("verifyAlgo") : "SHA256withECDSA";
        if (message == null || message.isEmpty() || base64Signature == null || base64Signature.isEmpty()) {
            return new HsmApiResponse<>(false, "Message and signature must not be empty", null);
        }
        String verified = cryptoService.verifyMessageWithGiveKeyAlias(selectedKeyAlias, message, base64Signature,signatureAlgorithm);
        return new HsmApiResponse<>(true, "Verification completed", verified);
    }
}
