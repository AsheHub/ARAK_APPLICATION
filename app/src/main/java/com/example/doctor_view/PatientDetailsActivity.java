package com.example.doctor_view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PatientDetailsActivity extends AppCompatActivity {

    private TextView patientNameTextView;
    private TextView patientBirthdayTextView;
    private TextView patientPhoneTextView;

    private TextView patient_chronic_diseases;
    private TextView appointmentDetailsTextView;
    private TextView patientGenderTextView;
    private DatabaseReference patientsReference;
     DatabaseReference reservedAppointmentsReference;

    String appointmentId;
    Button comeButton;
    Button didNotComeButton;
    Appointment appointment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);

        // Initialize views
        patientNameTextView = findViewById(R.id.patientNameTextView);
        patientBirthdayTextView = findViewById(R.id.patientBirthdayTextView);
        patient_chronic_diseases=findViewById(R.id.patientChronicDiseasesTextView);
        patientPhoneTextView = findViewById(R.id.patientPhoneTextView);
        appointmentDetailsTextView = findViewById(R.id.appointmentDetailsTextView);
        patientGenderTextView = findViewById(R.id.patientGenderTextView);
        // Find your buttons
        comeButton = findViewById(R.id.comeButton);
        didNotComeButton = findViewById(R.id.didNotComeButton);

        // Get appointment ID from the intent
        appointmentId = getIntent().getStringExtra("APPOINTMENT_ID");

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        patientsReference = firebaseDatabase.getReference().child("Patients");
        reservedAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");

        patientNameTextView.setText(appointmentId);
        // Retrieve and display reserved appointment information
        retrieveAndDisplayReservedAppointmentInformation(appointmentId);

        // Retrieve and display patient information
        retrievePatientInformation(appointmentId);

        Log.d("PatientDetailsActivity", "appointmentId: " + appointmentId);
        // Retrieve and display reserved appointment information
        retrieveReservedAppointmentInformation(appointmentId);

        Log.d("PatientDetailsActivity", "appointmentId: " + appointmentId);


        // Modify the onClick listener for the "COME" button
        comeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve and display reserved appointment information
                handleComeButtonClick();
            }
        });

        didNotComeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the appointment ID is not null
                if (appointmentId != null) {
                    // Get a reference to the specific appointment in the database
                    DatabaseReference appointmentRef = reservedAppointmentsReference.child(appointmentId);

                    // Remove the appointment from "Reserved Appointments"
                    appointmentRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Handle the success case (appointment removed)

                                Log.d("PatientDetailsActivity", "Appointment removed successfully.");
                                Intent intent=new Intent(PatientDetailsActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                                // You can also perform any additional actions or show a message to the user
                            } else {
                                // Handle the case when the appointment removal fails
                                Log.d("PatientDetailsActivity", "Failed to remove appointment. " + task.getException());
                                // Display an error message or perform necessary actions
                            }
                        }
                    });
                } else {
                    // Handle the case when the appointmentId is null
                    Log.d("PatientDetailsActivity", "Appointment ID is null.");
                    // Display an error message or perform necessary actions
                }
            }

        });
    }

    private void updateAppointmentStatus(Appointment appointment) {
        // Assuming you have a reference to the specific appointment in the database
        DatabaseReference appointmentRef = reservedAppointmentsReference.child(appointment.getAppointmentId());
        // Remove the appointment from "Reserved Appointments"
        appointmentRef.removeValue();

        // Add the appointment to "Appointment completed"
        DatabaseReference completedAppointmentsRef = FirebaseDatabase.getInstance()
                .getReference().child("Appointments").child("Appointment completed");
        completedAppointmentsRef.child(appointment.getAppointmentId()).setValue(appointment);
    }

    private boolean isSameDayAsAppointment() {
        // Retrieve the appointment date from the reserved appointment
        // Assuming appointment is stored in the 'appointment' variable
        String appointmentDate = appointment.getDate();
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        // Compare the appointment date with the current date
        return currentDate.equals(appointmentDate);
    }


    private void retrievePatientInformation(String appointmentId) {
        reservedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String patientId = dataSnapshot.child("patientId").getValue(String.class);
                    patientsReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                patientNameTextView.setText(dataSnapshot.child("patient_FullName").getValue(String.class));
                                patientBirthdayTextView.setText(dataSnapshot.child("patient_Birthday").getValue(String.class));
                                patientGenderTextView.setText(dataSnapshot.child("patient_Gender").getValue(String.class));
                               patient_chronic_diseases.setText(dataSnapshot.child("chronic_Diseases").getValue(String.class));
                                patientPhoneTextView.setText(dataSnapshot.child("phone_Number").getValue(String.class));

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void retrieveReservedAppointmentInformation(String appointmentId) {
        reservedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract details of the reserved appointment
                    Appointment reservedAppointment = dataSnapshot.getValue(Appointment.class);
                    // Now, you can display or use these details as needed
                    if (reservedAppointment != null) {
                        String appointmentDate = reservedAppointment.getDate();
                        String appointmentTime = reservedAppointment.getTime();
                        String healthCare = reservedAppointment.getHealthCare();
                        // Display appointment details
                        String appointmentDetails = "Date: " + appointmentDate +
                                "\nTime: " + appointmentTime +
                                "\nHealth Care: " + healthCare;
                        appointmentDetailsTextView.setText(appointmentDetails);
                        // Enable the "COME" button after appointment data is retrieved
                        comeButton.setEnabled(true);
                    } else {
                        // Handle the case when appointment is null
                        Log.d("PatientDetailsActivity", "Appointment is null.");
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    // Method to handle the "COME" button click
    // Method to handle the "COME" button click
    private void handleComeButtonClick() {
        // Log the state of appointment and date before the check
        Log.d("PatientDetailsActivity", "Appointment: " + appointment);
        Log.d("PatientDetailsActivity", "Current date: " + getCurrentDate());

        // Check if appointment is not null before accessing its methods
        if (appointment != null && isSameDayAsAppointment()) {
            // If the date is the same, update patientHasCome status
            appointment.setPatientHasCome(true);
            // Update the Firebase database to reflect the change
            updateAppointmentStatus(appointment);
            // Log the success message
            Log.d("PatientDetailsActivity", "Appointment updated successfully.");
            // Navigate to MyPatients activity
            Intent intent = new Intent(PatientDetailsActivity.this, MyPatientsActivity.class);
            intent.putExtra("APPOINTMENT_ID", appointmentId);
            startActivity(intent);
        } else {
            // Log the state of appointment and date when the conditions are not met
            Log.d("PatientDetailsActivity", "Appointment is null or different day.");
            Log.d("PatientDetailsActivity", "Appointment date: " + (appointment != null ? appointment.getDate() : "null"));
            Log.d("PatientDetailsActivity", "Current date: " + getCurrentDate());

            // Display an error message or perform necessary actions
        }
    }

    // Helper method to get the current date
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Method to retrieve and display reserved appointment information
    private void retrieveAndDisplayReservedAppointmentInformation(String appointmentId) {
        reservedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract details of the reserved appointment
                    appointment = dataSnapshot.getValue(Appointment.class);

                    // Now, you can display or use these details as needed
                    if (appointment != null) {
                        String appointmentDate = appointment.getDate();
                        String appointmentTime = appointment.getTime();
                        String healthCare = appointment.getHealthCare();

                        // Display appointment details
                        String appointmentDetails = "Date: " + appointmentDate +
                                "\nTime: " + appointmentTime +
                                "\nHealth Care: " + healthCare;

                        appointmentDetailsTextView.setText(appointmentDetails);
                        // Enable the "COME" button after appointment data is retrieved
                        comeButton.setEnabled(true);
                    } else {
                        // Handle the case when appointment is null
                        Log.d("PatientDetailsActivity", "Appointment is null.");
                        // Display an error message or perform necessary actions
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}