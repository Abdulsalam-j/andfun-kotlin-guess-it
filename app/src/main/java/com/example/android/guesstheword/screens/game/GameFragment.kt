/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding

    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )

        gameViewModel = ViewModelProvider(this)[GameViewModel::class.java]

        // Set the binding ViewModel object to the gameViewModel object
        binding.gameViewModel = gameViewModel

        // Add the lifecycleOwner to the binding
        binding.lifecycleOwner = this

        gameViewModel.buzzType.observe(viewLifecycleOwner) { currentBuzzType ->
            if (currentBuzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(currentBuzzType.pattern)
                gameViewModel.onBuzzComplete()
            }
        }

        gameViewModel.eventGameFinish.observe(viewLifecycleOwner) { hasFinished ->
            if (hasFinished) {
                gameFinished()
                gameViewModel.onGameFinishComplete()
            }
        }

        return binding.root
    }

    /**
     * Given a pattern, this method makes sure the device buzzes
     */
    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        val action = GameFragmentDirections.actionGameToScore(gameViewModel.score.value ?: 0)
        findNavController().navigate(action)
    }
}
