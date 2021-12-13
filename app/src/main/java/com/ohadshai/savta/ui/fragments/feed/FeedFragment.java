package com.ohadshai.savta.ui.fragments.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ohadshai.savta.R;
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.databinding.FragmentFeedBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.ui.dialogs.AboutDialog;
import com.ohadshai.savta.utils.SharedElementsUtil;

import java.util.List;

public class FeedFragment extends Fragment {

    private FeedViewModel _viewModel;
    private FragmentFeedBinding _binding;
    private RecyclerView _rvRemediesList;
    private RemediesListAdapter _adapter;
    private List<Remedy> _remedies;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        _binding = FragmentFeedBinding.inflate(inflater, container, false);

        _rvRemediesList = _binding.rvRemediesList;
        _rvRemediesList.setHasFixedSize(true);
        _rvRemediesList.setLayoutManager(new LinearLayoutManager(getContext()));
        _adapter = new RemediesListAdapter(_remedies);
        _adapter.setOnItemClickListener(new RemediesListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Remedy remedy, View view) {
                // Navigates to the details fragment of the remedy (with a transition of shared elements):
                CardView cardContainer = view.findViewById(R.id.item_remedy_card);
                ImageView imgRemedyPhoto = view.findViewById(R.id.item_remedy_imgPhoto);

                Navigation.findNavController(view).navigate(
                        FeedFragmentDirections.actionNavFeedToNavRemedyDetails(remedy),
                        SharedElementsUtil.build(cardContainer, imgRemedyPhoto)
                );
            }
        });
        _rvRemediesList.setAdapter(_adapter);
        this.loadData();

        return _binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            AboutDialog.make((AppCompatActivity) requireActivity()).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    //region Private Methods

    private void loadData() {
        //_progressBar.setVisibility(View.VISIBLE);
        _rvRemediesList.setVisibility(View.GONE);

//        RemediesModelSql.instance.getAll(new OnGetCompleteListener<List<Remedy>>() {
//            @Override
//            public void onComplete(List<Remedy> object) {
//                _remedies = object;
//                _adapter.setRemedies(_remedies);
//
//                // Updates the UI:
//                //_progressBar.setVisibility(View.GONE);
//                if (_remedies.size() < 1) {
//                    _rvRemediesList.setVisibility(View.GONE);
//                    //_btnMoviesNotFound.setVisibility(View.VISIBLE);
//                } else {
//                    //_btnMoviesNotFound.setVisibility(View.GONE);
//                    _rvRemediesList.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    //endregion

}