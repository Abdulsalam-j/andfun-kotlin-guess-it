package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

// Buzz patterns
private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {

    // These are the three different types of buzzing in the game. Buzz pattern is the number of
    // milliseconds each interval of buzzing and non-buzzing takes.
    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    //companion object representing some static constants
    companion object {
        // These represent different important times

        // This is when the game is over
        private const val DONE = 0L

        // This is the number of milliseconds in a second
        private const val ONE_SECOND = 1000L

        // This is the total time of the game
        private const val COUNTDOWN_TIME = 60000L

        // This is the time when the phone will start buzzing each second
        private const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    // The count down timer
    private val timer: CountDownTimer

    // The current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String> get() = _word

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    // The current game finished state
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean> get() = _eventGameFinish

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    // The current time in milli seconds
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long> get() = _currentTime

    // The current time as a formatted string
    val currentTimeString = Transformations.map(currentTime) { currentTime ->
        DateUtils.formatElapsedTime(currentTime)
    }

    // Event that triggers the phone to buzz using different patterns, determined by BuzzType
    private val _buzzEvent = MutableLiveData<BuzzType>()
    val buzzType: LiveData<BuzzType> get() = _buzzEvent

    init {
        resetList()
        nextWord()
        _score.value = 0
        _buzzEvent.value = BuzzType.NO_BUZZ

        // Creates a timer that triggers the end of the game when it's finished
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _buzzEvent.value = BuzzType.COUNTDOWN_PANIC
                }
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _buzzEvent.value = BuzzType.GAME_OVER
                _eventGameFinish.postValue(true)
            }
        }

        // Start the timer
        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /** Methods for buttons presses **/

    fun onSkip() {
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = score.value?.plus(1)
        _buzzEvent.value = BuzzType.CORRECT
        nextWord()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)
    }

    fun onGameFinishComplete() {
        _eventGameFinish.postValue(false)
    }

    fun onBuzzComplete() {
        _buzzEvent.value = BuzzType.NO_BUZZ
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}