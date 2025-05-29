package com.hsm.simulator.dto;

public class Pkcs11SlotInfo {
    public int slotIndex;
    public String slotHex;
    public String slotDecimal;
    public String label;
    public String manufacturer;
    public String model;
    public String flags;
    public String hwVersion;
    public String fwVersion;
    public String serial;
    public String pinMinMax;
    public boolean initialized = true;

    public Pkcs11SlotInfo(){
        // Default constructor
    }

    public Pkcs11SlotInfo (int slotIndex, String slotHex, String slotDecimal, String label, String manufacturer, String model,
            String flags, String hwVersion, String fwVersion, String serial, String pinMinMax,
            boolean initialized) {
        this.slotIndex = slotIndex;
        this.slotHex = slotHex;
        this.slotDecimal = slotDecimal;
        this.label = label;
        this.manufacturer = manufacturer;
        this.model = model;
        this.flags = flags;
        this.hwVersion = hwVersion;
        this.fwVersion = fwVersion;
        this.serial = serial;
        this.pinMinMax = pinMinMax;
        this.initialized = initialized;
    }


    public int getSlotIndex() {
        return slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public String getSlotHex() {
        return slotHex;
    }

    public void setSlotHex(String slotHex) {
        this.slotHex = slotHex;
    }

    public String getSlotDecimal() {
        return slotDecimal;
    }
    public void setSlotDecimal(String slotDecimal) {
        this.slotDecimal = slotDecimal;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getHwVersion() {
        return hwVersion;
    }

    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public void setFwVersion(String fwVersion) {
        this.fwVersion = fwVersion;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPinMinMax() {
        return pinMinMax;
    }

    public void setPinMinMax(String pinMinMax) {
        this.pinMinMax = pinMinMax;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }


}
