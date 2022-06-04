package com.kamaboko.voiceapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Voice::class], version = 1, exportSchema = false)
abstract class VoiceDatabase : RoomDatabase() {

    abstract val voiceDao: VoiceDao

    companion object {

        @Volatile
        private var INSTANCE: VoiceDatabase? = null

        fun getInstance(context: Context): VoiceDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        VoiceDatabase::class.java,
                        "voice_table"
                    )
                        .createFromAsset("voice_database/voice_table")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }

}