package com.ohadshai.savta.ui.fragments.userRemedies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ohadshai.savta.databinding.FragmentUserRemediesBinding;

public class UserRemediesFragment extends Fragment {

    private UserRemediesViewModel _viewModel;
    private FragmentUserRemediesBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _viewModel = new ViewModelProvider(this).get(UserRemediesViewModel.class);

        _binding = FragmentUserRemediesBinding.inflate(inflater, container, false);
        View rootView = _binding.getRoot();


//        _adapter.setOnItemClickListener(new RemediesListAdapter.OnItemClickListener() {
//            @Override
//            public void onClick(Remedy remedy, View view) {
//                // Navigates to the details fragment of the remedy (with a transition of shared elements):
//                CardView cardContainer = view.findViewById(R.id.item_remedy_card);
//                ImageView imgRemedyPhoto = view.findViewById(R.id.item_remedy_imgPhoto);
//
//                Navigation.findNavController(view).navigate(
//                        UserRemediesFragmentDirections.actionNavUserRemediesToNavRemedyDetails(remedy),
//                        SharedElementsUtil.build(cardContainer, imgRemedyPhoto)
//                );
//            }
//        });


        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}