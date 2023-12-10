package com.example.doctor_view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DoctorAppointmentsActivity extends AppCompatActivity implements DoctorAppointmentsAdapter.AppointmentClickListener {

    private RecyclerView doctorAppointmentsRecyclerView;
    private DoctorAppointmentsAdapter doctorAppointmentsAdapter;
    private List<Appointment> doctorAppointmentsList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointments);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });
        doctorAppointmentsRecyclerView = findViewById(R.id.doctorAppointmentsRecyclerView);
        doctorAppointmentsList = new ArrayList<>();
        doctorAppointmentsAdapter = new DoctorAppointmentsAdapter(this, doctorAppointmentsList);
        doctorAppointmentsAdapter.setAppointmentClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        doctorAppointmentsRecyclerView.setLayoutManager(layoutManager);
        doctorAppointmentsRecyclerView.setAdapter(doctorAppointmentsAdapter);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");


        // Retrieve and display doctor's appointments
        retrieveDoctorAppointments();

    }

    // Retrieve doctor's appointments from Firebase
    private void retrieveDoctorAppointments() {
        doctorAppointmentsList.clear();
        doctorAppointmentsAdapter.notifyDataSetChanged();

        databaseReference.orderByChild("doctorId").equalTo(getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Appointment appointment = snapshot.getValue(Appointment.class);
                            if (appointment != null) {
                                doctorAppointmentsList.add(appointment);
                            }
                        }
                        doctorAppointmentsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }

    // Handle appointment item click
        @Override
        public void onAppointmentItemClick(Appointment appointment){
        // Handle the item click
           // Log.d("DoctorAppointments", "Appointment clicked: " + appointment.getAppointmentId());
            if (appointment != null) {
                // Handle the item click
                Intent intent = new Intent(DoctorAppointmentsActivity.this, PatientDetailsActivity.class);
                intent.putExtra("APPOINTMENT_ID", appointment.getAppointmentId());
                startActivity(intent);
            } else {
                Log.d("DoctorAppointments", "Appointment clicked: null");
            }
    }

    // Method to get the current user's ID (doctor ID)
    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return (currentUser != null) ? currentUser.getUid() : "";
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

        }
    }
}
