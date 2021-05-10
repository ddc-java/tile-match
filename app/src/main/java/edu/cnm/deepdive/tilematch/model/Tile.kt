package edu.cnm.deepdive.tilematch.model;

import androidx.annotation.NonNull;

public class Tile {

  private final int imageIndex;
  @NonNull
  private State state;

  public Tile(int imageIndex) {
    this.imageIndex = imageIndex;
    state = State.HIDDEN;
  }

  public int getImageIndex() {
    return imageIndex;
  }

  @NonNull
  public State getState() {
    return state;
  }

  public void setState(@NonNull State state) {
    this.state = state;
  }

  public enum State {
    HIDDEN, SELECTED, SOLVED;
  }

}
