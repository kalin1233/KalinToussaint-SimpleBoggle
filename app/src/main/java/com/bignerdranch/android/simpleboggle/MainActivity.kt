package com.bignerdranch.android.simpleboggle

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private val usedButtons = mutableListOf<Button>()
    private lateinit var userInputTextView: TextView
    private var lastClickedButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = listOf(
            findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3), findViewById(R.id.button4),
            findViewById(R.id.button5), findViewById(R.id.button6), findViewById(R.id.button7), findViewById(R.id.button8),
            findViewById(R.id.button9), findViewById(R.id.button10), findViewById(R.id.button11), findViewById(R.id.button12),
            findViewById(R.id.button13), findViewById(R.id.button14), findViewById(R.id.button15), findViewById(R.id.button16)
        )

        // Initialize User Input TextView
        userInputTextView = findViewById(R.id.userInputTextView)

        for (button in buttons) {
            button.setOnClickListener {
                if (isValidMove(button)) {
                    val buttonText = button.text
                    if (userInputTextView.text == "User Input") {
                        userInputTextView.text = ""
                    }
                    userInputTextView.text = userInputTextView.text.toString() + buttonText
                    usedButtons.add(button)
                    button.isEnabled = false
                    button.setBackgroundColor(Color.GRAY)
                    lastClickedButton = button
                } else {
                    Toast.makeText(this, "Invalid move. Please select a button to the right of the previous button.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set onClickListener for clear button
        val clearButton = findViewById<Button>(R.id.clear_button)
        clearButton.setOnClickListener {
            userInputTextView.text = "User Input"
            usedButtons.clear()
            for (button in buttons) {
                button.isEnabled = true
                button.setBackgroundColor(Color.WHITE)
            }
            lastClickedButton = null
        }

        initializeButtons()
    }

    private fun initializeButtons() {
        val letters = generateRandomLetters()
        for ((index, button) in buttons.withIndex()) {
            button.text = letters[index].toString()
        }
    }

    private fun generateRandomLetters(): List<Char> {
        val letters = mutableListOf<Char>()
        val alphabet = ('A'..'Z').toList()

        repeat(16) {
            val randomChar = alphabet.random()
            letters.add(randomChar)
        }

        return letters
    }

    private fun isValidMove(button: Button): Boolean {
        if (lastClickedButton == null) return true // Allow first click

        val lastRow = buttons.indexOf(lastClickedButton) / 4
        val lastColumn = buttons.indexOf(lastClickedButton) % 4

        val currentRow = buttons.indexOf(button) / 4
        val currentColumn = buttons.indexOf(button) % 4

        return (currentRow == lastRow - 1 && currentColumn == lastColumn) || // Up
                (currentRow == lastRow + 1 && currentColumn == lastColumn) || // Down
                (currentRow == lastRow && (currentColumn == lastColumn + 1 || currentColumn == lastColumn - 1)) || // Left or right
                (currentRow == lastRow - 1 && (currentColumn == lastColumn + 1 || currentColumn == lastColumn - 1)) || // Up diagonal
                (currentRow == lastRow + 1 && (currentColumn == lastColumn + 1 || currentColumn == lastColumn - 1)) // Down diagonal
    }
}