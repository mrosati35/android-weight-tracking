package com.rosati.weighttracking;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String username;

    public String password;

    public int goalWeight;

    public String phoneNumber;

    public User(@NonNull String username, String password, int goalWeight, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.goalWeight = goalWeight;
        this.phoneNumber = phoneNumber;
    }
}
