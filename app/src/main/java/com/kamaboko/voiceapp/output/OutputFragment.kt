package com.kamaboko.voiceapp.output

import android.animation.*
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kamaboko.voiceapp.R
import com.kamaboko.voiceapp.database.Voice
import com.kamaboko.voiceapp.database.VoiceDatabase
import com.kamaboko.voiceapp.databinding.FragmentOutputBinding
import java.util.*
import kotlin.collections.ArrayDeque

class OutputFragment : Fragment(), TextToSpeech.OnInitListener {

    // テキスト読み上げ
    private var textToSpeech: TextToSpeech? = null

    // データバインド
    private lateinit var binding: FragmentOutputBinding

    // アニメーションストップフラグ
    private var animStop: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Menu設定
        setHasOptionsMenu(true)

        // DBデータ設定
        val application = requireNotNull(this.activity).application
        val db = VoiceDatabase.getInstance(application)

        // TextToSpeechインスタンスを作成
        textToSpeech = TextToSpeech(context, this)

        // データバインド
        binding = FragmentOutputBinding.inflate(layoutInflater)
        binding.lifecycleOwner = viewLifecycleOwner

        // ViewModel設定
        val viewModelFactory = OutputViewModelFactory(db.voiceDao)
        val viewModel = ViewModelProvider(
            this, viewModelFactory
        )[OutputViewModel::class.java]

        // レッツ、スピーク！
        viewModel.voiceSpeak.observe(viewLifecycleOwner) {
            if (viewModel.isVoice && it && !textToSpeech!!.isSpeaking) {
                Log.i("test", "スピーチ開始")
                if (!viewModel.voiceList.isNotEmpty()) {
                    // 入れ直す
                    viewModel.voice.value!!.shuffled().forEach { v ->
                        viewModel.voiceList.add(v)
                    }
                }
                val text: String = viewModel.voiceList.removeFirst().messege
                startSpeak(text)
                binding.textView2.text = text
                viewModel.endVoice()
            }
        }

        // DBデータ取得ずみ？
        viewModel.voice.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotEmpty()) {
                    Log.i("test", "DB入りました")
                    viewModel.isVoice = true
                    viewModel.voice.value!!.shuffled().forEach { v ->
                        viewModel.voiceList.add(v)
                    }
                }
            }
        }

        // フローティングボタン
        binding.floatingActionButton.setOnClickListener {
            val action =
                OutputFragmentDirections.actionOutputFragmentToInputFragment()
            findNavController().navigate(action)
        }

        // ダークモード対応
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (requireContext().theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                binding.outputBack.setBackgroundResource(R.drawable.darkbacj)
            }
        }

        // アニメーションスタート
        animStop = false
        moveDonkey()
        moveCloud(binding.cloud01)
        moveCloud(binding.cloud02)

        return binding.root
    }

    // TextToSpeechを初期化
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val locale = Locale.JAPAN
                if (tts.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = Locale.JAPAN
                } else {
                    // 言語の設定に失敗
                }
            }
        } else {
            // Tts init 失敗
        }
    }

    // テキストを音声出力する
    private fun startSpeak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    override fun onResume() {
        animStop = false
        super.onResume()
    }


    override fun onPause() {
        textToSpeech?.stop()
        animStop = true
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeech?.shutdown()
        super.onDestroy()
    }

    // Menu設定
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.output_menu, menu)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "ハナス"
    }

    // Menuリスナー
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                val action =
                    OutputFragmentDirections.actionOutputFragmentToManageFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // ロバのアニメーション
    private fun moveDonkey() {
        val moverDonkey = ObjectAnimator.ofFloat(binding.donkey, View.TRANSLATION_Y, 0f, -70f, 0f)
        moverDonkey.interpolator = AccelerateInterpolator()
        val rotatorDonkey = ObjectAnimator.ofFloat(binding.donkey, View.ROTATION, 0f, 10f, 0f)
        rotatorDonkey.interpolator = AccelerateDecelerateInterpolator()
        val animSetDonkey = AnimatorSet()
        animSetDonkey.playTogether(moverDonkey, rotatorDonkey)
        animSetDonkey.duration = (Math.random() * 1000 + 500).toLong()
        animSetDonkey.startDelay = (Math.random() * 5000 + 500).toLong()
        animSetDonkey.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (!animStop) {
                    animSetDonkey.duration = (Math.random() * 1000 + 500).toLong()
                    animSetDonkey.startDelay = (Math.random() * 5000 + 500).toLong()
                    animSetDonkey.start()
                }
            }
    })
    animSetDonkey.start()
}

// 雲のアニメーション
private fun moveCloud(view: View) {
    val moverCloud = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -30f, 0f, 30f, 0f)
    val scaleCloudX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
    val scaleCloudY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)
    val animCloud =
        ObjectAnimator.ofPropertyValuesHolder(view, scaleCloudX, scaleCloudY, moverCloud)
    animCloud.duration = (Math.random() * 5000 + 4000).toLong()
    animCloud.interpolator = AccelerateDecelerateInterpolator()
    animCloud.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            if (!animStop) {
                animCloud.duration = (Math.random() * 5000 + 4000).toLong()
                animCloud.start()
            }
        }
    })
    animCloud.start()
}
}

