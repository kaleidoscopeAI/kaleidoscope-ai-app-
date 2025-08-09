package com.example.kaleidoscopeai.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kaleidoscopeai.data.KaleidoscopeDatabase
import com.example.kaleidoscopeai.data.MemoryRepository
import com.example.kaleidoscopeai.llm.LocalLLM
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): KaleidoscopeDatabase {
        return Room.databaseBuilder(
            appContext,
            KaleidoscopeDatabase::class.java,
            "kaleidoscope.db"
        )
        .addMigrations(*KaleidoscopeDatabase.getAllMigrations())
        .fallbackToDestructiveMigration() // Only for development - remove in production
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
    }

    @Provides
    @Singleton
    fun provideMemoryRepository(db: KaleidoscopeDatabase): MemoryRepository = MemoryRepository(db)
    
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    
    @Provides
    @Singleton
    fun provideLocalLLM(coroutineScope: CoroutineScope): LocalLLM {
        return LocalLLM(coroutineScope = coroutineScope)
    }
}
