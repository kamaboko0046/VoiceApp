package com.kamaboko.voiceapp.input

import android.app.Application
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.database.VoiceDao
import com.kamaboko.voiceapp.database.VoiceDatabase
import kotlinx.coroutines.launch

class InputViewModel(application: Application) :ViewModel(){

    // テキスト保管
    var nowText: String = "";

    val dao = VoiceDatabase.getInstance(application).voiceDao

    fun insert(text : String){
        viewModelScope.launch {
            val voice = Voice()
            voice.messege = text
            dao.insert(voice)
        }
    }

}