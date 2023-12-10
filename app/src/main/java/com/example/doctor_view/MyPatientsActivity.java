package com.example.doctor_view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyPatientsActivity extends AppCompatActivity {

    private ListView listView;
    private AppointmentCompleted_ListAdapter appointmentAdapter;
    private ArrayList<Appointment> appointmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });


        // Initialize the views
        listView = findViewById(R.id.listView);

        // Initialize the ArrayList to store appointments
        appointmentList = new ArrayList<>();

        // Initialize the adapter
        appointmentAdapter = new AppointmentCompleted_ListAdapter(this, appointmentList);

        // Set the adapter to the ListView
        listView.setAdapter(appointmentAdapter);

        // Get a reference to the "Appointments" node in the Firebase database
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference().child("Appointments").child("Appointment completed");

        // Query the database to retrieve appointments
        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing list
                appointmentList.clear();

                // Iterate through the dataSnapshot to get each appointment
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    // Convert the appointment data to the Appointment class
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);

                    // Add the appointment to the list
                    appointmentList.add(appointment);
                }

                // Notify the adapter that the data has changed
                appointmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click here
                if (appointmentAdapter != null) {
                String appointmentId = appointmentAdapter.getItem(position).getAppointmentId();
                navigateToPatientDetails(appointmentId);
            }}
        });
    }
    private void navigateToPatientDetails(String appointmentId) {
        Intent intent = new Intent(MyPatientsActivity.this, MyPatientDetailsActivity.class);
        intent.putExtra("APPOINTMENT_ID", appointmentId);
        startActivity(intent);
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
