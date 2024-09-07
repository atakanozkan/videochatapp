package com.videochat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.videochat.data.dao.AppConfigDao
import com.videochat.data.dao.UserDao
import com.videochat.domain.entity.AppConfigEntity
import com.videochat.domain.entity.UserEntity

@Database(entities = [UserEntity::class, AppConfigEntity::class], version = 1, exportSchema = true)
@TypeConverters(com.videochat.common.extension.Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun appConfig(): AppConfigDao
}
