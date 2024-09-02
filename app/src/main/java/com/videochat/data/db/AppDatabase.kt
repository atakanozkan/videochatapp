package com.videochat.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.videochat.common.extension.Converters
import com.videochat.data.dao.AppConfigDao
import com.videochat.data.dao.UserDao
import com.videochat.domain.entity.config.AppConfigEntity
import com.videochat.domain.entity.user.UserEntity

@Database(entities = [UserEntity::class, AppConfigEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun appConfig(): AppConfigDao
}
