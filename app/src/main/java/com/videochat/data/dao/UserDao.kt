package com.videochat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.videochat.domain.entity.user.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Update
    fun update(user: UserEntity)

    @Query("DELETE FROM users WHERE 1=1")
    fun deleteUser()
}
