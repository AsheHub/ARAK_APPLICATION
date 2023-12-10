package com.example.doctor_view;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {


private TextView fullNameTextView, birthDateTextView, genderTextView,
            hospitalTextView, emailTextView, passwordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        fullNameTextView = findViewById(R.id.fullNameTextView);
        birthDateTextView = findViewById(R.id.birthDateTextView);
        genderTextView = findViewById(R.id.genderTextView);
        hospitalTextView = findViewById(R.id.hospitalTextView);
        //userIdTextView = findViewById(R.id.userIdTextView);
        emailTextView = findViewById(R.id.profileEmail);
        passwordTextView = findViewById(R.id.profilepassword);
        Button logoutButton;
        mAuth = FirebaseAuth.getInstance();

        // Assume you have buttons for updating email and password

        // Assuming you have a Button with id logoutButton in your XML layout
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
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }


    private void retrieveUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data exists, retrieve it
                    User user = dataSnapshot.getValue(User.class);

                    // Update UI with user data
                    updateUI(user);
                } else {
                    // Handle the case where user data doesn't exist
                    Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(ProfileActivity.this, "Failed to retrieve user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        if (user != null) {
            fullNameTextView.setText(user.getFullName());
            birthDateTextView.setText(user.getBirthday());
            genderTextView.setText(user.getGender());
            hospitalTextView.setText(user.getHealthCare());
            emailTextView.setText(user.getEmail());
            passwordTextView.setText(user.getPassword());
            // update other UI elements with user data
        }
    }


    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                // Handle Home item click
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.navigation_home:
                // Handle Dashboard item click
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.navigation_clender:
                // Handle Profile item click
                startActivity(new Intent(this, ViewScheduleActivity.class));
                break;

        }
    }

}




