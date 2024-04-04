package com.example.shiftsfinalproj;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ShiftsFragment extends Fragment {

//    private EditText editTextStartTime, editTextEndTime, editTextRole;
////    private DatabaseReference mDatabase;
////    private FirebaseAuth mAuth;
//    private FirebaseFirestore firestore;
//    private FirebaseUser user;

    private EditText editTextStartTime, editTextEndTime, editTextRole;
    private TextView textViewShiftsDisplay;
    private FirebaseFirestore firestore;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);

//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        editTextStartTime = view.findViewById(R.id.editTextStartTime);
        editTextEndTime = view.findViewById(R.id.editTextEndTime);
        editTextRole = view.findViewById(R.id.editTextRole);
        textViewShiftsDisplay = view.findViewById(R.id.textViewShiftsDisplay); // TextView to display shifts
        Button buttonAddShift = view.findViewById(R.id.buttonAddShift);

        loadUserShifts();
        buttonAddShift.setOnClickListener(v -> addShift());


        return view;
    }

    private void addShift() {
        String startTime = editTextStartTime.getText().toString().trim();
        String endTime = editTextEndTime.getText().toString().trim();
        String role = editTextRole.getText().toString().trim();

        if (startTime.isEmpty() || endTime.isEmpty() || role.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> shift = new HashMap<>();
        shift.put("startTime", startTime);
        shift.put("endTime", endTime);
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

    private void loadUserShifts() {
        if (user == null) {
            textViewShiftsDisplay.setText("No user logged in.");
            return;
        }

        firestore.collection("shifts")
                .whereEqualTo("user_id", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder shiftsBuilder = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> shift = document.getData();
                            shiftsBuilder.append("Start Time: ").append(shift.get("startTime"))
                                    .append("\nEnd Time: ").append(shift.get("endTime"))
                                    .append("\nRole: ").append(shift.get("role"))
                                    .append("\n\n");
                        }
                        textViewShiftsDisplay.setText(shiftsBuilder.toString());
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}

//    private void addShift() {
//        String startTime = editTextStartTime.getText().toString().trim();
//        String endTime = editTextEndTime.getText().toString().trim();
//        String role = editTextRole.getText().toString().trim();
//        String userid = user.getUid();
//
//        // Generate a unique key for each shift
//        //String key = mDatabase.child("shifts").push().getKey();
//
//        Shift shift = new Shift(startTime, endTime, role, userid);
//        Map<String, Object> shiftValues = new HashMap<>();
//        shiftValues.put("startTime", shift.startTime);
//        shiftValues.put("endTime", shift.endTime);
//        shiftValues.put("role", shift.role);
//        shiftValues.put("user_id", shift.userid);
//
////        Map<String, Object> childUpdates = new HashMap<>();
////        childUpdates.put("/shifts/" + key, shiftValues);
//
////        mDatabase.updateChildren(childUpdates)
////                .addOnSuccessListener(aVoid -> Log.d(TAG, "Shift added successfully!"))
////                .addOnFailureListener(e -> Log.e(TAG, "Failed to add shift", e));
//
//        firestore.collection("shifts")
//                .add(shiftValues)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        // Product added successfully
//                        Toast.makeText(getActivity(), "Success ", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Error adding product
//                        Toast.makeText(getActivity(), "failed: ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        //Toast.makeText(getActivity(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//    }
//}



//public class ShiftsFragment extends Fragment {
//
//    private EditText editTextStartTime, editTextEndTime, editTextRole;
//    private DatabaseReference mDatabase;
//    private FirebaseAuth mAuth;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
//
//        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//        editTextStartTime = view.findViewById(R.id.editTextStartTime);
//        editTextEndTime = view.findViewById(R.id.editTextEndTime);
//        editTextRole = view.findViewById(R.id.editTextRole);
//        Button buttonAddShift = view.findViewById(R.id.buttonAddShift);
//
//        buttonAddShift.setOnClickListener(v -> addShift());
//        //loadUserShifts();
//
//        return view;
//    }
//}

//    private void loadUserShifts() {
//        String userId = mAuth.getCurrentUser().getUid();
//        DatabaseReference userShiftsRef = mDatabase.child("user-shifts").child(userId);
//
//        userShiftsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot shiftSnapshot : dataSnapshot.getChildren()) {
//                    Shift shift = shiftSnapshot.getValue(Shift.class);
//                    // Add shift to your UI here
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "loadUserShifts:onCancelled", databaseError.toException());
//                // Handle errors...
//            }
//        });
//    }
//
//    private void addShift() {
//        String userId = mAuth.getCurrentUser().getUid();
//        String startTime = editTextStartTime.getText().toString().trim();
//        String endTime = editTextEndTime.getText().toString().trim();
//        String role = editTextRole.getText().toString().trim();
//
//        // Validate inputs...
//
//        String key = mDatabase.child("shifts").push().getKey();
//        Shift shift = new Shift(userId, startTime, endTime, role);
//        Map<String, Object> shiftValues = shift.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/shifts/" + key, shiftValues);
//        childUpdates.put("/user-shifts/" + userId + "/" + key, shiftValues);
//
//        mDatabase.updateChildren(childUpdates);
//    }



//public class ShiftsFragment extends Fragment {
//
//    public ShiftsFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_shifts, container, false);
//    }
//}
