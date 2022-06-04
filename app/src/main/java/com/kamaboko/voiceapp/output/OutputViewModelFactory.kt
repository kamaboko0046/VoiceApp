package com.kamaboko.voiceapp.output

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kamaboko.voiceapp.database.VoiceDao
import com.kamaboko.voiceapp.manege.ManageViewModel

class OutputViewModelFactory(private val dao: VoiceDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OutputViewModel::class.java)) {
            return OutputViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}