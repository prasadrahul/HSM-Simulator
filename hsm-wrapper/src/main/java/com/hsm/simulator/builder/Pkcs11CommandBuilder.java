package com.hsm.simulator.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for constructing PKCS#11 command line arguments.
 * This class provides a fluent API to build commands for interacting with PKCS#11 modules.
 */
public class Pkcs11CommandBuilder {
    private final List<String> command;

    public Pkcs11CommandBuilder(String baseCommand, String modulePath) {
        this.command = new ArrayList<>();
        this.command.add(baseCommand);
        this.command.add("--module");
        this.command.add(modulePath);
    }

    public Pkcs11CommandBuilder withSlot(String slot) {
        if (slot != null) {
            this.command.add("--slot");
            this.command.add(slot);
        }
        return this;
    }

    public Pkcs11CommandBuilder withLogin(String pin) {
        if (pin != null) {
            this.command.add("--login");
            this.command.add("--pin");
            this.command.add(pin);
        }
        return this;
    }

    public Pkcs11CommandBuilder withKeyPairGeneration(String keySpec, String id, String label) {
        if (keySpec != null && label != null && id != null) {
            this.command.add("--keypairgen");
            this.command.add("--key-type");
            this.command.add(keySpec);
            this.command.add("--id");
            this.command.add(id);
            this.command.add("--label");
            this.command.add(label);
        }
        return this;
    }

    public Pkcs11CommandBuilder withKeyGeneration(String keySpec, String id, String label) {
        if (keySpec != null && label != null && id != null) {
            this.command.add("--keygen");
            this.command.add("--key-type");
            this.command.add(keySpec);
            this.command.add("--id");
            this.command.add(id);
            this.command.add("--label");
            this.command.add(label);
        }
        return this;
    }

    public Pkcs11CommandBuilder withListObjects() {
        this.command.add("--list-objects");
        return this;
    }

    public Pkcs11CommandBuilder withListSlots() {
        this.command.add("--list-slots");
        return this;
    }

    public Pkcs11CommandBuilder withVerbose() {
        this.command.add("--verbose");
        return this;
    }

    /** -y, --type <arg>        Specify the type of object (e.g. cert, privkey, pubkey, secrkey, data) */
    public Pkcs11CommandBuilder withType(String type) {
        this.command.add("--type");
        this.command.add(type);
        return this;
    }

    public Pkcs11CommandBuilder withWriteObject(String filePath, String objectType, String label) {
        if (filePath != null && objectType != null && label != null) {
            this.command.add("--write-object");
            this.command.add(filePath);
            this.command.add("--type");
            this.command.add(objectType);
            this.command.add("--label");
            this.command.add(label);
        }
        return this;
    }

    public List<String> build() {
        return new ArrayList<>(this.command);
    }

}
