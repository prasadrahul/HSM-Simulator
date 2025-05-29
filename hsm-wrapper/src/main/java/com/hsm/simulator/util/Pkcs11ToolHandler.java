package com.hsm.simulator.util;

import com.hsm.simulator.builder.Pkcs11CommandBuilder;
import com.hsm.simulator.config.Pkcs11Config;
import com.hsm.simulator.dto.Pkcs11ObjectInfo;
import com.hsm.simulator.dto.Pkcs11SlotInfo;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Usage: pkcs11-tool [OPTIONS]
 * Refer: <a href="https://manpages.ubuntu.com/manpages/plucky/man1/pkcs11-tool.1.html">OpenSC pkcs11-tool...</a>
 */
@Configuration
public class Pkcs11ToolHandler {


    private final String pkcs11ToolPath;
    private final String pkcs11ModulePath;

    public Pkcs11ToolHandler(Pkcs11Config pkcs11Config) {
        this.pkcs11ToolPath = pkcs11Config.getPkcs11ToolPath();
        this.pkcs11ModulePath = pkcs11Config.getPkcs11ModulePath();
    }

    public List<String> generateKeyPair(String slot, String pin, AsymmetricKeyType asymmetricKeyType,String id, String label) throws IOException, InterruptedException {
        List<String> command = new Pkcs11CommandBuilder(pkcs11ToolPath, pkcs11ModulePath)
                .withSlot(slot)
                .withLogin(pin)
                .withKeyPairGeneration(asymmetricKeyType.getKeySpec(), id, label)
                .build();
        return executeCommand(command);
    }

    public List<String> generateKeys(String slot, String pin, SymmetricKeyType symmetricKeyType,String id, String label) throws IOException, InterruptedException {
        List<String> command = new Pkcs11CommandBuilder(pkcs11ToolPath, pkcs11ModulePath)
                .withSlot(slot)
                .withLogin(pin)
                .withKeyGeneration(symmetricKeyType.getKeySpec(), id, label)
                .build();
        return executeCommand(command);
    }

    public List<String> getListOfKeys(String slot, String hsmPin, String type) throws IOException, InterruptedException {
        Pkcs11CommandBuilder commandBuilder = new Pkcs11CommandBuilder(pkcs11ToolPath, pkcs11ModulePath)
                .withSlot(slot)
                .withLogin(hsmPin)
                .withListObjects()
                .withVerbose();
        if (type != null) {
            commandBuilder = commandBuilder.withType(type);
        }

        List<String> command = commandBuilder.build();
        return executeCommand(command);
    }


    public List<Pkcs11ObjectInfo> getObjectsListInSlot(int slotId, String hsmPin) throws IOException, InterruptedException {


        List<String> commandList = new Pkcs11CommandBuilder(pkcs11ToolPath, pkcs11ModulePath)
                .withLogin(hsmPin)
                .withSlot(String.valueOf(slotId))
                .withListObjects()
                .build();

        List<String> slotList = executeCommand(commandList);

        return parseSlotList(slotList);

    }

    private List<Pkcs11ObjectInfo> parseSlotList(List<String> slotList) {
        List<Pkcs11ObjectInfo> pkcs11ObjectInfoList = new ArrayList<>();
        Pkcs11ObjectInfo current = null;

        for (String line : slotList) {
            line = line.trim();

            if (line.startsWith("Private Key Object") || line.startsWith("Certificate Object") || line.startsWith("Public Key Object")) {
                // Finalize the current object if it exists
                if (current != null) pkcs11ObjectInfoList.add(current);

                // Create a new object and set its type
                current = new Pkcs11ObjectInfo();
                if (line.contains("cert")) {
                    current.setType("Certificate");
                } else if (line.contains("Private Key")) {
                    current.setType("PrivateKey");
                } else if (line.contains("Public Key")) {
                    current.setType("PublicKey");
                }
            } else if (current != null) {
                // Parse attributes for the current object
                if (line.startsWith("label:")) {
                    current.setLabel(line.substring(6).trim());
                } else if (line.startsWith("ID:")) {
                    current.setId(line.substring(3).trim());
                } else if (line.startsWith("subject:")) {
                    current.setSubject(line.substring(8).trim());
                } else if (line.startsWith("Usage:")) {
                    current.setUsage(line.substring(6).trim());
                } else if (line.startsWith("Access:")) {
                    current.setAccess(line.substring(7).trim());
                }
            }
        }

        // Add the last object if it exists
        if (current != null) {
            pkcs11ObjectInfoList.add(current);
        }

        return pkcs11ObjectInfoList;
    }

    public List<Pkcs11SlotInfo> getListOfSlots(String slot,String hsmPin) throws IOException, InterruptedException {

        List<String> commandList = new Pkcs11CommandBuilder(pkcs11ToolPath, pkcs11ModulePath)
                .withSlot(slot)
                .withLogin(hsmPin)
                .withListSlots()
                .build();

        List<String> outputLines = executeCommand(commandList);

        List<Pkcs11SlotInfo> result = new ArrayList<>();
        Pkcs11SlotInfo current = null;

        for (String line : outputLines) {
            line = line.trim();
            if (line.startsWith("Slot ")) {
                // Finalize previous slot
                if (current != null) result.add(current);

                current = new Pkcs11SlotInfo();
                int slotIdxStart = line.indexOf("Slot ") + 5;
                int slotIdxEnd = line.indexOf(' ', slotIdxStart);
                int slotHexStart = line.indexOf("(0x") + 1;
                int slotHexEnd = line.indexOf(')', slotHexStart);

                current.slotIndex = Integer.parseInt(line.substring(slotIdxStart, slotIdxEnd));
                var slotHex = line.substring(slotHexStart, slotHexEnd);
                var slotDecimal = Long.parseLong(slotHex.replace("0x","").trim(), 16);
                current.slotHex = slotHex;
                current.slotDecimal = String.valueOf(slotDecimal);
            } else if (current != null && line.contains(":")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();

                switch (key) {
                    case "token label":
                        current.setLabel(value);
                        break;
                    case "token manufacturer":
                        current.setManufacturer(value);
                        break;
                    case "token model":
                        current.setModel(value);
                        break;
                    case "token flags":
                        current.setFlags(value);
                        break;
                    case "hardware version":
                        current.setHwVersion(value);
                        break;
                    case "firmware version":
                        current.setFwVersion(value);
                        break;
                    case "serial num":
                        current.setSerial(value);
                        break;
                    case "pin min/max":
                        current.setPinMinMax(value);
                        break;
                    case "token state":
                        current.setInitialized(!value.equalsIgnoreCase("uninitialized"));
                        break;
                    default:
                        // Handle unknown keys if necessary
                        break;
                }
            }
        }

        // Add the last slot
        if (current != null)
            result.add(current);

        return result;
    }

    private List<String> executeCommand(List<String> command) throws IOException, InterruptedException {
        System.out.println("Executing command: "+   command);
        var builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        var process = builder.start();

        var outputStream = new ByteArrayOutputStream();
        try (var inputStream = process.getInputStream()) {
            inputStream.transferTo(outputStream);
        }

        if (process.waitFor() != 0) {
            throw new RuntimeException("Command failed with exit code: " + process.exitValue());
        }

        return List.of(outputStream.toString(StandardCharsets.UTF_8).split("\\R"));
    }

}
