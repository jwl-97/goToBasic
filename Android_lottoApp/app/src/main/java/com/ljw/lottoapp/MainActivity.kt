package com.ljw.lottoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.numberPicker)
    }

    private val numberTextviewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.tv_num1),
            findViewById<TextView>(R.id.tv_num2),
            findViewById<TextView>(R.id.tv_num3),
            findViewById<TextView>(R.id.tv_num4),
            findViewById<TextView>(R.id.tv_num5),
            findViewById<TextView>(R.id.tv_num6)
        )
    }

    private var numberPickerSet = mutableSetOf<Int>()
    private var didRun: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45
    }

    private fun getRamdomNumber(): List<Int> {
        didRun = true

        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45) {
                if (numberPickerSet.contains(i)) {
                    continue
                }

                this.add(i)
            }
        }

        numberList.shuffle()
        val list = numberPickerSet + numberList.subList(0, 6 - numberPickerSet.size)

        return list.sorted()
    }

    private fun setNumberToTextview(textview: TextView, value: Int) {
        textview.visibility = View.VISIBLE
        textview.text = value.toString()

        when (value) {
            in 1..10 -> textview.setBackgroundResource(R.drawable.circle_yellow)
            in 11..20 -> textview.setBackgroundResource(R.drawable.circle_blue)
            in 21..30 -> textview.setBackgroundResource(R.drawable.circle_red)
            in 31..40 -> textview.setBackgroundResource(R.drawable.circle_gray)
            else -> textview.setBackgroundResource(R.drawable.circle_green)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                if (didRun) {
                    Toast.makeText(this, "초기화 후에 시도해 주세요", Toast.LENGTH_LONG).show()
                    return
                }

                if (numberPickerSet.size >= 5) {
                    Toast.makeText(this, "번호는 5개까지 선택가능합니다.", Toast.LENGTH_LONG).show()
                    return
                }

                if (numberPickerSet.contains(numberPicker.value)) {
                    Toast.makeText(this, "이미 선택된 번호입니다.", Toast.LENGTH_LONG).show()
                    return
                }

                val textView = numberTextviewList[numberPickerSet.size]
                setNumberToTextview(textView, numberPicker.value)

                numberPickerSet.add(numberPicker.value)
            }

            R.id.btn_reset -> {
                numberPickerSet.clear()
                numberTextviewList.forEach {
                    it.visibility = View.GONE
                }

                didRun = false
            }

            R.id.btn_start -> {
                val result = getRamdomNumber()

                numberTextviewList.forEachIndexed { index, textview ->
                    setNumberToTextview(textview, result[index])
                }
            }
        }
    }
}
