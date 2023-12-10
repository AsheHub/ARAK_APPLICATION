package com.example.doctor_view;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    Button signInButton;
    TextView signupRedirectText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);

        firebaseAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() | !validatePassword()) {
                    validateEmail();
                    validatePassword();
                } else {
                    signInUser();
                }
            }
        });

    }//ends of onCreate

    public void onSendSignUp(View view) {
        Intent intent_2 = new Intent(MainActivity.this, SelectUniversityActivity.class);
        startActivity(intent_2);
    }

    public void onSendSignUpPatient(View view){
        Intent intent = new Intent(MainActivity.this, PatientRegistration.class);
        startActivity(intent);
    }
    public Boolean validateEmail() {
        String val = emailEditText.getText().toString();
        if (val.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return false;
        } else if (!isValidEmail(val)) {
            emailEditText.setError("Enter a valid email address");
            return false;
        }
        else {
            emailEditText.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = passwordEditText.getText().toString();
        if (val.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        }else if (!isValidPassword(val)) {
            passwordEditText.setError("Password must be at least 6 characters long");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        // You can add your own email validation logic here
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // You can customize your password criteria here
        return password.length() >= 6;
    }
    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    if (user.isEmailVerified()) {
                        checkUserType(user.getUid());
                        // Email is verified, allow the user to proceed to the personal information activity
                        Toast.makeText(MainActivity.this, "Your email has been verified.", Toast.LENGTH_SHORT).show();

                    } else {
                        // Email is not verified, prompt the user to verify their email
                        Toast.makeText(MainActivity.this, "Please verify your email before proceeding.", Toast.LENGTH_SHORT).show();
                        // You can provide an option for the user to resend the verification email if needed.
                    }
                }
            } else {
                // Sign-in failed, handle errors
                Toast.makeText(MainActivity.this, "Sign-in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserType(String userId) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        DatabaseReference patientsReference = FirebaseDatabase.getInstance().getReference("Patients").child(userId);
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User exists in "users" node
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // User does not exist in "users" node, check "patients" node
                    patientsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot patientSnapshot) {
                            if (patientSnapshot.exists()) {
                                // User exists in "patients" node
                                Intent intent = new Intent(MainActivity.this, PatientHomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle the case where the user is not a patient or doctor
                                Toast.makeText(MainActivity.this, "Invalid user type.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }


}//ends of main