package com.example.personaleventplannerapp2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EventListFragment extends Fragment {

    private AppDataBase database;
    private EventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = DataBaseProvider.getDatabase(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            // Setup listeners for cards
            adapter = new EventAdapter(new EventAdapter.OnEventActionListener() {
                @Override
                public void onDelete(Event event) {
                    showDeleteConfirmation(event);
                }

                @Override
                public void onEdit(Event event) {
                    // Navigate to Add Form and pass the Event ID
                    Bundle bundle = new Bundle();
                    bundle.putLong("eventId", event.id);
                    androidx.navigation.Navigation.findNavController(requireView())
                            .navigate(R.id.addEventFragment, bundle);
                }
            });

            recyclerView.setAdapter(adapter);

            // Observe DB and update ListAdapter
            database.eventDao().getAllEvents().observe(getViewLifecycleOwner(), events -> {
                adapter.submitList(events);
            });
        }
    }

    private void showDeleteConfirmation(Event event) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event")
                .setMessage("Delete \"" + event.title + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        database.eventDao().delete(event); //
                    }).start();
                })
                .setNegativeButton("No", null)
                .show();
    }
}