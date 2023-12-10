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

public class PatientRegistration extends AppCompatActivity {

    private EditText pNameEditText;
    private EditText pBirthDateEditText;
    private RadioGroup pradioGroupGender;
    private EditText pEmailEditText;
    private EditText pPasswordEditText;

    private EditText pPhoneNumber;

    private Spinner pChronic_Diseases_spinner;
    private FirebaseAuth pFirebaseAuth;
    FirebaseDatabase pDatabase;
    DatabaseReference pReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration);

        pNameEditText = findViewById(R.id.nameEditText);
        pBirthDateEditText = findViewById(R.id.birthDateEditText);
        pradioGroupGender = findViewById(R.id.radioGroupGender);
        pPhoneNumber=findViewById(R.id.phoneNumberEditText);
        pChronic_Diseases_spinner=findViewById(R.id.Chronic_Diseases);
        pEmailEditText = findViewById(R.id.emailEditText);
        pPasswordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);

        //function of format of birth
        addTextWatcherToBirthDateEditText();

        pFirebaseAuth = FirebaseAuth.getInstance();
        pDatabase =FirebaseDatabase.getInstance();
        pReference= FirebaseDatabase.getInstance().getReference("Patients");


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Chronic_Diseases, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pChronic_Diseases_spinner.setAdapter(adapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPatient();}
        });
    }



    private void addTextWatcherToBirthDateEditText() {
        pBirthDateEditText.addTextChangedListener(new TextWatcher() {
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
                    pBirthDateEditText.setText(current);
                    pBirthDateEditText.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void registerPatient() {
        String patient_name = pNameEditText.getText().toString();
        String patient_birthDate = pBirthDateEditText.getText().toString();
        int selectedGenderId = pradioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String patient_gender = selectedGender.getText().toString();
        String patient_Chronic_Diseases= pChronic_Diseases_spinner.getSelectedItem().toString();

        String patient_phone= pPhoneNumber.getText().toString().trim();
        String patient_email = pEmailEditText.getText().toString().trim();
        String patient_password = pPasswordEditText.getText().toString().trim();

        pFirebaseAuth.createUserWithEmailAndPassword(patient_email, patient_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, you can now collect user information
                            FirebaseUser pUser = pFirebaseAuth.getCurrentUser();
                            assert pUser != null;

                            // Send email verification
                            pUser.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                // Email verification sent successfully
                                                Toast.makeText(PatientRegistration.this, "Verification email sent, please check your inbox.", Toast.LENGTH_SHORT).show();

                                                // Get the user ID
                                                String userId = pUser.getUid();
                                                // Create User object with the provided information
                                                patientUser userClass = new patientUser(patient_name, patient_birthDate, patient_gender,patient_email, patient_password);
                                                userClass.setUserType("patient");
                                                userClass.setPhone_Number(patient_phone);
                                                userClass.setChronic_Diseases(patient_Chronic_Diseases);


                                                // Store user information in RealTime Database
                                                pReference.child(userId).setValue(userClass);

                                                Intent intent = new Intent(PatientRegistration.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Error sending verification email
                                                Toast.makeText(PatientRegistration.this, "Failed to send verification email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // Registration failed, handle errors
                            Toast.makeText(PatientRegistration.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}