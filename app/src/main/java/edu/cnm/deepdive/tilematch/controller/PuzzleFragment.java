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
package edu.cnm.deepdive.tilematch.controller;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.tilematch.R;
import edu.cnm.deepdive.tilematch.adapter.TileAdapter;
import edu.cnm.deepdive.tilematch.databinding.FragmentPuzzleBinding;
import edu.cnm.deepdive.tilematch.viewmodel.PuzzleViewModel;
import java.util.List;

public class PuzzleFragment extends Fragment {

  private FragmentPuzzleBinding binding;
  private PuzzleViewModel viewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentPuzzleBinding.inflate(inflater, container, false);
    binding.board.setOnItemClickListener(
        (parent, view, position, id) -> viewModel.select(position));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    FragmentActivity activity = getActivity();
    //noinspection ConstantConditions
    viewModel = new ViewModelProvider(activity).get(PuzzleViewModel.class);
    viewModel.getPuzzle().observe(getViewLifecycleOwner(), (puzzle) -> {
      int columns =
          (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
              ? (int) Math.round(Math.sqrt(9 + 4 * puzzle.getSize()) + 3) / 2
              : (int) Math.round(Math.sqrt(9 + 4 * puzzle.getSize()) - 3) / 2;
      TileAdapter adapter = new TileAdapter(activity, puzzle);
      binding.board.setNumColumns(columns);
      binding.board.setAdapter(adapter);
      List<Integer> selections = puzzle.getSelections();
      if (selections.size() > 0) {
//        binding.board.setSelection(selections.get(selections.size() - 1));
      }
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    inflater.inflate(R.menu.puzzle_options, menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled = true;
    int id = item.getItemId();
    if (id == R.id.new_puzzle) {
      viewModel.newPuzzle();
    } else {
      handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }

}
