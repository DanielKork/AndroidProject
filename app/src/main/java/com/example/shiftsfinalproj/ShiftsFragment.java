package com.example.shiftsfinalproj;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ShiftsFragment extends Fragment {
    private EditText editTextStartTime, editTextEndTime, editTextRole;
    private TextView textViewStartDate, textViewEndDate, textViewStartTime, textViewEndTime;
    private TextView textViewShiftsDisplay;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);

//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


//        editTextStartTime = view.findViewById(R.id.editTextStartTime);
//        editTextEndTime = view.findViewById(R.id.editTextEndTime);
        editTextRole = view.findViewById(R.id.editTextRole);
        textViewStartDate = view.findViewById(R.id.textViewStartDate);
        textViewStartTime = view.findViewById(R.id.textViewStartTime);
        textViewEndTime = view.findViewById(R.id.textViewEndTime);
        textViewEndDate = view.findViewById(R.id.textViewEndDate);
        textViewShiftsDisplay = view.findViewById(R.id.textViewShiftsDisplay);

        Button buttonSelectStartDate = view.findViewById(R.id.buttonSelectStartDate);
        buttonSelectStartDate.setOnClickListener(v -> showDatePickerDialog(true));

        Button buttonSelectStartTime = view.findViewById(R.id.buttonSelectStartTime);
        buttonSelectStartTime.setOnClickListener(v -> showTimePickerDialog(true));

        Button buttonSelectEndTime = view.findViewById(R.id.buttonSelectEndTime);
        buttonSelectEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        Button buttonSelectEndDate = view.findViewById(R.id.buttonSelectEndDate);
        buttonSelectEndDate.setOnClickListener(v -> showDatePickerDialog(false));

        Button buttonAddShift = view.findViewById(R.id.buttonAddShift);
        Button buttonAddAllShiftsToCalendar = view.findViewById(R.id.buttonAddAllShiftsToCalendar);

        loadUserShifts();
        buttonAddShift.setOnClickListener(v -> addShift());
        buttonAddAllShiftsToCalendar.setOnClickListener(v -> addAllShiftsToCalendar());


        return view;
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

    private void addShift() {
//        String startTime = editTextStartTime.getText().toString().trim();
//        String endTime = editTextEndTime.getText().toString().trim();
//        String role = editTextRole.getText().toString().trim();
        String startDate = textViewStartDate.getText().toString().replace("Start Date: ", "").trim();
        String endDate = textViewEndDate.getText().toString().replace("End Date: ", "").trim();
        String startTime = textViewStartTime.getText().toString().replace("Start Time: ", "").trim();
        String endTime = textViewEndTime.getText().toString().replace("End Time: ", "").trim();
        //String role = editTextRole != null ? editTextRole.getText().toString().trim() : "";
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

//        Map<String, Object> shift = new HashMap<>();
//        shift.put("startTime", startTime);
//        shift.put("endTime", endTime);
//        shift.put("role", role);
//        shift.put("user_id", user.getUid());
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
    private long combineDateAndTime(String date, String time) {
        // Assuming date format is "dd/MM/yyyy" and time format is "HH:mm"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date parsedDate = dateFormat.parse(date + " " + time);
            return parsedDate != null ? parsedDate.getTime() : 0;
        } catch (ParseException e) {
            Log.e(TAG, "Failed to parse date or time", e);
            return 0;
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserShifts() {
        if (user == null) {
            textViewShiftsDisplay.setText("No user logged in.");
            return;
        }

        firestore.collection("shifts")
                .whereEqualTo("user_id", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        StringBuilder shiftsBuilder = new StringBuilder();
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Map<String, Object> shift = document.getData();
//                            shiftsBuilder.append("Start Time: ").append(shift.get("startTime"))
//                                    .append("\nEnd Time: ").append(shift.get("endTime"))
//                                    .append("\nRole: ").append(shift.get("role"))
//                                    .append("\n\n");
//                        }
//                        textViewShiftsDisplay.setText(shiftsBuilder.toString());
//                    }
                    if (task.isSuccessful()) {
                        StringBuilder shiftsBuilder = new StringBuilder();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> shift = document.getData();
                            Long startTimestamp = (Long) shift.get("startTimestamp");
                            Long endTimestamp = (Long) shift.get("endTimestamp");

                            // Check if timestamps are not null
                            String startDate = startTimestamp != null ? dateFormat.format(new Date(startTimestamp)) : "Unknown";
                            String endDate = endTimestamp != null ? dateFormat.format(new Date(endTimestamp)) : "Unknown";

                            shiftsBuilder.append("Start Time: ").append(startDate)
                                    .append("\nEnd Time: ").append(endDate)
                                    .append("\nRole: ").append(shift.get("role"))
                                    .append("\n\n");
                        }
                        textViewShiftsDisplay.setText(shiftsBuilder.toString());
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    private void addAllShiftsToCalendar() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, PERMISSION_REQUEST_CODE);
            return;
        }

        if (user != null) {
            firestore.collection("shifts")
                    .whereEqualTo("user_id", user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Shift shift = document.toObject(Shift.class);
                                addEventToCalendar(shift.getRole(), convertTimeToMillis(shift.getStartTime()), convertTimeToMillis(shift.getEndTime()));
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void addEventToCalendar(String title, long beginTimeInMillis, long endTimeInMillis) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Calendar permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentResolver cr = getActivity().getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, beginTimeInMillis);
        values.put(CalendarContract.Events.DTEND, endTimeInMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "Generated from Shifts App");
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri == null) {
            Toast.makeText(getContext(), "Failed to add event to calendar", Toast.LENGTH_SHORT).show();
        }
    }

    private long convertTimeToMillis(String time) {
        // Convert your time string to milliseconds
        return 0;
    }

    @Override
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

}