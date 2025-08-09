package com.example.kaleidoscopeai.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.TypeConverters

@Database(
    entities = [NodeReflection::class, NodeStateEntity::class, BondStateEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class KaleidoscopeDatabase : RoomDatabase() {
    abstract fun nodeReflectionDao(): NodeReflectionDao
    abstract fun nodeStateDao(): NodeStateDao
    abstract fun bondStateDao(): BondStateDao
    
    companion object {
        // Migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns to existing tables
                database.execSQL("ALTER TABLE node_states ADD COLUMN stress REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE node_states ADD COLUMN awareness REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE node_states ADD COLUMN memoryLoad REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE node_states ADD COLUMN knowledge REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE node_states ADD COLUMN emotion REAL NOT NULL DEFAULT 0.0")
                
                // Add new table for system metrics
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS system_metrics (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        timestamp INTEGER NOT NULL,
                        homologySignature TEXT,
                        intelligenceMetrics TEXT,
                        performanceMetrics TEXT
                    )
                """)
            }
        }
        
        // Migration from version 2 to 3 (future)
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Future migration logic here
                // Example: Add quantum state columns
                database.execSQL("ALTER TABLE node_states ADD COLUMN quantumState TEXT")
            }
        }
        
        // Get all migrations
        fun getAllMigrations(): Array<Migration> = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3
        )
    }
}