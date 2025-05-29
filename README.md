# hsm-simulator
A HSM simulator using SoftHSM and PKCS#11 for local testing of CloudHSM-like cryptographic operations.


# SoftHSM Wrapper - Java HSM Simulator

This project provides a Java-based wrapper for SoftHSM2 using the SunPKCS11 provider to simulate AWS CloudHSM-like functionalities. The goal is to create a local, developer-friendly environment to prototype, test, and simulate cryptographic operations similar to a real Hardware Security Module (HSM).

## Features

- Java + Spring Boot REST API
- AES-GCM encryption & decryption
- Key generation and listing
- Metadata lookup
- In-memory key simulation
- Pluggable for SoftHSM2 backend
- Ideal for testing HSM client logic locally

## Requirements

- Java 17+
- SoftHSM2 installed (libsofthsm2.so)
- Spring Boot 3.x
- Docker (optional)

## API Endpoints

| Method | Endpoint                   | Description            |
|--------|----------------------------|------------------------|
| POST   | `/api/keys/generate`       | Generate new key       |
| GET    | `/api/keys`                | List all keys          |
| GET    | `/api/keys/{id}`           | Get metadata           |
| POST   | `/api/keys/{id}/encrypt`   | Encrypt plaintext      |
| POST   | `/api/keys/{id}/decrypt`   | Decrypt ciphertext     |

## HSM related API Endpoints

| Method | Endpoint                          | Parameter(s)    / Request Body                                             | Description                  |
|--------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| POST   | `/api/v1/tool/generateSymmetricKey`           | `{ "keyType": "AES_128", "id": "02", "label": "SymKeyLabel" }`                                                   | Generate symmetric keys                 |
| POST   | `/api/v1/tool/generateAsymmetricKeyPair`      | `{ "keyType": "RSA", "id": "01", "label": "AsymKeyLabel" }`                                                      | Generate an asymmetric key pair         |
| GET    | `/api/v1/tool/listKeys`                       | `type` (Query: Optional, e.g., 'cert', 'privkey', 'pubkey', etc.)                                                | List all keys                           |
| GET    | `/api/v1/tool/listMechanisms`                 | None                                                                                                             | List all mechanisms                     |
| GET    | `/api/v1/slots/{slotId}/keys`                 | `slotId` (Path: Integer)                                                                                         | List all keys in a specific slot        |
| GET    | `/api/v1/slots`                               | None                                                                                                             | List all available slots                |
| POST   | `/api/v1/data/sign/{selectedKeyAlias}`        | `{ "message": "example message", "signAlgo": "SHA256withRSA" }`                                                  | Sign a message using a key alias        |
| POST   | `/api/v1/data/verify/{selectedKeyAlias}`      | `{ "message": "example message", "base64Signature": "base64EncodedSignature", "verifyAlgo": "SHA256withECDSA" }` | Verify a signature using a key alias    |
| POST   | `/api/v1/generateKey`                         | `{ "algorithm": "AES", "keySize": 256, "alias": "symmetricKeyAlias" }`                                           | Generate a symmetric key                |
| POST   | `/api/v1/generateKeyPair`                     | `{ "algorithm": "RSA", "keySize": 2048, "ecCurve": "secp256r1", "alias": "keyPairAlias" }`                       | Generate a key pair                     |
| POST   | `/api/v1/encrypt/{algorithm}`                 | `{ "plainText": "example text" }`                                                                                | Encrypt a message                       |
| POST   | `/api/v1/decrypt/{algorithm}`                 | `{ "base64Encrypted": "base64EncodedText" }`                                                                     | Decrypt a message                       |
| GET    | `/api/v1/random/{byteCount}`                  | `byteCount` (Path: Positive Integer, e.g., 16 or 32)                                                             | Generate random bytes                   |

## Login and Authentication
This API does not implement authentication or authorization. It is intended for local development and testing purposes only. In a production environment, you should implement proper security measures.

| Method | Endpoint                  | Parameter(s) / Request Body                     | Description                     |
|--------|---------------------------|------------------------------------------------|---------------------------------|
| POST   | `/api/v1/auth/login`      | `{ "username": "user", "password": "pass" }`   | Authenticate and obtain a token |
| POST   | `/api/v1/auth/logout`     | None                                           | NA                              |
| GET    | `/api/v1/auth/status`     | None                                           | NA                              |

## Swagger API Documentation
Refer to the API documentation for detailed request and response formats.  
- http://localhost:8080/swagger-ui/index.html

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](./LICENSE) file for details.

## Acknowledgements

- [SoftHSM2](https://github.com/opendnssec/SoftHSMv2) - BSD-2-Clause Licensed
- Java SunPKCS11 provider - part of OpenJDK
- [OpenSC](https://github.com/OpenSC/OpenSC) - LGPL-2.1 Licensed
- [Spring Boot](https://spring.io/projects/spring-boot) - Apache 2.0 Licensed
- [Springdoc](https://springdoc.org/) - Apache 2.0 Licensed

**Note:** This project is intended as a development and testing tool. It is not a replacement for certified HSM systems in production.
