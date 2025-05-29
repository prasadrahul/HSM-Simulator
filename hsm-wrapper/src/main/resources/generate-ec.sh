#!/bin/bash
set -e

OUT_DIR=/app/certs

mkdir -p "$OUT_DIR"

openssl ecparam -name prime256v1 -genkey -noout -out "$OUT_DIR/ec-key.pem"
openssl req -new -x509 -key "$OUT_DIR/ec-key.pem" -out "$OUT_DIR/ec-cert.pem" -days 365 -subj "/CN=EcKeyPair256_01"
openssl x509 -in "$OUT_DIR/ec-cert.pem" -outform DER -out "$OUT_DIR/ec-cert.der"

echo "Generated EC key and certificate in $OUT_DIR"
