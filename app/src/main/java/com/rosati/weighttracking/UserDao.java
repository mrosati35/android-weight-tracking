package com.rosati.weighttracking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findByUsername(String username);

    @Insert
    void insert(User user);

    @Query("UPDATE users SET goalWeight = :newGoalWeight WHERE username = :username")
    void updateGoalWeight(String username, int newGoalWeight);
}
