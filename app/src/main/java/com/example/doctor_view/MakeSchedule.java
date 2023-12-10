package com.example.doctor_view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MakeSchedule extends AppCompatActivity {
    public static final int MODIFY_APPOINTMENT_REQUEST = 1;
    private DatePicker datePicker;
    private TimePicker timePicker;
     TextView examinationAppointmentText;
    RecyclerView recyclerView;
    List<Appointment> appointmentList;
    ScheduleAdapter scheduleAdapter;

     DatabaseReference availableAppointmentsReference;
     DatabaseReference reservedAppointmentsReference;

     String userId;  // You need to set this with the user ID when the user logs in

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_schedule);



        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        Button addButton = findViewById(R.id.addButton);
        Button saveButton = findViewById(R.id.saveButton);


        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        availableAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Available Appointments");
        reservedAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Reserved Appointments");


        recyclerView = findViewById(R.id.recyclerView);
        appointmentList = new ArrayList<>();


        // Create an instance of ScheduleAdapter.AppointmentKeyCallback
        ScheduleAdapter.AppointmentKeyCallback keyCallback = new ScheduleAdapter.AppointmentKeyCallback() {
            @Override
            public void onKeyReceived(String key) {
                // Handle the key callback if needed
                // For example, you can perform additional actions when the key is received
            }
        };

        scheduleAdapter = new ScheduleAdapter(this,availableAppointmentsReference,appointmentList, keyCallback);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(scheduleAdapter);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {addAppointment();}
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchedule();
            }
        });
    }



    private void saveSchedule() {

        Toast.makeText(this, "Schedule Saved!", Toast.LENGTH_SHORT).show();
        // Start a new activity to display the schedule
        Intent intent = new Intent(MakeSchedule.this, HomeActivity.class);
        startActivity(intent);
    }
    // Inside the MakeSchedule class
    private void addAppointment() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1; // Month is zero-based
        int year = datePicker.getYear();

        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute);

        // Check if the selected date is in the past
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            Toast.makeText(this, "Cannot create appointment for past days", Toast.LENGTH_SHORT).show();
            return; // Stop further processing
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(calendar.getTime());

       // String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = sdfTime.format(calendar.getTime());

        String examinationAppointmentText1= "Examination Appointment";
        // Get the currently logged-in user's ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if the appointment already exists in available or reserved appointments
        if (isDuplicateAppointment(formattedDate, formattedTime, userId)) {
            Toast.makeText(this, "You already have an appointment at this time", Toast.LENGTH_SHORT).show();
            return; // Stop further processing
        }
        // Retrieve user information from Firebase Realtime Database
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User data found
                    User user = dataSnapshot.getValue(User.class);
                    // Check if the user has healthCare information
                    if (user != null && user.getHealthCare() != null &&user.getFullName() != null) {
                        String healthCare = user.getHealthCare();
                        String doctorName = user.getFullName();
                        // Create an Appointment object with healthCare information
                        Appointment appointment = new Appointment(examinationAppointmentText1 ,formattedDate, formattedTime,healthCare,userId,doctorName);
                        // Update the local list and notify the adapter
                        appointmentList.add(appointment);
                        scheduleAdapter.notifyDataSetChanged();
                        // Save the appointment to Available Appointments in Firebase
                        String appointmentKey = availableAppointmentsReference.push().getKey();
                        availableAppointmentsReference.child(appointmentKey).setValue(appointment);


                        Toast.makeText(MakeSchedule.this, "Schedule Saved!", Toast.LENGTH_SHORT).show();

                    } else {
                        // Handle the case when healthCare information is not available
                        Toast.makeText(MakeSchedule.this, "HealthCare information not available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case when user data is not available
                    Toast.makeText(MakeSchedule.this, "User data not available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
                Toast.makeText(MakeSchedule.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to check if the appointment already exists
    private boolean isDuplicateAppointment(String date, String time, String doctorId) {
        for (Appointment appointment : appointmentList) {
            if (appointment.getDate().equals(date) && appointment.getTime().equals(time) && appointment.getDoctorId().equals(doctorId)) {
                return true; // Appointment already exists
            }
        }
        return false; // Appointment does not exist
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MODIFY_APPOINTMENT_REQUEST && resultCode == RESULT_OK && data != null) {
            // Retrieve the modified appointment and position
            Appointment modifiedAppointment = (Appointment) data.getSerializableExtra("modifiedAppointment");
            int position = data.getIntExtra("position", -1);

            if (position != -1) {
                // Update the appointment in the list and notify the adapter
                appointmentList.set(position, modifiedAppointment);
                scheduleAdapter.notifyItemChanged(position);
            }
        }
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
