package com.kamaboko.voiceapp.output

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.database.VoiceDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OutputViewModel(private val dao: VoiceDao) : ViewModel() {

    // ボイスデータ
    val voice = dao.getAll()

    // ボイスデータ
    var isVoice = false

    // 出力ボイスリスト
    var voiceList:ArrayDeque<Voice> = ArrayDeque()

    // 発声フラグ
    private val _voiceSpeak = MutableLiveData<Boolean>()
    val voiceSpeak: LiveData<Boolean>
        get() = _voiceSpeak

    init {
        randomVoice()
    }

    private fun randomVoice() {
        viewModelScope.launch {
            val delayTime = (3000..5000)
            while (true) {
                Log.i("model", "startSpeak")
                delay(delayTime.random().toLong())
                startVoice()
            }
        }
    }

    fun startVoice() {
        _voiceSpeak.value = true;
    }

    fun endVoice() {
        _voiceSpeak.value = false;
    }


}