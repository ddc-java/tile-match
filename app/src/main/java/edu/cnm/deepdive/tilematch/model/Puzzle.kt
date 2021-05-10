package edu.cnm.deepdive.tilematch.model;

import androidx.annotation.NonNull;
import edu.cnm.deepdive.tilematch.model.Tile.State;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Puzzle {

  private final int size;
  @NonNull
  private final List<Tile> tiles;
  @NonNull
  private final List<Integer> selections;

  @NonNull
  private State state;
  private int matches;
  private int moves;
  private Tile selection;

  public Puzzle(int puzzleSize, int imagePoolSize, Random rng) {
    size = puzzleSize;
    List<Integer> pool = IntStream
        .range(0, imagePoolSize)
        .boxed()
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(pool, rng);
    List<Tile> rawTiles = pool
        .stream()
        .limit(puzzleSize / 2)
        .flatMap((v) -> Stream.of(new Tile(v), new Tile(v)))
        .collect(Collectors.toCollection(ArrayList::new));
    Collections.shuffle(rawTiles);
    tiles = Collections.unmodifiableList(rawTiles);
    selections = new LinkedList<>();
    state = State.IN_PROGRESS;
  }

  public void select(int position) {
    Tile tile = tiles.get(position);
    selections.add(position);
    if (tile.getState() == Tile.State.HIDDEN) {
      switch (state) {
        case IN_PROGRESS:
          state = State.GUESSING;
          tile.setState(Tile.State.SELECTED);
          selection = tile;
          break;
        case GUESSING:
          moves++;
          if (tile.getImageIndex() == selection.getImageIndex()) {
            matches++;
            state = State.REVEALING_MATCH;
            selection.setState(Tile.State.SOLVED);
            tile.setState(Tile.State.SOLVED);
          } else {
            state = State.REVEALING_NO_MATCH;
            tile.setState(Tile.State.SELECTED);
          }
          selection = null;
          break;
      }
    }
  }

  public void unreveal() {
    state = (matches >= size / 2) ? State.COMPLETED : State.IN_PROGRESS;
    tiles.forEach((tile) -> {
      if (tile.getState() == Tile.State.SELECTED) {
        tile.setState(Tile.State.HIDDEN);
      }
    });
  }

  public int getSize() {
    return size;
  }

  @NonNull
  public List<Tile> getTiles() {
    return tiles;
  }

  @NonNull
  public List<Integer> getSelections() {
    return selections;
  }

  @NonNull
  public State getState() {
    return state;
  }

  public int getMoves() {
    return moves;
  }

  public enum State {
    IN_PROGRESS, GUESSING, REVEALING_NO_MATCH, REVEALING_MATCH, COMPLETED;
  }

}
