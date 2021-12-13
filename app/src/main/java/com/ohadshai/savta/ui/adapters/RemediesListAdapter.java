package com.ohadshai.savta.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ohadshai.savta.R;
import com.ohadshai.savta.entities.Remedy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Represents a recycler view adapter for list of remedies.
 */
public class RemediesListAdapter extends RecyclerView.Adapter<RemediesListAdapter.ViewHolder> {
    private List<Remedy> _remedies;
    private OnItemClickListener _onItemClickListener;

    public RemediesListAdapter(List<Remedy> remedies) {
        _remedies = remedies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedy, parent, false);
        return new ViewHolder(view, _onItemClickListener);
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

    /**
     * Sets a listener for when an item in the list is clicked on.
     *
     * @param listener The listener to set.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        _onItemClickListener = listener;
    }

    /**
     * Sets the list of remedies to the adapter.
     *
     * @param remedies The list of remedies to set.
     */
    public void setRemedies(List<Remedy> remedies) {
        this._remedies = remedies;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final OnItemClickListener _onItemClickListener;
        private final CardView _card;
        private final ImageView _imgPhoto;
        private final TextView _lblName;
        private final TextView _lblProblem;

        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            _onItemClickListener = onItemClickListener;
            _card = itemView.findViewById(R.id.item_remedy_card);
            _imgPhoto = itemView.findViewById(R.id.item_remedy_imgPhoto);
            _lblName = itemView.findViewById(R.id.item_remedy_lblName);
            _lblProblem = itemView.findViewById(R.id.item_remedy_lblProblem);
        }

        void bind(Remedy remedy) {
            ViewCompat.setTransitionName(_card, ("remedy_container_" + remedy.getId()));
            _card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (_onItemClickListener != null) {
                        _onItemClickListener.onClick(remedy, itemView);
                    }
                }
            });
            ViewCompat.setTransitionName(_imgPhoto, ("remedy_image_" + remedy.getId()));
            Picasso.get()
                    .load(remedy.getImageUrl())
                    .placeholder(R.drawable.remedy_default_image)
                    .into(_imgPhoto);
            _lblName.setText(remedy.getName());
            _lblProblem.setText(remedy.getProblemDescription());
        }
    }

    /**
     * Represents a listener for when an item in the list is clicked on.
     */
    public interface OnItemClickListener {
        void onClick(Remedy remedy, View view);
    }

}