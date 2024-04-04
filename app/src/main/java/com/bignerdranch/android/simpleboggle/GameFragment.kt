package com.bignerdranch.android.simpleboggle

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.math.sqrt


class GameFragment : Fragment(), SensorEventListener {

    private lateinit var gameFragmentListener: GameFragmentListener
    private lateinit var buttons: List<Button>
    private val usedButtons = mutableListOf<Button>()
    private var lastClickedButton: Button? = null
    private lateinit var dictionary: Set<String>
    private val submittedWords = mutableSetOf<String>()

    private var score = 0

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private val shakeThreshold = 12
    private val shakeInterval = 1000

    interface GameFragmentListener {
        fun onSubmitWord(word: String)
        fun onNewGame()
        fun onScoreUpdated(score: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize buttons
        buttons = listOf(
            view.findViewById(R.id.button1),
            view.findViewById(R.id.button2),
            view.findViewById(R.id.button3),
            view.findViewById(R.id.button4),
            view.findViewById(R.id.button5),
            view.findViewById(R.id.button6),
            view.findViewById(R.id.button7),
            view.findViewById(R.id.button8),
            view.findViewById(R.id.button9),
            view.findViewById(R.id.button10),
            view.findViewById(R.id.button11),
            view.findViewById(R.id.button12),
            view.findViewById(R.id.button13),
            view.findViewById(R.id.button14),
            view.findViewById(R.id.button15),
            view.findViewById(R.id.button16)
        )

        // Set onClickListener for each button
        for (button in buttons) {
            button.setOnClickListener {
                handleButtonClick(button)
            }
        }

        // Set onClickListener for clear button
        val clearButton = view.findViewById<Button>(R.id.clear_button)
        clearButton.setOnClickListener {
            resetButtonBackgrounds()
            clearUserInput()
        }

        // Set onClickListener for submit button
        val submitButton = view.findViewById<Button>(R.id.submit_button)
        submitButton.setOnClickListener {
            submitUserInput()
        }

        initializeButtons()
        initializeDictionary()

        // Initialize sensor manager and accelerometer sensor
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }



    @OptIn(DelicateCoroutinesApi::class)
    private fun initializeDictionary() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Download dictionary from the URL
                val dictionaryText = downloadDictionary()

                // Parse and store dictionary words
                dictionary = parseDictionary(dictionaryText)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to download dictionary", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun downloadDictionary(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: throw IOException("Failed to download dictionary")
    }

    private fun parseDictionary(dictionaryText: String): Set<String> {
        return dictionaryText.trim().split("\n").toSet()
    }

    private fun isValidWord(word: String): Boolean {
        return word.lowercase() in dictionary
    }

    fun setListener(listener: GameFragmentListener) {
        this.gameFragmentListener = listener
    }

    fun initializeButtons() {
        val letters = generateRandomLetters()
        for ((index, button) in buttons.withIndex()) {
            button.text = letters[index].toString()
        }
    }

    private fun generateRandomLetters(): List<Char> {
        val letters = mutableListOf<Char>()
        val vowels = listOf('A', 'E', 'I', 'O', 'U')
        val alphabet = ('A'..'Z').toList()

        val vowelsToAdd = 4
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

    private fun handleButtonClick(button: Button) {
        if (isValidMove(button)) {
            val buttonText = button.text
            val userInputTextView = view?.findViewById<TextView>(R.id.userInputTextView)
            if (userInputTextView?.text == "User Input") {
                userInputTextView.text = ""
            }
            userInputTextView?.text = userInputTextView?.text.toString() + buttonText
            usedButtons.add(button)
            button.isEnabled = false
            button.setBackgroundColor(Color.GRAY)
            lastClickedButton = button
        } else {
            Toast.makeText(
                requireContext(),
                "Invalid move. Please select a button to the right of the previous button.",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    private fun clearUserInput() {
        val userInputTextView = view?.findViewById<TextView>(R.id.userInputTextView)
        userInputTextView?.text = "User Input"

        // Create a copy of the usedButtons list
        val usedButtonsCopy = ArrayList<Button>(usedButtons)

        // Iterate over the copy of the usedButtons list
        for (button in usedButtonsCopy) {
            button.isEnabled = true
            usedButtons.remove(button) // Remove the button from the original list
            button.setBackgroundColor(R.drawable.small_border)
        }

        lastClickedButton = null
    }

    private fun submitUserInput() {
        if (!::dictionary.isInitialized) {
            // Dictionary is not initialized yet, handle this case gracefully
            showToast("Dictionary is still loading. Please wait.")
            return
        }
        val userInputTextView = view?.findViewById<TextView>(R.id.userInputTextView)
        val word = userInputTextView?.text.toString()

        if (word.isEmpty() || word == "User Input") {
            showToast("Please enter a word.")
            return
        }

        if (word.length < 4) {
            showToast("Word must be at least 4 characters long. Try again!")
            resetButtonBackgrounds()
            return
        }

        if (!isValidWord(word)) {
            showToast("Invalid word. Please enter a valid English word.")
            deductPoints(10)
            resetButtonBackgrounds()
            return
        }

        val vowels = "AEIOU"
        val vowelCount = word.count { vowels.contains(it) }

        if (vowelCount < 2) {
            showToast("Word must contain at least two vowels. Try again!")
            resetButtonBackgrounds()
            return
        }

        if (submittedWords.contains(word)) {
            showToast("Word already submitted. Try again!")
        } else {
            // Add the word to the set of submitted words
            submittedWords.add(word)
            // Calculate word score
            val wordScore = calculateScore(word)
            if (wordScore > 0) {
                showToast("That's correct! +${wordScore}")
                resetButtonBackgrounds()
                clearUserInput()
            } else {
                showToast("That's incorrect. -10")
                resetButtonBackgrounds()
            }
            gameFragmentListener.onSubmitWord(word)
            if (wordScore > 0) {
                addPoints(wordScore) // Add points if the word is correct
            } else {
                deductPoints(10) // Deduct 10 points for incorrect word
            }
            clearUserInput()
            resetButtonBackgrounds()
        }
    }




    private fun resetButtonBackgrounds() {
        for (button in buttons) {
            button.setBackgroundResource(R.drawable.small_border)
        }
    }


    private fun deductPoints(points: Int) {
        score = maxOf(score - points, 0) // Deduct points, but ensure the score doesn't go negative
        gameFragmentListener.onScoreUpdated(score) // Update the score in the ScoreFragment
    }

    private fun addPoints(points: Int) {
        score += points // Add points to the score
        gameFragmentListener.onScoreUpdated(score) // Update the score in the ScoreFragment
    }

    private fun calculateScore(word: String): Int {
        var wordScore = 0
        var consonantCount = 0

        for (char in word) {
            if (char in "AEIOU") {
                wordScore += 5 // Add 5 points for each vowel
            } else {
                wordScore += 1 // Add 1 point for each consonant
                consonantCount++
            }
        }

        if (consonantCount > 0 && word.containsAny("SZPXQ")) {
            wordScore *= 2 // Double the score if the word contains any of 'S', 'Z', 'P', 'X', or 'Q'
        }

        if (wordScore < 0) {
            wordScore = 0 // Ensure non-negative score
        }

        return wordScore
    }

    private fun String.containsAny(chars: CharSequence): Boolean {
        for (char in chars) {
            if (this.contains(char)) {
                return true
            }
        }
        return false
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun resetSubmittedWords() {
        submittedWords.clear()
        score = 0 // Reset score when new game starts
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { accel ->
            sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // Detect shake gesture
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > shakeInterval) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                if (acceleration > shakeThreshold) {
                    startNewGame()
                }

                lastShakeTime = currentTime
            }
        }
    }
    private fun startNewGame() {
        gameFragmentListener.onNewGame()
        resetSubmittedWords()
    }
}