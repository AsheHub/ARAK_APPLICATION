package com.example.doctor_view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class PatientHomeActivity extends AppCompatActivity {

    Button myAppointments;

    TextView upcomingAppointmentTextView;
    Button btnViewAvailableDates;
    Button myReport;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewUpcomingAppointments;
    private UpcomingAppointmentsAdapter appointmentsAdapter;
    private List<Appointment> upcomingAppointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        // Check the user's booking status when the patient home activity is created
        //checkUserBookingStatus();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        // Initialize RecyclerView and its adapter
        recyclerViewUpcomingAppointments = findViewById(R.id.recyclerViewUpcomingAppointments);
        recyclerViewUpcomingAppointments.setLayoutManager(new LinearLayoutManager(this));
        upcomingAppointments = new ArrayList<>();
        appointmentsAdapter = new UpcomingAppointmentsAdapter(upcomingAppointments);
        recyclerViewUpcomingAppointments.setAdapter(appointmentsAdapter);


        //upcomingAppointmentTextView=findViewById(R.id.upcomingAppointmentInfo);

        myAppointments =findViewById(R.id.myAppointments);
        btnViewAvailableDates=findViewById(R.id.btnViewAvailableDates);
        myReport=findViewById(R.id.myReport);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");

        myAppointments.setOnClickListener(v -> {
            startActivity(new Intent(PatientHomeActivity.this, AppointmentsActivity.class));
        });
        btnViewAvailableDates.setOnClickListener(v -> {
            startActivity(new Intent(PatientHomeActivity.this, PatientAppointmentsActivity.class));
        });

        myReport.setOnClickListener(v -> {
            startActivity(new Intent(PatientHomeActivity.this, MyReportActivity.class));
        });


        // Assuming you have the RecyclerView in your layout with ID recyclerViewUpcomingAppointments
        RecyclerView recyclerViewUpcomingAppointments = findViewById(R.id.recyclerViewUpcomingAppointments);

        // Assuming you have the TextView in your layout with ID upcomingAppointmentInfo
        upcomingAppointmentTextView = findViewById(R.id.upcomingAppointmentInfo);

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
        String patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.orderByChild("patientId").equalTo(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
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
                            upcomingAppointmentTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }



    private boolean isAppointmentUpcoming(Appointment appointment) {
        // Get the milliseconds for the appointment time
        long appointmentTimeInMillis = appointment.getDateTimeInMillis();

        // Check if the appointment is upcoming
        return appointmentTimeInMillis > 0;
    }





    private void handleBottomNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                // Handle Home item click
                startActivity(new Intent(this, PatientProfileActivity.class));
                break;
            case R.id.navigation_home:
                // Handle Dashboard item click
                startActivity(new Intent(this, PatientHomeActivity.class));
                break;

        }

}
}