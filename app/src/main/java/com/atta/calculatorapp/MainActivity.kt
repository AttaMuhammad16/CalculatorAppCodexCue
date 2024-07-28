package com.atta.calculatorapp

import android.graphics.Color
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.utils.Utils

class MainActivity : AppCompatActivity() {
    private lateinit var inputTextView: TextView
    private lateinit var outputTextView: TextView
    private lateinit var button_delete: AppCompatButton
    private lateinit var vibrator: Vibrator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        inputTextView = findViewById(R.id.input)
        outputTextView = findViewById(R.id.output)
        button_delete = findViewById(R.id.button_delete)
        window.statusBarColor=ContextCompat.getColor(this,R.color.tool_bar_color)


        val buttonClickListener = View.OnClickListener { v ->
            val clickedButton = v as Button
            val buttonText = clickedButton.text.toString()
            when (v.id) {
                R.id.button_equals -> evaluateExpression()
                R.id.button_clear -> clearInput()
                else -> appendInput(buttonText)
            }
        }


        button_delete.setOnClickListener{
            deleteInputCharacter()
        }


        val buttonIds = arrayOf(
            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_dot, R.id.button_addition, R.id.button_subtraction,
            R.id.button_multiply, R.id.button_division
        )

        for (buttonId in buttonIds) {
            val button = findViewById<AppCompatButton>(buttonId)
            button.setOnClickListener(buttonClickListener)
        }
        findViewById<AppCompatButton>(R.id.button_equals).setOnClickListener(buttonClickListener)
        findViewById<AppCompatButton>(R.id.button_clear).setOnClickListener(buttonClickListener)

    }

    private fun appendInput(text: String) {
        inputTextView.append(text)
    }

    private fun clearInput() {
        inputTextView.text = ""
        outputTextView.text = ""
    }

    private fun evaluateExpression() {
        val expression = inputTextView.text.toString()
        try {
            val result = evaluate(expression)
            outputTextView.setTextColor(Color.WHITE)
            outputTextView.text = result.toString()
        } catch (e: Exception) {
            outputTextView.setTextColor(Color.RED)
            outputTextView.text = "Error"
        }
    }



    private fun evaluate(expression: String): Double {
        val operators = setOf("+", "-", "×", "÷")
        val tokens = mutableListOf<String>()
        var currentToken = ""

        var validExpression = false // Flag to track if the expression starts with a valid token
        val bracketStack = mutableListOf<String>()

        for (i in expression.indices) {
            val char = expression[i]

            if (char.toString() in operators) {
                if (currentToken.isNotEmpty()) {
                    tokens.add(currentToken)
                    currentToken = ""
                }
                if (i == 0 || expression[i - 1].toString() in operators) {
                    currentToken += char
                } else {
                    tokens.add(char.toString())
                }

                if (char.toString() == "(") {
                    bracketStack.add("(")
                } else if (char.toString() == ")") {
                    if (bracketStack.isEmpty()) {
                        throw IllegalArgumentException("Invalid input: Mismatched brackets")
                    } else {
                        bracketStack.removeAt(bracketStack.size - 1)
                    }
                }
            } else {
                currentToken += char
            }
        }

        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken)
        }

        if (tokens.isNotEmpty()) {
            val firstToken = tokens[0]
            validExpression = firstToken.toDoubleOrNull() != null || firstToken == "("
        }
        if (!validExpression || bracketStack.isNotEmpty()) {
            throw IllegalArgumentException("Invalid input")
        }

        var accumulator = 0.0
        var currentOperator = "+"
        var lastTokenWasOperator = true // Initialize to true to catch leading double operators

        for (token in tokens) {
            if (token in operators) {
                if (lastTokenWasOperator || token == "*" || token == "/") {
                    throw IllegalArgumentException("Invalid input: Invalid operator placement")
                }
                currentOperator = token
                lastTokenWasOperator = true
            } else {
                val value = token.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid input")
                when (currentOperator) {
                    "+" -> accumulator += value
                    "-" -> accumulator -= value
                    "×" -> accumulator *= value
                    "÷" -> accumulator /= value
                }
                lastTokenWasOperator = false
            }
        }
        return accumulator
    }

    private fun deleteInputCharacter() {
        val currentInput = inputTextView.text.toString()
        if (currentInput.isNotEmpty()) {
            val newInput = currentInput.substring(0, currentInput.length - 1)
            inputTextView.text = newInput
        }
    }

}