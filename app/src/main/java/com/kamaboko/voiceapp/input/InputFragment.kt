package com.kamaboko.voiceapp.input

import android.animation.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kamaboko.voiceapp.R
import com.kamaboko.voiceapp.databinding.FragmentInputBinding
import com.kamaboko.voiceapp.output.OutputFragmentDirections
import java.util.*

class InputFragment : Fragment(), RecognitionListener {

    // アニメーション
    lateinit var one: ObjectAnimator
    lateinit var two: ObjectAnimator
    lateinit var three: ObjectAnimator
    lateinit var four: ObjectAnimator
    lateinit var finve: ObjectAnimator
    lateinit var six: ObjectAnimator
    lateinit var seven: ObjectAnimator
    lateinit var nine: ObjectAnimator

    // 音声認識
    private var speechRecognizer: SpeechRecognizer? = null

    // バインド
    private lateinit var binding: FragmentInputBinding

    // 音声認識エラーカウント
    private var errCnt: Int = 0

    companion object {
        // 音声認識エラー回数上限
        const val ERR_LIMIT: Int = 3
    }

    private val viewModel: InputViewModel by lazy {
        val application = requireNotNull(this.activity).application
        ViewModelProvider(
            this, InputViewModelFactory(application)
        ).get(InputViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Menu設定
        setHasOptionsMenu(true)

        // binding取得
        binding = FragmentInputBinding.inflate(layoutInflater)
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // アニメーションをセット
        one = moveUp(binding.textOne, 0)
        two = moveUp(binding.textTwo, 100)
        three = moveUp(binding.textThree, 200)
        four = moveUp(binding.textFour, 300)
        finve = moveUp(binding.textFive, 400)
        six = moveUp(binding.textSix, 500)
        seven = moveUp(binding.textSeven, 600)
        nine = moveUp(binding.textNine, 700)

        // 音声認識開始ボタンリスナー
        binding.voiceRecBtn.setOnClickListener {
            startSpeechRecognize()
            // アニメーションスタート
            one.start()
            two.start()
            three.start()
            four.start()
            finve.start()
            six.start()
            seven.start()
            nine.start()
            moveLeft()
        }

        // 音声認識終了ボタンリスナー
        binding.voiceStopBtn.setOnClickListener {
            // エラーカウントアップ
            errCnt = ERR_LIMIT + 101
            speechRecognizer?.stopListening()
        }

        // This callback will only be called when MyFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.editTextTextMultiLine2.text.isNotEmpty()) {
                viewModel.insert(binding.editTextTextMultiLine2.text.toString())
                val mySnackbar = Snackbar.make(
                    binding.root,
                    "テキストを保存しました", Snackbar.LENGTH_SHORT
                )
                mySnackbar.show()
            }
            val action =
                InputFragmentDirections.actionInputFragmentToOutputFragment()
            findNavController().navigate(action)
        }

        // ダークモード対応
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // SDKのバージョンがR以降である場合にダークモード設定が導入されたため、それを判定する
            if (requireContext().theme.resources.configuration.isNightModeActive) {
                // ダークモードの場合にこのスコープに入る
                binding.inputBack.setBackgroundResource(R.drawable.darkbacj)
            }
        }

        return binding.root
    }

    // 音声認識開始
    private fun startSpeechRecognize() {
        // テキスト状態を同期
        viewModel.nowText = binding.editTextTextMultiLine2.text.toString()

        // 音声認識待機画面制御
        isRecognizing(true)

        // SpeechRecognizerを破棄する
        speechRecognizer?.destroy()

        // SpeechRecognizer初期設定
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(this)

        // インテント準備
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        // 認識スタート
        speechRecognizer?.startListening(intent)
    }

    // 音声認識終了
    private fun stopSpeechRecognize() {
        Log.w("speech", "音声終了メソッド開始")

        // 音声認識待機画面制御
        isRecognizing(false)
        one.cancel()
        two.cancel()
        three.cancel()
        four.cancel()
        finve.cancel()
        six.cancel()
        seven.cancel()
        nine.cancel()
        moveRight()

        // SpeechRecognizerを破棄する
        speechRecognizer?.destroy()
    }

    // ライフサイクル
    override fun onPause() {
        super.onPause()
        speechRecognizer?.stopListening()
    }


    override fun onDestroy() {
        // SpeechRecognizerを破棄する
        speechRecognizer?.destroy()
        super.onDestroy()
    }

    // Menu設定
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sample_menu, menu)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "キク"
    }

    // Menuリスナー
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                if (binding.editTextTextMultiLine2.text.isNotEmpty()) {
                    viewModel.insert(binding.editTextTextMultiLine2.text.toString())
                    val mySnackbar = Snackbar.make(
                        binding.root,
                        "テキストを保存しました", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                    val action =
                        InputFragmentDirections.actionInputFragmentToOutputFragment()
                    findNavController().navigate(action)
                } else {
                    val mySnackbar = Snackbar.make(
                        binding.root,
                        "1文字以上、テキストを入力してください", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }

                true
            }
            android.R.id.home -> {
                // editTextに1文字以上入力されていれば保存する
                if (binding.editTextTextMultiLine2.text.isNotEmpty()) {
                    viewModel.insert(binding.editTextTextMultiLine2.text.toString())
                    val mySnackbar = Snackbar.make(
                        binding.root,
                        "テキストを保存しました", Snackbar.LENGTH_SHORT
                    )
                    mySnackbar.show()
                }
                // 画面遷移
                val action =
                    InputFragmentDirections.actionInputFragmentToOutputFragment()
                findNavController().navigate(action)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // スピーチリスナー

    // 部分認識
    override fun onPartialResults(partialResults: Bundle) {
        val stringArray =
            partialResults.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        if (stringArray != null) {
            if (stringArray[0].isNotEmpty()) {
                val dipText = viewModel.nowText + "\n" + stringArray.get(0)
                binding.editTextTextMultiLine2.setText(dipText)
            }
        }
    }

    // 認識エラー
    override fun onError(error: Int) {
        when (error) {
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT, SpeechRecognizer.ERROR_NO_MATCH -> {
                errCnt++
                Log.w("speech", "ErrorCnt:$errCnt")
                // エラー上限の場合は音声認識終了
                if (errCnt >= ERR_LIMIT) {
                    Log.w("speech", "ErrorCnt:音声終了ルート")
                    errCnt = 0;
                    stopSpeechRecognize()
                } else {
                    startSpeechRecognize()
                }
            }
            else -> {
                // 何がしかのエラーのため終了
                Log.w("speech", "Error:$error")
                errCnt = 0;
            }
        }
    }

    // 認識終了
    override fun onResults(results: Bundle) {
        val stringArray =
            results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
        if (stringArray != null) {
            if (stringArray[0].isNotEmpty()) {
                val dipText = viewModel.nowText + "\n" + stringArray.get(0)
                binding.editTextTextMultiLine2.setText(dipText)
            }
        }
        // 再度音声認識開始
        if (errCnt < ERR_LIMIT + 100) {
            startSpeechRecognize()
            errCnt = 0
        } else {
            Log.d("test","stop")
            // 音声認識待機画面制御
            isRecognizing(false)
            one.cancel()
            two.cancel()
            three.cancel()
            four.cancel()
            finve.cancel()
            six.cancel()
            seven.cancel()
            nine.cancel()
            moveRight()
        }
    }

    // 画面状態制御
    // true 音声認識中 false 音声認識待機状態
    private fun isRecognizing(f: Boolean) {
        if (f) {
            // 音声認識中
            binding.voiceRecBtn.visibility = View.GONE
            binding.editTextTextMultiLine2.isEnabled = false
            binding.voiceStopBtn.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE
            binding.view.visibility = View.VISIBLE
            binding.textOne.visibility = View.VISIBLE
            binding.textTwo.visibility = View.VISIBLE
            binding.textThree.visibility = View.VISIBLE
            binding.textFour.visibility = View.VISIBLE
            binding.textFive.visibility = View.VISIBLE
            binding.textSix.visibility = View.VISIBLE
            binding.textSeven.visibility = View.VISIBLE
            binding.textNine.visibility = View.VISIBLE
        } else {
            // 音声認識待機中
            binding.voiceRecBtn.visibility = View.VISIBLE
            binding.editTextTextMultiLine2.isEnabled = true
            binding.voiceStopBtn.visibility = View.GONE
            binding.view.visibility = View.GONE
            binding.textOne.visibility = View.GONE
            binding.textTwo.visibility = View.GONE
            binding.textThree.visibility = View.GONE
            binding.textFour.visibility = View.GONE
            binding.textFive.visibility = View.GONE
            binding.textSix.visibility = View.GONE
            binding.textSeven.visibility = View.GONE
            binding.textNine.visibility = View.GONE
        }
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onReadyForSpeech(params: Bundle) {}
    override fun onBufferReceived(buffer: ByteArray) {}
    override fun onEvent(eventType: Int, params: Bundle) {}
    override fun onBeginningOfSpeech() {}
    override fun onEndOfSpeech() {}

    // アニメーション
    // 上に移動
    private fun moveUp(view: View, time: Long): ObjectAnimator {
        val move = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -30f, 0f, 0f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.5f, 1.0f, 0.5f)
        val anim =
            ObjectAnimator.ofPropertyValuesHolder(view, move, alpha)
        anim.duration = 1500L
        anim.startDelay = time
        anim.repeatCount = Animation.INFINITE
        anim.repeatMode = ValueAnimator.RESTART
        anim.interpolator = AccelerateDecelerateInterpolator()
        return anim
    }

    // ろば登場
    private fun moveLeft() {
        val move = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,  0f,-200f)
        val anim =
            ObjectAnimator.ofPropertyValuesHolder(binding.inputRoba,move)
        anim.duration = 1500L
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.start()
    }

    // ろば退場
    private fun moveRight() {
        Log.d("test","aa")
        val move = PropertyValuesHolder.ofFloat(View.TRANSLATION_X,  -200f,-0f)
        val anim =
            ObjectAnimator.ofPropertyValuesHolder(binding.inputRoba,move)
        anim.duration = 1500L
        anim.interpolator = AccelerateInterpolator()
        anim.start()
    }

}