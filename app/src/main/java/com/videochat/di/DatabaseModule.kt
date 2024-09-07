package com.videochat.di

import android.content.Context
import androidx.room.Room
import com.videochat.data.dao.AppConfigDao
import com.videochat.data.dao.UserDao
import com.videochat.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    )
        .build()

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()

    @Provides
    fun provideAppConfigDao(appDatabase: AppDatabase): AppConfigDao = appDatabase.appConfig()
}
