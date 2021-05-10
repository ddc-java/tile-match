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
package edu.cnm.deepdive.tilematch.controller

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.cnm.deepdive.tilematch.R
import edu.cnm.deepdive.tilematch.adapter.TileAdapter
import edu.cnm.deepdive.tilematch.databinding.FragmentPuzzleBinding
import edu.cnm.deepdive.tilematch.viewmodel.PuzzleViewModel
import kotlin.math.round

class PuzzleFragment : Fragment() {

    private lateinit var binding: FragmentPuzzleBinding
    private lateinit var viewModel: PuzzleViewModel
    private lateinit var timer: TextView
    private lateinit var hmsFormat: String
    private lateinit var msFormat: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        hmsFormat = getString(R.string.hms_format)
        msFormat = getString(R.string.ms_format)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        binding.board.onItemClickListener =
            OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                viewModel.select(position)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity!!
        viewModel = ViewModelProvider(activity)
            .get(PuzzleViewModel::class.java)
            .apply {
                puzzle.observe(viewLifecycleOwner) {
                    val columns =
                        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                            round(Math.sqrt((9 + 4 * it.size).toDouble()) + 3).toInt() / 2
                        else
                            round(Math.sqrt((9 + 4 * it.size).toDouble()) - 3).toInt() / 2
                    val adapter = TileAdapter(activity, it)
                    binding.board.numColumns = columns
                    binding.board.adapter = adapter
                }
                duration.observe(viewLifecycleOwner) {
                    var seconds = round(it.toDouble() / MS_PER_SECOND).toInt()
                    var minutes = seconds / SECONDS_PER_MINUTE
                    seconds %= SECONDS_PER_MINUTE
                    val hours = minutes / MINUTES_PER_HOUR
                    minutes %= MINUTES_PER_HOUR
                    val elapsedTime: String = if (hours > 0)
                        String.format(hmsFormat, hours, minutes, seconds)
                    else
                        String.format(msFormat, minutes, seconds)
                    timer.text = elapsedTime
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.puzzle_options, menu)
        timer = menu.findItem(R.id.timer).actionView as TextView
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var handled = true
        val id = item.itemId
        if (id == R.id.new_puzzle) {
            viewModel.newPuzzle()
        } else {
            handled = super.onOptionsItemSelected(item)
        }
        return handled
    }

    companion object {
        const val SECONDS_PER_MINUTE = 60
        const val MINUTES_PER_HOUR = 60
        const val MS_PER_SECOND = 1000
    }
}