package com.bignerdranch.android.simpleboggle

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
    private val usedButtons = mutableListOf<Button>()
    private val originalButtonBackgrounds = mutableMapOf<Button, Drawable?>()
    private lateinit var userInputTextView: TextView
    private val vowels = listOf('A', 'E', 'I', 'O', 'U')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            // Store original background drawable of each button
            originalButtonBackgrounds[button] = button.background

            button.setOnClickListener {
                val buttonText = (it as Button).text
                if (userInputTextView.text == "User Input") {
                    userInputTextView.text = ""
                }
                if (button !in usedButtons) {
                    userInputTextView.text = userInputTextView.text.toString() + buttonText
                    usedButtons.add(button)
                    button.isEnabled = false
                    button.setBackgroundColor(Color.GRAY)
                } else {
                    Toast.makeText(this, "This letter has been used. Pick another letter.", Toast.LENGTH_SHORT).show()
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
                // Restore original background drawable
                button.background = originalButtonBackgrounds[button]
            }
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


        val vowelsToAdd = 3
        val chosenVowels = mutableListOf<Char>()

        repeat(vowelsToAdd) {
            val vowel = (vowels - chosenVowels.toSet()).random()
            chosenVowels.add(vowel)
            letters.add(vowel)
        }


        repeat(16 - vowelsToAdd) {
            val consonants = (alphabet - vowels).toList()
            val consonant = consonants.random()
            letters.add(consonant)
        }

        return letters.shuffled()
    }

    fun onNewGameButtonClick(view: android.view.View) {
        initializeButtons()
    }

}