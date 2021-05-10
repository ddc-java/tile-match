package edu.cnm.deepdive.tilematch.view;

import edu.cnm.deepdive.tilematch.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TileImages {

  public static List<Integer> RESOURCE_IDS;

  static {
    RESOURCE_IDS = Collections.unmodifiableList(
        (List<? extends Integer>) IntStream.of(
            R.drawable.ic_baby_carriage,
            R.drawable.ic_bicycle,
            R.drawable.ic_camera,
            R.drawable.ic_coffee,
            R.drawable.ic_eye,
            R.drawable.ic_fingerprint,
            R.drawable.ic_flatware,
            R.drawable.ic_flight,
            R.drawable.ic_flower,
            R.drawable.ic_gas_pump,
            R.drawable.ic_hand,
            R.drawable.ic_key,
            R.drawable.ic_liquor,
            R.drawable.ic_lock,
            R.drawable.ic_microphone,
            R.drawable.ic_open_lock,
            R.drawable.ic_phone,
            R.drawable.ic_sailboat,
            R.drawable.ic_search,
            R.drawable.ic_skiing,
            R.drawable.ic_snowflake,
            R.drawable.ic_theater_masks,
            R.drawable.ic_trash,
            R.drawable.ic_volume
        )
        .boxed()
        .collect(Collectors.toCollection(ArrayList::new))
    );
  }

}
