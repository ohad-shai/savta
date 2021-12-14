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
import com.ohadshai.savta.data.utils.OnGetCompleteListener;
import com.ohadshai.savta.data.sql.RemediesModelSql;
import com.ohadshai.savta.databinding.FragmentUserRemediesBinding;
import com.ohadshai.savta.entities.Remedy;
import com.ohadshai.savta.ui.adapters.RemediesListAdapter;
import com.ohadshai.savta.utils.SharedElementsUtil;

import java.util.List;

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