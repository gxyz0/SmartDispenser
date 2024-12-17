package com.example.smartdispenser.database;

public class Medication {
    private int id;
    private int medicationId;
    private int userId;
    private String medicationName;
    private int medicationQuantity;
    private String medicationNote;
    private String medicationImage;

    public Medication(int id, int medicationId, int userId, String medicationName, int medicationQuantity, String medicationNote, String medicationImage) {
        this.id = id;
        this.medicationId = medicationId;
        this.userId = userId;
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.medicationNote = medicationNote;
        this.medicationImage = medicationImage;
    }

    public void setMedication(String medicationName, int medicationQuantity, String medicationNote, String medicationImage) {
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.medicationNote = medicationNote;
        this.medicationImage = medicationImage;
    }

    public int getId() {
        return id;
    }

    public int getMedicationId() {
        return medicationId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getMedicationQuantity() {
        return medicationQuantity;
    }

    public String getMedicationNote() {
        return medicationNote;
    }

    public String getMedicationImage() {
        return medicationImage;
    }
}
