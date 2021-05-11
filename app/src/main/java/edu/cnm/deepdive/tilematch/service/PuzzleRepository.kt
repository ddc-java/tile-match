package edu.cnm.deepdive.tilematch.service

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import edu.cnm.deepdive.tilematch.model.Puzzle
import edu.cnm.deepdive.tilematch.R
import edu.cnm.deepdive.tilematch.view.TileImages
import java.util.*

class PuzzleRepository(private val context: Context, private val rng: Random) {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val sizePreferenceKey: String = context.getString(R.string.size_preference_key)
    private val sizePreferenceDefault: String = context.getString(R.string.size_preference_default)
    private val imagePoolSize: Int = TileImages.RESOURCE_IDS.size

    fun createPuzzle(): Puzzle {
        return Puzzle(
            preferences.getString(sizePreferenceKey, sizePreferenceDefault)?.toInt()
                ?: context.resources.getInteger(R.integer.size_preference_fallback),
            imagePoolSize,
            rng
        )
    }

}