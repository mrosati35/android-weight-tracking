package com.rosati.weighttracking;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "weight_entries",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "username",
                childColumns = "username",
                onDelete = ForeignKey.CASCADE))
public class WeightEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String username;

    public float weight;

    public long timestamp;

    public WeightEntry(@NonNull String username, float weight, long timestamp) {
        this.username = username;
        this.weight = weight;
        this.timestamp = timestamp;
    }
}
