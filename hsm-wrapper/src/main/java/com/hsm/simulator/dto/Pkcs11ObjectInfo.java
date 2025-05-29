package com.hsm.simulator.dto;

public class Pkcs11ObjectInfo {

    private String type;     // cert or key
    private String label;
    private String id;
    private String subject;
    private String usage;
    private String access;

    public Pkcs11ObjectInfo(){

    }

    public Pkcs11ObjectInfo(String type, String label, String id, String subject, String usage, String access) {
        this.type = type;
        this.label = label;
        this.id = id;
        this.subject = subject;
        this.usage = usage;
        this.access = access;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

}
