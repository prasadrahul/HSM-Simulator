# hsm-simulator
A HSM simulator using SoftHSM and PKCS#11 for local testing of CloudHSM-like cryptographic operations.

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

## License

This project is licensed under the Apache 2.0 License - see the [LICENSE](./LICENSE) file for details.

## Acknowledgements

- [SoftHSM2](https://github.com/opendnssec/SoftHSMv2) - BSD-2-Clause Licensed
- Java SunPKCS11 provider - part of OpenJDK

---

**Note:** This project is intended as a development and testing tool. It is not a replacement for certified HSM systems in production.
