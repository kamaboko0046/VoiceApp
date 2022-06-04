package com.kamaboko.voiceapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VoiceDao {

    @Insert
    suspend fun insert(voice: Voice)

    @Query("Select * From voice_table order by create_time_milli")
    fun getAll(): LiveData<List<Voice>>

    @Delete
    suspend fun delete(voice: Voice)


}