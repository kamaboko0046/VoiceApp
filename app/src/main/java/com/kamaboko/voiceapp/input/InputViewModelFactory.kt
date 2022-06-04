package com.kamaboko.voiceapp.input

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kamaboko.voiceapp.database.VoiceDao

class InputViewModelFactory(val app: Application) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InputViewModel::class.java)) {
            return InputViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}