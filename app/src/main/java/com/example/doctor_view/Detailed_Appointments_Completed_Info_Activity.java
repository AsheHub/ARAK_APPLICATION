package com.example.doctor_view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Detailed_Appointments_Completed_Info_Activity extends AppCompatActivity {

     TextView patientNameTextView;
     TextView patientBirthdayTextView;
     TextView patientEmailTextView;
     TextView appointmentDetailsTextView;
     TextView patientGenderTextView;

    private DatabaseReference patientsReference;
    private DatabaseReference appointmentsCompletedReference;

    private String patientId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_appointments_completed_info);

        // Initialize views
        patientNameTextView = findViewById(R.id.patientNameTextView);
        patientBirthdayTextView = findViewById(R.id.patientBirthdayTextView);
        patientEmailTextView = findViewById(R.id.patientEmailTextView);
        patientGenderTextView = findViewById(R.id.patientGenderTextView);
        appointmentDetailsTextView = findViewById(R.id.appointmentDetailsTextView);

        // Get patient ID from the intent
        patientId = getIntent().getStringExtra("PATIENT_ID");

        // Initialize Firebase
        patientsReference = FirebaseDatabase.getInstance().getReference().child("Patients");
        appointmentsCompletedReference = FirebaseDatabase.getInstance().getReference()
                .child("Appointments Completed")
                .child(patientId);

        // Retrieve and display patient information
        retrieveAndDisplayPatientInformation();
        // Retrieve and display appointment information
        retrieveAndDisplayAppointmentInformation();
    }

    private void retrieveAndDisplayPatientInformation() {
        patientsReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    patientUser patient = dataSnapshot.getValue(patientUser.class);
                    if (patient != null) {
                        displayPatientInformation(patient);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void retrieveAndDisplayAppointmentInformation() {
        appointmentsCompletedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        displayAppointmentInformation(appointment);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void displayPatientInformation(patientUser patient) {
        patientNameTextView.setText(patient.getPatient_FullName());
        patientEmailTextView.setText(patient.getPatient_Email());
        patientBirthdayTextView.setText(patient.getPatient_Birthday());
        patientGenderTextView.setText(patient.getPatient_Gender());
    }

    private void displayAppointmentInformation(Appointment appointment) {
        String appointmentDate = appointment.getDate();
        String appointmentTime = appointment.getTime();
        String healthCare = appointment.getHealthCare();

        String appointmentDetails = "Date: " + appointmentDate +
                "\nTime: " + appointmentTime +
                "\nHealth Care: " + healthCare;

        appointmentDetailsTextView.setText(appointmentDetails);
    }
}
