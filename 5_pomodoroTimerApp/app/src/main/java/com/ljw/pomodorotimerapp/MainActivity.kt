package com.ljw.pomodorotimerapp

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.tv_remain_minutes)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.tv_remain_seconds)
    }

    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.sb_timer)
    }

    private var countDownTimer: CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSeekbar()
        initSound()
    }

    override fun onResume() {
        super.onResume()

        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()

        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        soundPool.release() //메모리 해제
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun initSeekbar() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    updateRemainTimer(progress * 60 * 1000L)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopCountDown()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar == null) return

                if (seekBar.progress == 0) { //0초로 카운트가 멈췄을때 계속 bell 울리는 현상 수정
                    stopCountDown()
                } else {
                    startCountDown()
                }
            }
        })
    }

    private fun initSound() {
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMills: Long) =
        object : CountDownTimer(initialMills, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTimer(millisUntilFinished)
                updateSeekbar(millisUntilFinished)
            }

            override fun onFinish() {
                finishCountDown()
            }
        }

    private fun startCountDown() {
        countDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        countDownTimer?.start()

        tickingSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun finishCountDown() {
        updateRemainTimer(0)
        updateSeekbar(0)

        soundPool.autoPause()
        bellSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, 0, 1F)
        }
    }

    private fun stopCountDown() {
        countDownTimer?.cancel()
        countDownTimer = null
        soundPool.autoPause()
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTimer(remainMills: Long) {
        val remainSeconds = remainMills / 1000
        remainMinutesTextView.text = "%02d'".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekbar(remainMills: Long) {
        seekBar.progress = (remainMills / 1000 / 60).toInt()
    }
}
