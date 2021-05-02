package com.ljw.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

class SoundVisualizerView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500) //라인 색상
        strokeWidth = LINE_WIDTH //라인 넓이
        strokeCap = Paint.Cap.ROUND //라인의 양끝
    }

    var drawingWidth: Int = 0
    var drawingHeight: Int = 0
    var drawingAmplitudes: List<Int> = emptyList()
    var isReplaying: Boolean = false
    var replayingPosition: Int = 0

    private val visualizeRepeat: Runnable = object : Runnable {
        override fun run() {
            if (!isReplaying) {
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                replayingPosition++
            }

            invalidate() //갱신
            handler?.postDelayed(this, 20L)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //라인의 높이는 실제 값(볼륨)을 기반으로
        canvas ?: return

        val centerY = drawingHeight / 2f //가운데 위치
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitudes ->
                if (isReplaying) {
                    amplitudes.takeLast(replayingPosition) //뒤집기
                } else {
                    amplitudes
                }
            }.forEach { amplitudes ->
                val lineLength = amplitudes / MAX_AMPLITUDE * drawingHeight * 0.8F

                offsetX -= LINE_SPACE
                if (offsetX < 0) return@forEach

                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2,
                    offsetX,
                    centerY + lineLength / 2,
                    amplitudePaint
                )
            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeat)
    }

    fun stopVisualizing() {
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeat)
    }

    fun clearVisualizing() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
    }
}