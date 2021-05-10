package edu.cnm.deepdive.tilematch.adapter

import android.content.Context
import edu.cnm.deepdive.tilematch.model.Puzzle
import android.widget.ArrayAdapter
import edu.cnm.deepdive.tilematch.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.cnm.deepdive.tilematch.databinding.AdapterTileBinding
import edu.cnm.deepdive.tilematch.model.Tile
import edu.cnm.deepdive.tilematch.view.TileImages

class TileAdapter(context: Context, puzzle: Puzzle) :
    ArrayAdapter<Tile?>(context, R.layout.adapter_tile, puzzle.tiles) {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        (if (convertView != null)
            AdapterTileBinding.bind(convertView)
        else
            AdapterTileBinding.inflate(inflater, parent, false)).apply {
            val tile = getItem(position)!!
            number.text = (position + 1).toString()
            image.setImageResource(TileImages.RESOURCE_IDS[tile.imageIndex])
            when (tile.state) {
                Tile.State.HIDDEN -> {
                    number.visibility = View.VISIBLE
                    image.visibility = View.GONE
                }
                Tile.State.SELECTED -> {
                    number.visibility = View.GONE
                    image.visibility = View.VISIBLE
                }
                Tile.State.SOLVED -> {
                    number.visibility = View.GONE
                    image.visibility = View.VISIBLE
                    image.alpha = 0.25f
                }
            }
            return this.root;
        }
    }

}