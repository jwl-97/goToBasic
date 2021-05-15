package com.ljw.diaryapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.np_1).apply {
            this.minValue = 0
            this.maxValue = 9
        }
    }

    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.np_2).apply {
            this.minValue = 0
            this.maxValue = 9
        }
    }

    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.np_3).apply {
            this.minValue = 0
            this.maxValue = 9
        }
    }

    private var isChangingPassword: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker1
        numberPicker2
        numberPicker3
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_answer -> {
                if (isChangingPassword) {
                    Toast.makeText(this, "비밀번호 변경중입니다.", Toast.LENGTH_LONG).show()
                    return
                }

                val preferences = getSharedPreferences("password", Context.MODE_PRIVATE)
                val answer = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

                if (preferences.getString("password", "000") == answer) {
                    startActivity(Intent(this@MainActivity, DiaryActivity::class.java))
                } else {
                    setErrorPopup()
                }
            }

            R.id.btn_reset -> {
                if (isChangingPassword) { // 비밀번호 저장
                    val preferences = getSharedPreferences("password", Context.MODE_PRIVATE)
                    preferences.edit(true) {
                        val answer = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
                        putString("password", answer)
                    }

                    setPassword(false)

                } else { // 비밀번호 체크
                    val preferences = getSharedPreferences("password", Context.MODE_PRIVATE)
                    val answer = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

                    if (preferences.getString("password", "000") == answer) {
                        setPassword(true)
                    } else {
                        setErrorPopup()
                    }
                }
            }
        }
    }

    private fun setPassword(isChanging: Boolean) {
        if (isChanging) {
            isChangingPassword = true
            btn_reset.setBackgroundColor(resources.getColor(R.color.colorRed))
            Toast.makeText(this, "변경할 패스워드를 입력해주세요.", Toast.LENGTH_LONG).show()
        } else {
            isChangingPassword = false
            btn_reset.setBackgroundColor(resources.getColor(R.color.colorGray))
            Toast.makeText(this, "변경 완료되었습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setErrorPopup() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("잘못된 비밀번호입니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}
