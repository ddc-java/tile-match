/*
 * Copyright 2021 CNM Ingenuity, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.cnm.deepdive.tilematch.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.CountDownTimer
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import edu.cnm.deepdive.tilematch.R
import edu.cnm.deepdive.tilematch.model.Puzzle
import edu.cnm.deepdive.tilematch.service.PuzzleRepository
import java.security.SecureRandom
import java.util.*

class PuzzleViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {

    private val puzzleRepository: PuzzleRepository
    private val preferenceChangeListener: OnSharedPreferenceChangeListener

    private val _puzzle: MutableLiveData<Puzzle>
    val puzzle: LiveData<Puzzle>
        get() {
            return _puzzle
        }

    private val _revealTime: MutableLiveData<Long?>
    val revealTime: LiveData<Long?>
        get() {
            return _revealTime
        }

    private val _duration: MutableLiveData<Long>
    val duration: LiveData<Long>
        get() {
            return _duration
        }

    private val moves: MutableLiveData<Int>
    private val throwable: MutableLiveData<Throwable>

    private lateinit var revealTimePreferenceKey: String
    private var revealTimePreference = 0
    private var revealCountdown: CountDownTimer? = null
    private var durationCountup: Timer? = null
    private var countupStart: Long = 0

    init {
        puzzleRepository = PuzzleRepository(application, SecureRandom())
        preferenceChangeListener = setupPreferenceListener(application)
        _puzzle = MutableLiveData()
        _revealTime = MutableLiveData()
        _duration = MutableLiveData()
        moves = MutableLiveData()
        throwable = MutableLiveData()
        newPuzzle()
    }

    private fun setupPreferenceListener(
        application: Application
    ): OnSharedPreferenceChangeListener {
        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        revealTimePreferenceKey = application.getString(R.string.reveal_time_preference_key)
        val revealTimePreferenceDefault =
            application.resources.getInteger(R.integer.reveal_time_preference_default)
        return OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
            if (key == revealTimePreferenceKey) {
                revealTimePreference = sharedPreferences.getInt(
                    revealTimePreferenceKey,
                    revealTimePreferenceDefault
                )
            }
        }.apply {
            preferences.registerOnSharedPreferenceChangeListener(this)
            onSharedPreferenceChanged(preferences, revealTimePreferenceKey)
        }
    }

    fun getMoves(): LiveData<Int> {
        return moves
    }

    fun getThrowable(): LiveData<Throwable> {
        return throwable
    }

    fun newPuzzle() {
        val puzzle = puzzleRepository.createPuzzle()
        _puzzle.value = puzzle
        countupStart = System.currentTimeMillis()
        durationCountup = Timer().apply {
            schedule(object : TimerTask() {
                override fun run() {
                    _duration.postValue(System.currentTimeMillis() - countupStart)
                }
            }, 200, 200)
        }
    }

    fun select(position: Int) {
        val puzzle = puzzle.value
        puzzle!!.select(position)
        val state = puzzle.state
        if (state === Puzzle.State.REVEALING_NO_MATCH) {
            revealCountdown = object : CountDownTimer(1000L * revealTimePreference, TIMER_TICK) {
                override fun onTick(millisUntilFinished: Long) {
                    _revealTime.postValue(millisUntilFinished)
                }

                override fun onFinish() {
                    puzzle.unreveal()
                    _revealTime.postValue(null)
                    _puzzle.postValue(puzzle)
                }
            }.apply {
                start()
            }
        }
        _puzzle.value = puzzle
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopTimers() {
        val puzzle = puzzle.value
        // Pause timer
    }

    companion object {
        private const val TIMER_TICK: Long = 100
    }

}