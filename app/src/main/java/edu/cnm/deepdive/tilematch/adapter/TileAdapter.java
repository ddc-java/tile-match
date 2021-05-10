package edu.cnm.deepdive.tilematch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import edu.cnm.deepdive.tilematch.R;
import edu.cnm.deepdive.tilematch.databinding.AdapterTileBinding;
import edu.cnm.deepdive.tilematch.model.Puzzle;
import edu.cnm.deepdive.tilematch.model.Tile;
import edu.cnm.deepdive.tilematch.model.Tile.State;
import edu.cnm.deepdive.tilematch.view.TileImages;
import java.util.List;

public class TileAdapter extends ArrayAdapter<Tile> {

  private final LayoutInflater inflater;

  public TileAdapter(@NonNull Context context, @NonNull Puzzle puzzle) {
    super(context, R.layout.adapter_tile, puzzle.getTiles());
    inflater = LayoutInflater.from(context);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    AdapterTileBinding binding = (convertView != null)
        ? AdapterTileBinding.bind(convertView)
        : AdapterTileBinding.inflate(inflater, parent, false);
    Tile tile = getItem(position);
    if (tile.getState() == State.HIDDEN) {
      binding.number.setText(String.valueOf(position) + 1);
      binding.image.setVisibility(View.GONE);
    } else {
      binding.image.setImageResource(TileImages.RESOURCE_IDS.get(tile.getImageIndex()));
      binding.number.setVisibility(View.GONE);
    }
    return binding.getRoot();
  }

}
