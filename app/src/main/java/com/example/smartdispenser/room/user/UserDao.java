package com.example.smartdispenser.room.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    //增
    @Insert
    void insertUsers(User... users);

    //删
    @Delete
    void deleteUser(User...users);

    //改
    @Update
    void updateUsers(User... users);

    //删除所有
    @Query("DELETE FROM User")
    void deleteAllUsers();

    //查询所有
    @Query("SELECT * FROM User ORDER BY user_id DESC")
    List<User> getAllUsers();

}
