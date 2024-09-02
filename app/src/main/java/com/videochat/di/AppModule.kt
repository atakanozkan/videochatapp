package com.videochat.di
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.videochat.data.dao.AppConfigDao
import com.videochat.data.dao.UserDao
import com.videochat.data.db.AppDatabase
import com.videochat.data.source.FirestoreSource
import com.videochat.repository.AppConfigRepository
import com.videochat.repository.UserCacheRepository
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

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreSource(firestore: FirebaseFirestore): FirestoreSource = FirestoreSource(firestore)
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideUserRepository(userDao: UserDao): UserCacheRepository = UserCacheRepository(userDao)


    @Provides
    fun provideAppConfigRepository(appConfigDao: AppConfigDao): AppConfigRepository = AppConfigRepository(appConfigDao)
}