package com.bignerdranch.android.simpleboggle

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var buttons: List<Button>
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

        // Add at least three vowels
        val vowelsToAdd = 3
        val chosenVowels = mutableListOf<Char>()

        repeat(vowelsToAdd) {
            val vowel = (vowels - chosenVowels.toSet()).random()
            chosenVowels.add(vowel)
            letters.add(vowel)
        }

        // Fill the rest of the buttons with random consonants
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

    // Implement other button click handlers as needed
}