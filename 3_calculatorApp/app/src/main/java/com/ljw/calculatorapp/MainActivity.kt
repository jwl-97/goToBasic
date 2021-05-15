package com.ljw.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.ljw.calculatorapp.model.History

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val expressionTextview: TextView by lazy {
        findViewById<TextView>(R.id.tv_expression)
    }

    private val resultTextview: TextView by lazy {
        findViewById<TextView>(R.id.tv_result)
    }

    private val historyLayout: View by lazy {
        findViewById<View>(R.id.view_history)
    }

    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.ll_history)
    }

    lateinit var db: AppDataBase

    var isOperator = false
    var hasOperator = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = Room.databaseBuilder(this, AppDataBase::class.java, "histroyDB").build()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_0 -> numberButtonClicked("0")
            R.id.btn_1 -> numberButtonClicked("1")
            R.id.btn_2 -> numberButtonClicked("2")
            R.id.btn_3 -> numberButtonClicked("3")
            R.id.btn_4 -> numberButtonClicked("4")
            R.id.btn_5 -> numberButtonClicked("5")
            R.id.btn_6 -> numberButtonClicked("6")
            R.id.btn_7 -> numberButtonClicked("7")
            R.id.btn_8 -> numberButtonClicked("8")
            R.id.btn_9 -> numberButtonClicked("9")

            R.id.btn_plus -> operatorButtonClicked("+")
            R.id.btn_minus -> operatorButtonClicked("-")
            R.id.btn_multiple -> operatorButtonClicked("*")
            R.id.btn_divide -> operatorButtonClicked("/")
            R.id.btn_modulo -> operatorButtonClicked("%")
        }
    }

    private fun numberButtonClicked(number: String) {
        if (isOperator) {
            expressionTextview.append(" ")
        }

        isOperator = false

        val expressionText = expressionTextview.text.split(" ")

        if (expressionText.isNotEmpty() && expressionText.last().length > 15) {
            Toast.makeText(this, "15자리까지만 입력 가능합니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            return
        }

        expressionTextview.append(number)
        resultTextview.text = calculateExpression()
    }

    private fun operatorButtonClicked(operator: String) {
        if (expressionTextview.text.isEmpty()) {
            return
        }

        when {
            isOperator -> {
                val text = expressionTextview.text.toString()
                expressionTextview.text = text.dropLast(1) + operator
            }

            hasOperator -> {
                Toast.makeText(this, "연산자는 한 번만 사용 가능합니다.", Toast.LENGTH_SHORT).show()
                return
            }

            else -> expressionTextview.append(" $operator")
        }

        val ssb = SpannableStringBuilder(expressionTextview.text)
        ssb.setSpan(ForegroundColorSpan(getColor(R.color.green)), expressionTextview.text.length - 1, expressionTextview.text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        expressionTextview.text = ssb

        isOperator = true
        hasOperator = true
    }

    private fun calculateExpression(): String {
        val expressionText = expressionTextview.text.split(" ")

        if (hasOperator.not() || expressionText.size != 3) {
            return ""
        } else if (expressionText[0].isNumber().not() || expressionText[2].isNumber().not()) {
            return ""
        }

        val exp1 = expressionText[0].toBigInteger()
        val exp2 = expressionText[2].toBigInteger()
        val operator = expressionText[1]

        return when (operator) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextview.text.split(" ")

        if (expressionTextview.text.isEmpty() || expressionTexts.size == 1) {
            return
        }

        if (expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "연산식을 완성해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextview.text.toString()
        val resultText = calculateExpression()

        Thread(Runnable {
            db.historyDao().insertHistory(History(null, expressionText, resultText))
        }).start()

        resultTextview.text = ""
        expressionTextview.text = resultText

        isOperator = false
        hasOperator = false
    }

    fun clearButtonClicked(v: View) {
        expressionTextview.text = ""
        resultTextview.text = ""

        isOperator = false
        hasOperator = false
    }

    //////////////////////////////////////////////////////////////////////////////

    fun historyButtonClicked(v: View) {
        historyLayout.visibility = View.VISIBLE
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach {
                runOnUiThread {
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row, null, false)
                    historyView.findViewById<TextView>(R.id.tv_expression).text = it.expression
                    historyView.findViewById<TextView>(R.id.tv_result).text = " ${it.result}"

                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()
    }

    fun historyCloseButtonClicked(v: View) {
        historyLayout.visibility = View.INVISIBLE
    }

    fun historyClearButtonClicked(v: View) {
        historyLinearLayout.removeAllViews()

        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }
}

fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
