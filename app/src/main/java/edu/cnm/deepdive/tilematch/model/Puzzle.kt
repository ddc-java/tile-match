package edu.cnm.deepdive.tilematch.model

import java.util.*
import java.util.function.Consumer

class Puzzle(val size: Int, imagePoolSize: Int, rng: Random) {

    val tiles: List<Tile>

    var state: State
        private set

    var moves = 0
        private set

    private var selection: Tile? = null
    private var matches = 0

    private val _selections: MutableList<Int>
    val selections: List<Int>
        get() {
            return Collections.unmodifiableList(_selections);
        }

    init {
        val pool = MutableList<Int>(imagePoolSize) { it }
        pool.shuffle(rng)
        val rawTiles: MutableList<Tile> = pool
            .subList(0, size / 2)
            .flatMap { listOf(Tile(it), Tile(it)) } as MutableList<Tile>
        rawTiles.shuffle(rng)
        tiles = Collections.unmodifiableList(rawTiles)
        _selections = LinkedList()
        state = State.IN_PROGRESS
    }

    fun select(position: Int) {
        val tile = tiles[position]
        _selections.add(position)
        if (tile.state === Tile.State.HIDDEN) {
            when (state) {
                State.IN_PROGRESS -> {
                    state = State.GUESSING
                    tile.state = Tile.State.SELECTED
                    selection = tile
                }
                State.GUESSING -> {
                    moves++
                    state = State.REVEALING_NO_MATCH
                    tile.state = Tile.State.SELECTED
                    selection?.let {
                        if (tile.imageIndex == it.imageIndex) {
                            matches++
                            state = if (matches >= size / 2)
                                State.COMPLETED
                            else
                                State.IN_PROGRESS
                            it.state = Tile.State.SOLVED
                            tile.state = Tile.State.SOLVED
                        }
                    }
                    selection = null
                }
                else -> {
                }
            }
        }
    }

    fun unreveal() {
        if (state == State.REVEALING_NO_MATCH) {
            tiles
                .filter {
                    it.state === Tile.State.SELECTED
                }
                .forEach(Consumer {
                    it.state = Tile.State.HIDDEN
                })
            state = State.IN_PROGRESS
        }
    }

    enum class State {
        IN_PROGRESS, GUESSING, REVEALING_NO_MATCH, COMPLETED
    }

}