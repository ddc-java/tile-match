package edu.cnm.deepdive.tilematch.service;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.tilematch.R;
import edu.cnm.deepdive.tilematch.model.Puzzle;
import edu.cnm.deepdive.tilematch.view.TileImages;
import java.util.Random;

public class PuzzleRepository {

  @NonNull
  private final Context context;
  @NonNull
  private final Random rng;
  @NonNull
  private final SharedPreferences preferences;
  @NonNull
  private final String sizePreferenceKey;
  @NonNull
  private final String sizePreferenceDefault;
  private final int imagePoolSize;


  public PuzzleRepository(@NonNull Context context, @NonNull Random rng) {
    this.context = context;
    this.rng = rng;
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    sizePreferenceKey = context.getString(R.string.size_preference_key);
    sizePreferenceDefault = context.getString(R.string.size_preference_default);
    imagePoolSize = TileImages.RESOURCE_IDS.size();
  }

  public Puzzle createPuzzle() {
    return new Puzzle(Integer.parseInt(
        preferences.getString(sizePreferenceKey, sizePreferenceDefault)), imagePoolSize, rng);
  }

}
