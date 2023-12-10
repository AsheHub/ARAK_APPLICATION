package com.example.doctor_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewScheduleActivity extends AppCompatActivity {
    private RecyclerView scheduleRecyclerView;
    private ViewScheduleAdapter scheduleAdapter;
    private DatabaseReference availableAppointmentsReference;
    private DatabaseReference reservedAppointmentsReference;
    private String userId;  // You need to set this with the user ID when the user logs in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        availableAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Available Appointments");
        reservedAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");

        scheduleRecyclerView = findViewById(R.id.scheduleRecyclerView);

        // Pass an empty list or initialize it based on your requirement
        List<Appointment> allAppointmentsList = new ArrayList<>();

        // Create an instance of ViewScheduleAdapter
        scheduleAdapter = new ViewScheduleAdapter(this, allAppointmentsList);

        // Retrieve and add available appointments to the list
        retrieveAppointments(availableAppointmentsReference, allAppointmentsList);

        // Retrieve and add reserved appointments to the list
        retrieveAppointments(reservedAppointmentsReference, allAppointmentsList);

        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleRecyclerView.setAdapter(scheduleAdapter);
    }

    // Helper method to retrieve appointments from Firebase
    private void retrieveAppointments(DatabaseReference reference, List<Appointment> appointmentsList) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    appointmentsList.add(appointment);
                }
                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
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
