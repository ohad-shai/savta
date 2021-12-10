package com.ohadshai.savta.ui.fragments.userRemedies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ohadshai.savta.R;
import com.ohadshai.savta.data.RemediesModel;
import com.ohadshai.savta.databinding.FragmentUserRemediesBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.utils.SharedElementsUtil;

import java.util.List;

public class UserRemediesFragment extends Fragment {

    private UserRemediesViewModel _viewModel;
    private FragmentUserRemediesBinding _binding;
    private RemediesListAdapter _adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(UserRemediesViewModel.class);

        _binding = FragmentUserRemediesBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();

        List<Remedy> remedies = RemediesModel.instance.getAll();
        RecyclerView rvRemediesList = _binding.rvRemediesList;
        rvRemediesList.setHasFixedSize(true);
        rvRemediesList.setLayoutManager(new LinearLayoutManager(getContext()));
        _adapter = new RemediesListAdapter(remedies);
        _adapter.setOnItemClickListener(new RemediesListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Remedy remedy, View view) {
                // Navigates to the details fragment of the remedy (with a transition of shared elements):
                CardView cardContainer = view.findViewById(R.id.item_remedy_card);
                ImageView imgRemedyPhoto = view.findViewById(R.id.item_remedy_imgPhoto);

                Navigation.findNavController(view).navigate(
                        R.id.action_nav_user_remedies_to_nav_remedy_details,
                        Remedy.toBundle(remedy),
                        null,
                        SharedElementsUtil.build(cardContainer, imgRemedyPhoto)
                );
            }
        });
        rvRemediesList.setAdapter(_adapter);

        return rootView;
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

}