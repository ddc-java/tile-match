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
package edu.cnm.deepdive.tilematch.controller;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import edu.cnm.deepdive.tilematch.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  private String revealTimeSummaryPattern;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupChangeListeners();
  }

  private void setupChangeListeners() {
    String revealTimeKey = getString(R.string.reveal_time_preference_key);
    int revealTimeDefault = getResources().getInteger(R.integer.reveal_time_preference_default);
    SeekBarPreference preference = findPreference(revealTimeKey);
    revealTimeSummaryPattern = getString(R.string.reveal_time_preference_summary_pattern);
    //noinspection ConstantConditions
    preference.setOnPreferenceChangeListener((pref, value) ->
        updateRevealTimeSummary(preference, (Integer) value));
    preference.setUpdatesContinuously(true);
    updateRevealTimeSummary(preference,
        getPreferenceManager()
            .getSharedPreferences()
            .getInt(revealTimeKey, revealTimeDefault));
  }

  private boolean updateRevealTimeSummary(SeekBarPreference preference, Integer value) {
    preference.setSummary(String.format(revealTimeSummaryPattern, value));
    return true;
  }

}
