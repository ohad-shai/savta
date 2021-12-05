package com.ohadshai.savta.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ohadshai.savta.R;
import com.ohadshai.savta.entities.Remedy;

import java.util.List;

/**
 * Represents a recycler view adapter for list of remedies.
 */
public class RemediesListAdapter extends RecyclerView.Adapter<RemediesListAdapter.ViewHolder> {
    private final List<Remedy> _remedies;

    public RemediesListAdapter(List<Remedy> remedies) {
        _remedies = remedies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedy, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Remedy remedy = _remedies.get(position);
        holder.bind(remedy);
    }

    @Override
    public int getItemCount() {
        return _remedies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView _card;
        private final ImageView _imgPhoto;
        private final TextView _lblName;
        private final TextView _lblProblem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _card = itemView.findViewById(R.id.item_remedy_card);
            _imgPhoto = itemView.findViewById(R.id.item_remedy_imgPhoto);
            _lblName = itemView.findViewById(R.id.item_remedy_lblName);
            _lblProblem = itemView.findViewById(R.id.item_remedy_lblProblem);
        }

        void bind(Remedy remedy) {
            _card.setOnClickListener(v -> {
                // Navigates to the details fragment of the remedy:

            });
            //_imgPhoto.setImageResource(R.drawable.student_photo);
            _lblName.setText(remedy.getName());
            _lblProblem.setText(remedy.getProblemDescription());
        }
    }

}
