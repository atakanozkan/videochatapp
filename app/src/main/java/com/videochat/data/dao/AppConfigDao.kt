package com.videochat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.videochat.domain.entity.AppConfigEntity

@Dao
interface AppConfigDao {
    @Query("SELECT * FROM app_config WHERE id = :id")
    fun getConfig(id: Int): AppConfigEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConfig(config: AppConfigEntity)

    @Update
    fun updateConfig(config: AppConfigEntity)
}