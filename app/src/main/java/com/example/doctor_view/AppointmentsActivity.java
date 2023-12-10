package com.example.doctor_view;
// AppointmentsActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity  implements ReservedAppointmentsAdapter.AppointmentClickListener{

    private DatabaseReference databaseReference;
    private List<Appointment> patientAppointmentsList;
    private ReservedAppointmentsAdapter reservedAppointmentsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");


        // Initialize patientAppointmentsList and adapter
        patientAppointmentsList = new ArrayList<>();
        reservedAppointmentsAdapter = new ReservedAppointmentsAdapter(this, patientAppointmentsList);


        // Set click listener for the cancel button in the adapter
        reservedAppointmentsAdapter.setAppointmentClickListener(this);
        // Set up the ListView to display patient's reserved appointments
        ListView appointmentsListView = findViewById(R.id.appointmentsListView);
        appointmentsListView.setAdapter(reservedAppointmentsAdapter);

        // Retrieve and display patient's reserved appointments
        retrievePatientAppointments();

    }


    // Retrieve patient's booked appointments from Firebase
    private void retrievePatientAppointments() {
        // Clear the existing patientAppointmentsList
        patientAppointmentsList.clear();
        reservedAppointmentsAdapter.notifyDataSetChanged();

        // Get the current user's ID (patient ID)
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firebase for appointments reserved by the current patient
        databaseReference.orderByChild("patientId").equalTo(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Convert each DataSnapshot to an Appointment object
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                // Add the appointment to the list
                                patientAppointmentsList.add(appointment);
                            }
                        }
                        // Notify the adapter of the data change
                        reservedAppointmentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    @Override
    public void onCancelButtonClick(Appointment appointment) {
        // Remove the appointment from the database
        removeAppointmentFromDatabase(appointment);
    }


    private void removeAppointmentFromDatabase(Appointment appointment) {
        // Remove the appointment from Firebase using its unique identifier
        String appointmentId = appointment.getAppointmentId();
        databaseReference.child(appointmentId).removeValue();

        // Optionally, update the local list and notify the adapter
        patientAppointmentsList.remove(appointment);
        reservedAppointmentsAdapter.notifyDataSetChanged();
    }

    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                // Handle Home item click
                startActivity(new Intent(AppointmentsActivity.this, PatientProfileActivity.class));
                break;
            case R.id.navigation_home:
                // Handle Dashboard item click
                startActivity(new Intent(AppointmentsActivity.this, PatientHomeActivity.class));
                break;

        }
}}
