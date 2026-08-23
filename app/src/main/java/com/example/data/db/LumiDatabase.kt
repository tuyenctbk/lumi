package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VocabularyItemEntity::class,
        WordProgressEntity::class,
        BadgeEntity::class,
        LearningSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LumiDatabase : RoomDatabase() {
    abstract fun lumiDao(): LumiDao
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: LumiDatabase? = null

        fun getInstance(context: Context): LumiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LumiDatabase::class.java,
                    "lumi_learning.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
