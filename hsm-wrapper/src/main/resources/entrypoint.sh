#!/bin/bash
set -e

PIN=12345

# Init token using the PIN
SLOT_OUTPUT=$(softhsm2-util --init-token --slot 0 --label "Docker-Token" --so-pin "$PIN" --pin "$PIN")
NEW_SLOT=$(echo "$SLOT_OUTPUT" | grep -oP '(?<=slot )\d+')

#Update the slot number in the configuration file
sed -i "s/^slotListIndex = .*/slot = $NEW_SLOT/" /app/pkcs11.cfg

# Create EC key pair with id 012 and label "EcKeyPair256_01" using the PIN
pkcs11-tool --module /usr/lib/softhsm/libsofthsm2.so \
  --slot $NEW_SLOT --login --pin "$PIN" \
  --keypairgen --key-type EC:prime256v1 --id 01 --label "EcKeyPair256_01" \
  --attr-from /app/ec-keypair.tmpl

# Write EC cert for created id and label using the PIN
pkcs11-tool --module /usr/lib/softhsm/libsofthsm2.so \
    --slot $NEW_SLOT --login --pin "$PIN" \
    --write-object /app/certs/ec-cert.der --type cert --id 01 --label "EcKeyPair256_01"

echo "SoftHSM2 container ready with EC key pair having alias EcKeyPair256_01"
