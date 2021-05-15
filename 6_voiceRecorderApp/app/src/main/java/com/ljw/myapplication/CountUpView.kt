package com.ljw.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class CountUpView(context: Context, attributeSet: AttributeSet) :
    AppCompatTextView(context, attributeSet) {

    private var timeStamp: Long = 0L

    private val countUp: Runnable = object : Runnable {
        override fun run() {
            val currentTimeStamp = SystemClock.elapsedRealtime()

            val countTime = ((currentTimeStamp - timeStamp) / 1000L).toInt()
            updateCountTime(countTime)

            handler?.postDelayed(this, 1000L)
        }
    }

    fun startCountUp() {
        timeStamp = SystemClock.elapsedRealtime()
        handler?.post(countUp)
    }

    fun stopCountUp() {
        handler?.removeCallbacks(countUp)
    }

    fun clearCountUp() {
        updateCountTime(0)
    }

    @SuppressLint("SetTextI18n")
    fun updateCountTime(countTime: Int) {
        val minutes = countTime / 60
        val seconds = countTime % 60

        text = "%02d:%02d".format(minutes, seconds)
    }
}