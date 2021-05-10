package edu.cnm.deepdive.tilematch.model

class Tile(val imageIndex: Int) {

    var state: State

    init {
        state = State.HIDDEN
    }

    enum class State {
        HIDDEN, SELECTED, SOLVED
    }

}