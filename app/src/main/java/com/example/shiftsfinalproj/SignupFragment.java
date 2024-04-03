package com.example.shiftsfinalproj;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class SignupFragment extends Fragment {

    private EditText editTextNewEmail;
    private EditText editTextNewPassword;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        editTextNewEmail = view.findViewById(R.id.editTextNewEmail);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        Button buttonSignup = view.findViewById(R.id.buttonSignup);

        buttonSignup.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = editTextNewEmail.getText().toString().trim();
        String password = editTextNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextNewEmail.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            editTextNewPassword.setError("Password is required and should be at least 6 characters.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
                        // Transition to the ShiftsFragment or another relevant fragment
                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, new ShiftsFragment());
                        transaction.commit();
                    } else {
                        // Sign up failed
                        Toast.makeText(getActivity(), "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
