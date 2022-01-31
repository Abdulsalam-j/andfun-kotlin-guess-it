package com.example.android.guesstheword.screens.score

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int) : ViewModel() {

    // The current score
    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> get() = _score

    // The current eventPlayAgain
    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean> get() = _eventPlayAgain

    init {
        _score.value = finalScore
    }

    fun onPlayAgain() {
        _eventPlayAgain.postValue(true)
    }

    fun onEventPlayAgainComplete() {
        _eventPlayAgain.postValue(false)
    }

}