package com.rosati.weighttracking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries WHERE username = :username ORDER BY timestamp DESC")
    List<WeightEntry> getWeightEntriesForUser(String username);

    @Insert
    void insert(WeightEntry entry);

    @androidx.room.Delete
    void delete(WeightEntry entry);
}
