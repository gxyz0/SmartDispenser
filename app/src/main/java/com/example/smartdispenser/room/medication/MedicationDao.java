package com.example.smartdispenser.room.medication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicationDao {
    //增
    @Insert
    void insertMedications(Medication... medications);

    //删
    @Delete
    void deleteMedication(Medication... medications);

    //改
    @Update
    void updateMedications(Medication... medications);

    //根据userId删除所有
    @Query("DELETE FROM Medication WHERE user_id = :userId")
    void deleteMedicationsByUserId(int userId);

    //根据userId查询所有
    @Query("SELECT * FROM Medication WHERE user_id = :userId ORDER BY medication_id ASC")
    List<Medication> getMedicationsByUserId(int userId);
}
