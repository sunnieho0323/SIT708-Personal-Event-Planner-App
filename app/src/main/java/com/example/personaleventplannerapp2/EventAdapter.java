package com.example.personaleventplannerapp2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class EventAdapter extends ListAdapter<Event, EventAdapter.EventViewHolder> {

    // Define actions for Fragment to handle
    public interface OnEventActionListener {
        void onDelete(Event event);
        void onEdit(Event event);
    }

    private final OnEventActionListener actionListener;

    public EventAdapter(OnEventActionListener listener) {
        super(new DiffUtil.ItemCallback<Event>() {
            @Override
            public boolean areItemsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                return oldItem.id == newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Event oldItem, @NonNull Event newItem) {
                return oldItem.title.equals(newItem.title) &&
                        oldItem.dateTime.equals(newItem.dateTime);
            }
        });
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the custom material card layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = getItem(position);

        // Map data to UI
        holder.tvTitle.setText(event.title);
        holder.tvCategory.setText(event.category);
        holder.tvLocation.setText(event.location);
        holder.tvDateTime.setText(event.dateTime);

        // Button Click Listeners
        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(event));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(event));
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvLocation, tvDateTime;
        ImageButton btnEdit, btnDelete;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Link to IDs in item_event.xml
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}