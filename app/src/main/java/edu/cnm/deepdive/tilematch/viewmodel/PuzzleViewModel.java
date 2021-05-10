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
package edu.cnm.deepdive.tilematch.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.tilematch.R;
import edu.cnm.deepdive.tilematch.model.Puzzle;
import edu.cnm.deepdive.tilematch.model.Puzzle.State;
import edu.cnm.deepdive.tilematch.service.PuzzleRepository;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;

public class PuzzleViewModel extends AndroidViewModel implements LifecycleObserver {

  private static final long TIMER_TICK = 100;

  private final PuzzleRepository puzzleRepository;
  private final OnSharedPreferenceChangeListener preferenceChangeListener;
  private final MutableLiveData<Puzzle> puzzle;
  private final MutableLiveData<Long> revealTime;
  private final MutableLiveData<Long> duration;
  private final MutableLiveData<Integer> moves;
  private final MutableLiveData<Throwable> throwable;

  private String revealTimePreferenceKey;
  private int revealTimePreference;
  private CountDownTimer revealCountdown;
  private Timer durationCountup;

  public PuzzleViewModel(@NonNull Application application) {
    super(application);
    puzzleRepository = new PuzzleRepository(application, new SecureRandom());
    preferenceChangeListener = setupPreferenceListener(application);
    puzzle = new MutableLiveData<>();
    revealTime = new MutableLiveData<>();
    duration = new MutableLiveData<>();
    moves = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    newPuzzle();
  }

  @NonNull
  private OnSharedPreferenceChangeListener setupPreferenceListener(
      @NonNull Application application) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
    revealTimePreferenceKey = application.getString(R.string.reveal_time_preference_key);
    int revealTimePreferenceDefault = application.getResources().getInteger(R.integer.reveal_time_preference_default);
    OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
      if (key.equals(revealTimePreferenceKey)) {
        revealTimePreference =
            sharedPreferences.getInt(revealTimePreferenceKey, revealTimePreferenceDefault);
      }
    };
    preferences.registerOnSharedPreferenceChangeListener(listener);
    listener.onSharedPreferenceChanged(preferences, revealTimePreferenceKey);
    return listener;
  }

  public LiveData<Puzzle> getPuzzle() {
    return puzzle;
  }

  public LiveData<Long> getRevealTime() {
    return revealTime;
  }

  public LiveData<Long> getDuration() {
    return duration;
  }

  public LiveData<Integer> getMoves() {
    return moves;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void newPuzzle() {
    Puzzle puzzle = puzzleRepository.createPuzzle();
    this.puzzle.setValue(puzzle);
  }

  public void select(int position) {
    Puzzle puzzle = this.puzzle.getValue();
    puzzle.select(position);
    State state = puzzle.getState();
    if (state == State.REVEALING_MATCH || state == State.REVEALING_NO_MATCH) {
      revealCountdown = new CountDownTimer(1000L * revealTimePreference, TIMER_TICK) {
        @Override
        public void onTick(long millisUntilFinished) {
          revealTime.postValue(millisUntilFinished);
        }

        @Override
        public void onFinish() {
          puzzle.unreveal();
          revealTime.postValue(null);
          PuzzleViewModel.this.puzzle.postValue(puzzle);
        }
      };
      revealCountdown.start();
    }
    this.puzzle.setValue(puzzle);
  }

  @OnLifecycleEvent(Event.ON_STOP)
  public void stopTimers() {
    Puzzle puzzle = this.puzzle.getValue();
    // Pause timer
  }

}
