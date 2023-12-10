package com.example.doctor_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SecondActivity extends AppCompatActivity {
    private EditText emailEditText;
    private EditText nameEditText;
    private EditText passwordEditText;
    private EditText birthDateEditText;
    private RadioGroup radioGroupGender;
    private Spinner spinnerHealthCare;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        nameEditText = findViewById(R.id.nameEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        spinnerHealthCare = findViewById(R.id.spinnerHealthCare);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.health_care_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHealthCare.setAdapter(adapter);

        //function of format of birth
        addTextWatcherToBirthDateEditText();

        firebaseAuth = FirebaseAuth.getInstance();
        database =FirebaseDatabase.getInstance();
        reference= FirebaseDatabase.getInstance().getReference("users");

        String university = getIntent().getStringExtra("university");
        // Define the constant domain for each university
        String domain = getUniversityDomain(university);
        emailEditText.setText( domain );


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();}
        });

    }//ends of onCreate

    private void addTextWatcherToBirthDateEditText() {
        birthDateEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";
            final String ddmmyyyy = "DDMMYYYY";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;
                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (Math.min(year, cal.get(Calendar.YEAR)));
                        cal.set(Calendar.YEAR, year);

                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }
                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    current = clean;
                    birthDateEditText.setText(current);
                    birthDateEditText.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private String getUniversityDomain(String university) {
        switch (university) {
            case "Jeddah University":
                return "@uj.edu.sa";
            case "King Abdulaziz University":
                return "@kau.edu.sa";
            case "Btterjee Medical College":
                return "@bmc.edu.sa";
            default:
                return "";
        }

    }
    private void registerUser() {
        String name = nameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();
        String healthCare = spinnerHealthCare.getSelectedItem().toString();

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Register the user in Firebase Authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, you can now collect user information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;
                            // Send email verification
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Email verification sent successfully
                                                Toast.makeText(SecondActivity.this, "Verification email sent, please check your inbox.", Toast.LENGTH_SHORT).show();
                                                // Get the user ID
                                                String userId = user.getUid();
                                                // Create User object with the provided information
                                                User userClass = new User(name, birthDate, gender, healthCare, email, password);
                                                userClass.setUserType("doctor");
                                                // Store user information in RealTime Database
                                                reference.child(userId).setValue(userClass);

                                                Intent intent = new Intent(SecondActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Error sending verification email
                                                Toast.makeText(SecondActivity.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Registration failed, handle errors
                            Toast.makeText(SecondActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}