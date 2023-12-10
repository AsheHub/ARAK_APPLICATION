package com.example.doctor_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientProfileActivity extends AppCompatActivity {

    private TextView pFullNameTextView, pBirthDateTextView, pGenderTextView, pChronicDiseasesTextView, pPhone, pEmailTextView, pPasswordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        pFullNameTextView = findViewById(R.id.fullNameTextView);
        pBirthDateTextView = findViewById(R.id.birthDateTextView);
        pGenderTextView = findViewById(R.id.genderTextView);
        pEmailTextView = findViewById(R.id.emailTextView);
        // Avoid displaying passwords directly in the UI
        pPasswordTextView = findViewById(R.id.passwordTextView);
        pPhone = findViewById(R.id.phoneNumberEditText);
        pChronicDiseasesTextView = findViewById(R.id.chronicDiseasesTextView);
        Button logoutButton = findViewById(R.id.logoutButton);

        mAuth = FirebaseAuth.getInstance();

        // Retrieve user data and update UI
        retrieveUserData();
         logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }


    private void logoutUser() {
        mAuth.signOut();

        // After signing out, you can redirect the user to the login page or any other desired activity
        Intent intent = new Intent(PatientProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }

    private void retrieveUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data exists, retrieve it
                    patientUser pUser = dataSnapshot.getValue(patientUser.class);

                    // Update UI with user data
                    updateUI(pUser);
                } else {
                    // Handle the case where user data doesn't exist
                    Toast.makeText(PatientProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(PatientProfileActivity.this, "Failed to retrieve user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateUI(patientUser pUser) {
        if (pUser != null) {
            pFullNameTextView.setText(pUser.getPatient_FullName());
            pBirthDateTextView.setText(pUser.getPatient_Birthday());
            pGenderTextView.setText(pUser.getPatient_Gender());
            pChronicDiseasesTextView.setText(pUser.getChronic_Diseases());
            pPhone.setText(pUser.getPhone_Number());
            pEmailTextView.setText(pUser.getPatient_Email());
            // Avoid displaying passwords directly in the UI
            pPasswordTextView.setText(pUser.getPatient_Password()); // You can use a placeholder or leave it empty
            // update other UI elements with user data
        }
    }

    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                // Handle Home item click
                startActivity(new Intent(this, PatientProfileActivity.class));
                break;
            case R.id.navigation_home:
                // Handle Dashboard item click
                startActivity(new Intent(this, PatientHomeActivity.class));
                break;

        }}
}
