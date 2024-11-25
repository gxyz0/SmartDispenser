package com.example.smartdispenser.room.medication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Medication {
    @PrimaryKey(autoGenerate = true)
    public int _id;

    @ColumnInfo(name = "medication_id")
    public int medicationId;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "medication_name")
    public String medicationName;

    @ColumnInfo(name = "medication_quantity")
    public int medicationQuantity;

    @ColumnInfo(name = "medication_note")
    public String medicationNote;

    public Medication(int medicationId, int userId, String medicationName, int medicationQuantity, String medicationNote) {
        this.medicationId = medicationId;
        this.userId = userId;
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.medicationNote = medicationNote;
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

    public void setMedication(String medicationName, int medicationQuantity, String medicationNote) {
        this.medicationName = medicationName;
        this.medicationQuantity = medicationQuantity;
        this.medicationNote = medicationNote;
    }
}
