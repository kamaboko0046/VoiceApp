package com.kamaboko.voiceapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_table")
data class Voice(
    @PrimaryKey(autoGenerate = true)
    var voiceId: Long = 0L,

    @ColumnInfo(name = "create_time_milli")
    val createTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "voice_messege")
    var messege: String = ""
)
