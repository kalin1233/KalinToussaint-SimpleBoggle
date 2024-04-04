package com.bignerdranch.android.simpleboggle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity(), GameFragment.GameFragmentListener, ScoreFragment.ScoreFragmentListener {

    private lateinit var gameFragment: GameFragment
    private lateinit var scoreFragment: ScoreFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameFragment = supportFragmentManager.findFragmentById(R.id.game_fragment) as GameFragment
        scoreFragment = supportFragmentManager.findFragmentById(R.id.score_fragment) as ScoreFragment

        gameFragment.setListener(this)
        scoreFragment.setListener(this)
    }

    override fun onSubmitWord(word: String) {
        // Handle word submission, perform database operations, update score, etc.
        Toast.makeText(this, "Word submitted: $word", Toast.LENGTH_SHORT).show()
    }

    override fun onNewGame() {
        // Handle new game action, reset score, clear previous submissions, generate new letters, etc.
        Toast.makeText(this, "New game started", Toast.LENGTH_SHORT).show()

        // Reset the submitted words set in the GameFragment
        gameFragment.resetSubmittedWords()

        // Initialize new letters in the game board
        gameFragment.initializeButtons()
    }

    override fun onScoreUpdated(score: Int) {
        // Update score in ScoreFragment
        scoreFragment.updateScore(score)
    }
}