//package com.example.shiftsfinalproj;
//
//import static android.content.ContentValues.TAG;
//
//import android.Manifest;
//import android.app.AlertDialog;
//import android.app.DatePickerDialog;
//import android.app.TimePickerDialog;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.database.Cursor;
//import android.icu.text.SimpleDateFormat;
//import android.icu.util.Calendar;
//import android.net.ParseException;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.CalendarContract;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.TimeZone;
//
//public class ShiftsFragment extends Fragment {
//    private EditText editTextRole;
//    private TextView textViewStartDate, textViewEndDate, textViewStartTime, textViewEndTime;
//    private TextView textViewShiftsDisplay;
//    private FirebaseFirestore firestore;
//    private FirebaseUser user;
//    private static final int PERMISSION_REQUEST_CODE = 1;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
//
//        firestore = FirebaseFirestore.getInstance();
//        user = FirebaseAuth.getInstance().getCurrentUser();
//
//
//        editTextRole = view.findViewById(R.id.editTextRole);
//        textViewStartDate = view.findViewById(R.id.textViewStartDate);
//        textViewStartTime = view.findViewById(R.id.textViewStartTime);
//        textViewEndTime = view.findViewById(R.id.textViewEndTime);
//        textViewEndDate = view.findViewById(R.id.textViewEndDate);
//        //textViewShiftsDisplay = view.findViewById(R.id.textViewShiftsDisplay);
//
//        Button buttonSelectStartDate = view.findViewById(R.id.buttonSelectStartDate);
//        buttonSelectStartDate.setOnClickListener(v -> showDatePickerDialog(true));
//
//        Button buttonSelectStartTime = view.findViewById(R.id.buttonSelectStartTime);
//        buttonSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));
//
//        Button buttonSelectEndTime = view.findViewById(R.id.buttonSelectEndTime);
//        buttonSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));
//
//        Button buttonSelectEndDate = view.findViewById(R.id.buttonSelectEndDate);
//        buttonSelectEndDate.setOnClickListener(v -> showDatePickerDialog(false));
//
//        Button buttonAddShift = view.findViewById(R.id.buttonAddShift);
//        Button buttonAddAllShiftsToCalendar = view.findViewById(R.id.buttonAddAllShiftsToCalendar);
//
//        loadUserShifts();
//        buttonAddShift.setOnClickListener(v -> addShift());
//        buttonAddAllShiftsToCalendar.setOnClickListener(v -> addAllShiftsToCalendar());
//
//
//        return view;
//    }
//
//    private void addShift() {
//        String startDate = textViewStartDate.getText().toString().replace("Start Date: ", "").trim();
//        String endDate = textViewEndDate.getText().toString().replace("End Date: ", "").trim();
//        String startTime = textViewStartTime.getText().toString().replace("Start Time: ", "").trim();
//        String endTime = textViewEndTime.getText().toString().replace("End Time: ", "").trim();
//        String role = editTextRole.getText().toString().trim();
//
//        if (startTime.isEmpty() || endTime.isEmpty() || startDate.isEmpty() || role.isEmpty()) {
//            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (user == null) {
//            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        long startTimestamp = combineDateAndTime(startDate, startTime);
//        long endTimestamp = combineDateAndTime(endDate, endTime);
//
//        Map<String, Object> shift = new HashMap<>();
//        shift.put("startTimestamp", startTimestamp);
//        shift.put("endTimestamp", endTimestamp);
//        shift.put("role", role);
//        shift.put("user_id", user.getUid());
//
//        firestore.collection("shifts")
//                .add(shift)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getContext(), "Shift added successfully", Toast.LENGTH_SHORT).show();
//                    loadUserShifts();  // Reload shifts after adding
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add shift: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//
//    private long combineDateAndTime(String date, String time) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//        try {
//            Date parsedDate = dateFormat.parse(date + " " + time);
//            if (parsedDate != null) {
//                return parsedDate.getTime();
//            } else {
//                Log.e(TAG, "Parsed date is null for date: " + date + " and time: " + time);
//                return 0;
//            }
//        } catch (ParseException e) {
//            Log.e(TAG, "Failed to parse date or time for date: " + date + " and time: " + time, e);
//            return 0;
//        } catch (java.text.ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void loadUserShifts() {
//        if (user == null) {
//            textViewShiftsDisplay.setText("No user logged in.");
//            return;
//        }
//
//        firestore.collection("shifts")
//                .whereEqualTo("user_id", user.getUid())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        StringBuilder shiftsBuilder = new StringBuilder();
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Map<String, Object> shift = document.getData();
//                            Long startTimestamp = (Long) shift.get("startTimestamp");
//                            Long endTimestamp = (Long) shift.get("endTimestamp");
//
//                            // Check if timestamps are not null
//                            String startDate = startTimestamp != null ? dateFormat.format(new Date(startTimestamp)) : "Unknown";
//                            String endDate = endTimestamp != null ? dateFormat.format(new Date(endTimestamp)) : "Unknown";
//
//                            shiftsBuilder.append("Start Time: ").append(startDate)
//                                    .append("\nEnd Time: ").append(endDate)
//                                    .append("\nRole: ").append(shift.get("role"))
//                                    .append("\n\n");
//                        }
//                        textViewShiftsDisplay.setText(shiftsBuilder.toString());
//                    } else {
//                        Log.e(TAG, "Error getting documents: ", task.getException());
//                    }
//                });
//    }
//
//    private void addAllShiftsToCalendar() {
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            Log.e(TAG, "Calendar write permission not granted");
//            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
//            return;
//        }
//
//        if (user == null) {
//            Log.e(TAG, "User is null");
//            return;
//        }
//        showCalendarSelection();
//        firestore.collection("shifts")
//                .whereEqualTo("user_id", user.getUid())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null) {
//                        Log.d(TAG, "Successfully retrieved shifts");
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Shift shift = document.toObject(Shift.class);
//                            if (shift != null) {
//                                // Directly use the start and end timestamps from the Shift object.
//                                String role = shift.getRole() != null ? shift.getRole() : "No Role";
//                                long startTimestamp = shift.getStartTimestamp();
//                                long endTimestamp = shift.getEndTimestamp();
//
//                                Log.d(TAG, "Role: " + role + ", Start Time: " + startTimestamp + ", End Time: " + endTimestamp);
//                                addEventToCalendar(role, startTimestamp, endTimestamp);
//                            } else {
//                                Log.e(TAG, "Shift object is null for document: " + document.getId());
//                            }
//                        }
//                    } else {
//                        Log.e(TAG, "Error getting documents: ", task.getException());
//                    }
//                });
//    }
//
//    private void addEventToCalendar(String title, long beginTimeInMillis, long endTimeInMillis) {
//        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getContext(), "Calendar permission not granted", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (title == null) {
//            Log.e(TAG, "Title is null");
//            title = "No Title";  // Provide a default title if null
//        }
//
//        ContentResolver cr = getActivity().getContentResolver();
//        ContentValues values = new ContentValues();
//        values.put(CalendarContract.Events.DTSTART, beginTimeInMillis);
//        values.put(CalendarContract.Events.DTEND, endTimeInMillis);
//        values.put(CalendarContract.Events.TITLE, title);
//        values.put(CalendarContract.Events.DESCRIPTION, "Generated from Shifts App");
//
//        long calendarId = getPrimaryCalendarId();
//        if (calendarId == -1) {
//            Log.e(TAG, "No valid calendar ID found");
//            return;  // Exit the method if no valid calendar ID is found
//        }
//        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
//
//        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
//
//        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
//        if (uri == null) {
//            Toast.makeText(getContext(), "Failed to add event to calendar", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private long getPrimaryCalendarId() {
//        Cursor cursor = null;
//        try {
//            cursor = getContext().getContentResolver().query(
//                    CalendarContract.Calendars.CONTENT_URI,
//                    new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY},
//                    null,
//                    null,
//                    null
//            );
//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    long id = cursor.getLong(0);
//                    boolean isPrimary = cursor.getInt(1) == 1;
//                    if (isPrimary) {
//                        Log.d(TAG, "Using primary calendar ID: " + id);
//                        return id;
//                    }
//                }
//                // If no primary calendar, return the ID of the last calendar.
//                if (cursor.moveToLast()) {
//                    long id = cursor.getLong(0);
//                    Log.d(TAG, "Primary calendar not found, using last calendar ID: " + id);
//                    return id;
//                }
//            }
//            Log.e(TAG, "No valid calendar ID found or cursor is null");
//            return -1;
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                addAllShiftsToCalendar();
//            } else {
//                Toast.makeText(getContext(), "Permission denied to write to calendar", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void showCalendarSelection() {
//        List<String> calendarNames = new ArrayList<>();
//        List<Long> calendarIds = new ArrayList<>();
//
//        Cursor cursor = getContext().getContentResolver().query(
//                CalendarContract.Calendars.CONTENT_URI,
//                new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
//                null,
//                null,
//                null
//        );
//
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                long id = cursor.getLong(0);
//                String name = cursor.getString(1);
//                calendarIds.add(id);
//                calendarNames.add(name);
//            }
//            cursor.close();
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Select Calendar");
//        CharSequence[] items = calendarNames.toArray(new CharSequence[0]);
//        builder.setItems(items, (dialog, which) -> {
//            long selectedCalendarId = calendarIds.get(which);
//            Log.d(TAG, "User selected calendar ID: " + selectedCalendarId);
//            // Save the selected calendar ID for future operations
//            // e.g., SharedPreferences or directly use it for adding events
//        });
//        builder.show();
//    }
//
//    private void showDatePickerDialog(boolean isStartDate) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(
//                getContext(),
//                (view, year1, monthOfYear, dayOfMonth) -> {
//                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
//                    if (isStartDate) {
//                        textViewStartDate.setText("Start Date: " + date);
//                    } else {
//                        textViewEndDate.setText("End Date: " + date);  // Assuming you have an end date TextView
//                    }
//                },
//                year,
//                month,
//                day);
//        datePickerDialog.show();
//    }
//
//    private void showTimePickerDialog(boolean isStartTime) {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//
//        TimePickerDialog timePickerDialog = new TimePickerDialog(
//                getContext(),
//                (view, hourOfDay, minuteOfHour) -> {
//                    String time = hourOfDay + ":" + minuteOfHour;
//                    if (isStartTime) {
//                        textViewStartTime.setText("Start Time: " + time);
//                    } else {
//                        textViewEndTime.setText("End Time: " + time);
//                    }
//                },
//                hour,
//                minute,
//                true  // Use 24-hour time format
//        );
//        timePickerDialog.show();
//    }
//}


package com.example.shiftsfinalproj;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ShiftsFragment extends Fragment implements ShiftAdapter.ItemClickListener {
    private EditText editTextRole;
    private TextView textViewStartDate, textViewEndDate, textViewStartTime, textViewEndTime;
    private RecyclerView shiftsRecyclerView;
    private ShiftAdapter shiftAdapter;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private List<Shift> shifts = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        initializeView(view);
        setupRecyclerView();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        loadUserShifts();
        return view;
    }

    private void initializeView(View view) {

        shiftsRecyclerView = view.findViewById(R.id.shiftsRecyclerView);
        Button buttonAddAllShiftsToCalendar = view.findViewById(R.id.buttonAddAllShiftsToCalendar);
        Button buttonNewShift = view.findViewById(R.id.buttonNewShift);

        loadUserShifts();
        buttonNewShift.setOnClickListener(v -> showNewShiftDialog());
        buttonAddAllShiftsToCalendar.setOnClickListener(v -> addAllShiftsToCalendar());
    }

    private void setupRecyclerView() {
        shiftAdapter = new ShiftAdapter(getContext(), shifts, this);
        shiftsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shiftsRecyclerView.setAdapter(shiftAdapter);
    }


    //Load shitfs
    private void loadUserShifts() {
        if (user == null) return;
        firestore.collection("shifts").whereEqualTo("user_id", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    shifts.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Shift shift = document.toObject(Shift.class);
                        shift.setStartDate();
                        shift.setEndDate();
                        shift.setShift_id(document.getId()); // Make sure Shift has an id field
                        shifts.add(shift);
                    }
                    shiftAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading shifts", e));
    }






    //Edit Button and functions
    @Override
    public void onEditClick(Shift shift) {
        showEditDialog(shift);
    }
    public void showEditDialog(Shift shift) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Shift");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_shift_dialog, null);

        EditText editRole = dialogView.findViewById(R.id.editRole);
        EditText editStartDate = dialogView.findViewById(R.id.editStartDate);
        EditText editStartTime = dialogView.findViewById(R.id.editStartTime);
        EditText editEndDate = dialogView.findViewById(R.id.editEndDate);
        EditText editEndTime = dialogView.findViewById(R.id.editEndTime);

        // Initialize fields with current shift data
        editRole.setText(shift.getRole());
        editStartDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(shift.getStartTimestamp())));
        editStartTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(shift.getStartTimestamp())));
        editEndDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(shift.getEndTimestamp())));
        editEndTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(shift.getEndTimestamp())));

        builder.setView(dialogView);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newRole = editRole.getText().toString();
            long newStartTimestamp = combineDateAndTime(editStartDate.getText().toString(), editStartTime.getText().toString());
            long newEndTimestamp = combineDateAndTime(editEndDate.getText().toString(), editEndTime.getText().toString());

            updateShift(shift, newRole, newStartTimestamp, newEndTimestamp);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void updateShift(Shift shift, String newRole, long newStartTimestamp, long newEndTimestamp) {
        Map<String, Object> update = new HashMap<>();
        update.put("role", newRole);
        update.put("startTimestamp", newStartTimestamp);
        update.put("endTimestamp", newEndTimestamp);

        firestore.collection("shifts").document(shift.getShiftid())
                .update(update)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Shift updated", Toast.LENGTH_SHORT).show();
                    loadUserShifts();  // Reload the data
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating shift", Toast.LENGTH_SHORT).show());
    }





    //new shift button
    private void showNewShiftDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("New Shift");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_shift, null);

        editTextRole = dialogView.findViewById(R.id.editTextRole);
        textViewStartDate = dialogView.findViewById(R.id.textViewStartDate);
        textViewEndDate = dialogView.findViewById(R.id.textViewEndDate);
        textViewStartTime = dialogView.findViewById(R.id.textViewStartTime);
        textViewEndTime = dialogView.findViewById(R.id.textViewEndTime);


        Button buttonSelectStartDate = dialogView.findViewById(R.id.buttonSelectStartDate);
        buttonSelectStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        Button buttonSelectStartTime = dialogView.findViewById(R.id.buttonSelectStartTime);
        buttonSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));

        Button buttonSelectEndTime = dialogView.findViewById(R.id.buttonSelectEndTime);
        buttonSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        Button buttonSelectEndDate = dialogView.findViewById(R.id.buttonSelectEndDate);
        buttonSelectEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            addShift();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void addShift() {
        String startDate = textViewStartDate.getText().toString().replace("Start Date: ", "").trim();
        String endDate = textViewEndDate.getText().toString().replace("End Date: ", "").trim();
        String startTime = textViewStartTime.getText().toString().replace("Start Time: ", "").trim();
        String endTime = textViewEndTime.getText().toString().replace("End Time: ", "").trim();
        String role = editTextRole.getText().toString().trim();

        if (startTime.isEmpty() || endTime.isEmpty() || startDate.isEmpty() || role.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        long startTimestamp = combineDateAndTime(startDate, startTime);
        long endTimestamp = combineDateAndTime(endDate, endTime);

        Map<String, Object> shift = new HashMap<>();
        shift.put("startTimestamp", startTimestamp);
        shift.put("endTimestamp", endTimestamp);
        shift.put("role", role);
        shift.put("user_id", user.getUid());

        firestore.collection("shifts")
                .add(shift)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Shift added successfully", Toast.LENGTH_SHORT).show();
                    loadUserShifts();  // Reload shifts after adding
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add shift: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }






    //Delete Button and functions
    @Override
    public void onDeleteClick(Shift shift) {
        // Delete the shift from Firestore
        firestore.collection("shifts").document(shift.getShiftid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Shift deleted", Toast.LENGTH_SHORT).show();
                    loadUserShifts(); // Reload the shifts
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting shift", Toast.LENGTH_SHORT).show());
    }





    //add to calendar for each shift
    @Override
    public void onAddToCalendarClick(Shift shift) {
            if (shift != null) {
            // Directly use the start and end timestamps from the Shift object.
            String role = shift.getRole() != null ? shift.getRole() : "No Role";
            long startTimestamp = shift.getStartTimestamp();
            long endTimestamp = shift.getEndTimestamp();

            Log.d(TAG, "Role: " + role + ", Start Time: " + startTimestamp + ", End Time: " + endTimestamp);
            addEventToCalendar(role, startTimestamp, endTimestamp);
            //Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
        }
    }
    //Add to calendar with all the functions
    private void addAllShiftsToCalendar() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Calendar write permission not granted");
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
            return;
        }

        if (user == null) {
            Log.e(TAG, "User is null");
            return;
        }
        showCalendarSelection();
        firestore.collection("shifts")
                .whereEqualTo("user_id", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "Successfully retrieved shifts");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Shift shift = document.toObject(Shift.class);
                            if (shift != null) {
                                // Directly use the start and end timestamps from the Shift object.
                                String role = shift.getRole() != null ? shift.getRole() : "No Role";
                                long startTimestamp = shift.getStartTimestamp();
                                long endTimestamp = shift.getEndTimestamp();

                                Log.d(TAG, "Role: " + role + ", Start Time: " + startTimestamp + ", End Time: " + endTimestamp);
                                addEventToCalendar(role, startTimestamp, endTimestamp);
                            } else {
                                Log.e(TAG, "Shift object is null for document: " + document.getId());
                            }
                        }
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    private void addEventToCalendar(String title, long beginTimeInMillis, long endTimeInMillis) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Calendar permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title == null) {
            Log.e(TAG, "Title is null");
            title = "No Title";  // Provide a default title if null
        }

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTimeInMillis);
        values.put(CalendarContract.Events.DTEND, endTimeInMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "Generated from Shifts App");

        long calendarId = getPrimaryCalendarId();
        if (calendarId == -1) {
            Log.e(TAG, "No valid calendar ID found");
            return;  // Exit the method if no valid calendar ID is found
        }
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);

        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(getContext(), "Failed to add event to calendar", Toast.LENGTH_SHORT).show();
        }
    }
    private long getPrimaryCalendarId() {
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(
                    CalendarContract.Calendars.CONTENT_URI,
                    new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY},
                    null,
                    null,
                    null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    boolean isPrimary = cursor.getInt(1) == 1;
                    if (isPrimary) {
                        Log.d(TAG, "Using primary calendar ID: " + id);
                        Toast.makeText(getContext(), "Added", Toast.LENGTH_SHORT).show();
                        return id;
                    }
                }
                // If no primary calendar, return the ID of the last calendar.
                if (cursor.moveToLast()) {
                    long id = cursor.getLong(0);
                    Log.d(TAG, "Primary calendar not found, using last calendar ID: " + id);
                    return id;
                }
            }
            Log.e(TAG, "No valid calendar ID found or cursor is null");
            return -1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addAllShiftsToCalendar();
            } else {
                Toast.makeText(getContext(), "Permission denied to write to calendar", Toast.LENGTH_SHORT).show();
            }
        }
    }







    //All the diffrent showDialog
    private void showCalendarSelection() {
        List<String> calendarNames = new ArrayList<>();
        List<Long> calendarIds = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                calendarIds.add(id);
                calendarNames.add(name);
            }
            cursor.close();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Calendar");
        CharSequence[] items = calendarNames.toArray(new CharSequence[0]);
        builder.setItems(items, (dialog, which) -> {
            long selectedCalendarId = calendarIds.get(which);
            Log.d(TAG, "User selected calendar ID: " + selectedCalendarId);
            // Save the selected calendar ID for future operations
            // e.g., SharedPreferences or directly use it for adding events
        });
        builder.show();
    }
    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    if (isStartDate) {
                        textViewStartDate.setText("Start Date: " + date);
                    } else {
                        textViewEndDate.setText("End Date: " + date);  // Assuming you have an end date TextView
                    }
                },
                year,
                month,
                day);
        datePickerDialog.show();
    }
    private void showTimePickerDialog(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = hourOfDay + ":" + minuteOfHour;
                    if (isStartTime) {
                        textViewStartTime.setText("Start Time: " + time);
                    } else {
                        textViewEndTime.setText("End Time: " + time);
                    }
                },
                hour,
                minute,
                true  // Use 24-hour time format
        );
        timePickerDialog.show();
    }







    //Time convertor
    private long combineDateAndTime(String date, String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date parsedDate = dateFormat.parse(date + " " + time);
            if (parsedDate != null) {
                return parsedDate.getTime();
            } else {
                Log.e(TAG, "Parsed date is null for date: " + date + " and time: " + time);
                return 0;
            }
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse date or time for date: " + date + " and time: " + time, e);
            return 0;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }
}






























//    private long convertTimeToMillis(String dateTime) {
//        if (dateTime == null) {
//            Log.e(TAG, "DateTime string is null");
//            return 0;
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//        try {
//            Date date = sdf.parse(dateTime);
//            return date != null ? date.getTime() : 0;
//        } catch (ParseException e) {
//            Log.e(TAG, "Error parsing date/time: " + dateTime, e);
//            return 0;
//        } catch (java.text.ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
