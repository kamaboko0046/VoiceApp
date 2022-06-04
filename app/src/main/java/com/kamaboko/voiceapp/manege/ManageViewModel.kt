package com.kamaboko.voiceapp.manege

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.database.VoiceDao
import kotlinx.coroutines.launch

class ManageViewModel(private val dao: VoiceDao) :ViewModel(){

    public fun delete(voice: Voice){
        viewModelScope.launch {
            dao.delete(voice)
        }
    }

}