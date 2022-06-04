package com.kamaboko.voiceapp.manege

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kamaboko.voiceapp.database.VoiceDao

class ManageViewModelFactory(private val dao: VoiceDao) : ViewModelProvider.Factory{

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ManageViewModel::class.java)) {
            return ManageViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}