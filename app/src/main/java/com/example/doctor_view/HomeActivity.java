package com.example.doctor_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView upcomingAppointmentTextView;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewUpcomingAppointments;
    private UpcomingAppointmentsDoctorAdapter appointmentsAdapter;
    private List<Appointment> upcomingAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        // Initialize RecyclerView and its adapter
        recyclerViewUpcomingAppointments = findViewById(R.id.recyclerViewUpcomingAppointments1);
        recyclerViewUpcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        upcomingAppointments = new ArrayList<>();
        appointmentsAdapter = new UpcomingAppointmentsDoctorAdapter(upcomingAppointments);
        recyclerViewUpcomingAppointments.setAdapter(appointmentsAdapter);

        // Center buttons
        Button buttonMakeSchedule = findViewById(R.id.buttonMakeSchedule);
        Button buttonAppointment = findViewById(R.id.buttonAppointment);
        Button buttonPatient = findViewById(R.id.buttonPatient);

        // Set click listeners for center buttons
        buttonMakeSchedule.setOnClickListener(view -> {
            // Navigate to the Make Schedule page
            // Open the profile activity
            Intent intent = new Intent(HomeActivity.this, MakeSchedule.class);
            startActivity(intent);
        });

        buttonAppointment.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DoctorAppointmentsActivity.class);
            startActivity(intent);
            // Navigate to the Appointment page
        });

        buttonPatient.setOnClickListener(view -> {
            // Navigate to the Patient page
            Intent intent = new Intent(HomeActivity.this, MyPatientsActivity.class);
            startActivity(intent);
        });

        // Assuming you have the TextView in your layout with ID upcomingAppointmentInfo
        upcomingAppointmentTextView = findViewById(R.id.upcomingAppointmentInfo1);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Appointments");

        // Create the adapter and set it to the RecyclerView
        UpcomingAppointmentsAdapter adapter = new UpcomingAppointmentsAdapter(upcomingAppointments);
        recyclerViewUpcomingAppointments.setAdapter(adapter);

        // Assuming you have LinearLayoutManager in your layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewUpcomingAppointments.setLayoutManager(layoutManager);

        // Display upcoming appointment on the home page
        displayUpcomingAppointments();
    }

    private void displayUpcomingAppointments() {
        String doctorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.orderByChild("doctorId").equalTo(doctorId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        upcomingAppointments.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null && isAppointmentUpcoming(appointment)) {
                                upcomingAppointments.add(appointment);
                            }
                        }

                        appointmentsAdapter.notifyDataSetChanged();

                        // Update the visibility of the TextView
                        if (upcomingAppointments.isEmpty()) {
                            // If no upcoming appointments, hide the RecyclerView and show the TextView
                            recyclerViewUpcomingAppointments.setVisibility(View.GONE);
                            upcomingAppointmentTextView.setVisibility(View.VISIBLE);
                            upcomingAppointmentTextView.setText("NO UPCOMING APPOINTMENT");
                        } else {
                            // If there are upcoming appointments, show the RecyclerView and hide the TextView
                            recyclerViewUpcomingAppointments.setVisibility(View.VISIBLE);
                            upcomingAppointmentTextView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    private boolean isAppointmentUpcoming(Appointment appointment) {
        // Get the milliseconds for the appointment time
        long appointmentTimeInMillis = appointment.getDateTimeInMillis();

        // Check if the appointment is upcoming
        return appointmentTimeInMillis > System.currentTimeMillis();
    }

    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                // Handle Profile item click
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.navigation_home:
                // Handle Home item click
                // No need to start the same activity again
                break;
            case R.id.navigation_clender:
                // Handle Calendar item click
                startActivity(new Intent(this, ViewScheduleActivity.class));
                break;
        }
    }
}