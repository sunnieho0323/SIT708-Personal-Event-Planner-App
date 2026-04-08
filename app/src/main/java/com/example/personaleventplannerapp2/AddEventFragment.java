package com.example.personaleventplannerapp2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment for adding or editing an event.
 * Implements CRUD operations and input validation as required by Task 4.1.
 */
public class AddEventFragment extends Fragment {

    private AppDataBase database;
    private Calendar selectedCalendar = null;

    // Stores the ID of the event being edited. Default is -1 for new events.
    private long editingEventId = -1;

    // Formatters for displaying date and time on buttons.
    private final SimpleDateFormat dateDisplay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat timeDisplay = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the UI layout from XML.
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Room database instance.
        database = DataBaseProvider.getDatabase(requireContext());

        // UI Element Binding.
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        TextInputEditText etTitle = view.findViewById(R.id.etTitle);
        TextInputEditText etLocation = view.findViewById(R.id.etLocation);
        MaterialButton btnDate = view.findViewById(R.id.btnSelectDate);
        MaterialButton btnTime = view.findViewById(R.id.btnSelectTime);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveEvent);

        // Populate the Spinner with predefined event categories.
        String[] categories = {"Work", "Social", "Travel", "Study", "Health", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // --- EDIT MODE LOGIC ---
        // If an eventId is passed via arguments, retrieve data to populate the fields.
        if (getArguments() != null && getArguments().containsKey("eventId")) {
            editingEventId = getArguments().getLong("eventId");

            new Thread(() -> {
                // Fetch event details on a background thread to avoid UI lag.
                Event event = database.eventDao().getEventById(editingEventId);
                if (event != null) {
                    requireActivity().runOnUiThread(() -> {
                        etTitle.setText(event.title);
                        etLocation.setText(event.location);
                        btnSave.setText("Update");

                        // Split combined dateTime string for button display.
                        String[] dateTimeParts = event.dateTime.split(" ");
                        if (dateTimeParts.length == 2) {
                            btnDate.setText(dateTimeParts[0]);
                            btnTime.setText(dateTimeParts[1]);
                        }

                        // Sync spinner selection with the saved category.
                        for (int i = 0; i < categories.length; i++) {
                            if (categories[i].equals(event.category)) {
                                spinnerCategory.setSelection(i);
                                break;
                            }
                        }
                    });
                }
            }).start();
        }

        // --- DATE PICKER LOGIC ---
        btnDate.setOnClickListener(v -> {
            Calendar cal = (selectedCalendar != null) ? selectedCalendar : Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    android.R.style.Theme_DeviceDefault_Dialog_Alert, (picker, y, m, d) -> {
                if (selectedCalendar == null) selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(y, m, d);
                // Display the selected date on the button.
                btnDate.setText(dateDisplay.format(selectedCalendar.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

            // Logic Validation: Prevent picking past dates.
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        // --- TIME PICKER LOGIC ---
        btnTime.setOnClickListener(v -> {
            Calendar cal = (selectedCalendar != null) ? selectedCalendar : Calendar.getInstance();
            new TimePickerDialog(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_Alert, (picker, h, min) -> {
                if (selectedCalendar == null) selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(Calendar.HOUR_OF_DAY, h);
                selectedCalendar.set(Calendar.MINUTE, min);
                // Display the selected time on the button.
                btnTime.setText(timeDisplay.format(selectedCalendar.getTime()));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
        });

        // --- SAVE BUTTON LOGIC (Create & Update) ---
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();
            String location = etLocation.getText().toString().trim();

            // Mandatory Validation: Ensure title is not empty.
            if (title.isEmpty()) {
                etTitle.setError("Title is required");
                return;
            }

            // Logic Validation: Double check that new events are not set in the past.
            if (editingEventId == -1 && selectedCalendar != null) {
                if (selectedCalendar.getTimeInMillis() < System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "Cannot pick a date in the past", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            new Thread(() -> {
                String finalDateTime;
                if (selectedCalendar != null) {
                    // Format full date-time string for database storage.
                    finalDateTime = Event.formatter.format(selectedCalendar.getTime());
                } else if (editingEventId != -1) {
                    // Fallback to existing text if date/time wasn't changed during edit.
                    finalDateTime = btnDate.getText().toString() + " " + btnTime.getText().toString();
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Please pick Date and Time", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (editingEventId == -1) {
                    // Create Operation: Insert new record.
                    database.eventDao().insert(new Event(title, category, location, finalDateTime));
                } else {
                    // Update Operation: Save changes to existing record.
                    Event updatedEvent = new Event(title, category, location, finalDateTime);
                    updatedEvent.id = editingEventId;
                    database.eventDao().update(updatedEvent);
                }

                requireActivity().runOnUiThread(() -> {
                    // Provide feedback via Toast.
                    Toast.makeText(requireContext(), "Event Saved Successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate back to the list screen.
                    androidx.navigation.Navigation.findNavController(view).popBackStack();
                });
            }).start();
        });
    }
}