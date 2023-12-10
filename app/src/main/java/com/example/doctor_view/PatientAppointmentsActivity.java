package com.example.doctor_view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PatientAppointmentsActivity extends AppCompatActivity  implements PatientAppointmentsAdapter.AppointmentClickListener {

    private Spinner healthCareSpinner;
     ListView appointmentsListView;
    private DatabaseReference databaseReference;
     DatabaseReference availableAppointmentsReference;
     DatabaseReference reservedAppointmentsReference;
    private List<Appointment> appointmentList;

    private PatientAppointmentsAdapter appointmentAdapter;

    String patientId; 
    Appointment newAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation2);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Appointments");
        availableAppointmentsReference = databaseReference.child("Available Appointments");
        reservedAppointmentsReference = databaseReference.child("Reserved Appointments");



        // Get the current user's ID (patient ID)
        patientId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newAppointment=new Appointment();
        String appointmentId = reservedAppointmentsReference.push().getKey();
        newAppointment.setAppointmentId(appointmentId);

        // Initialize appointmentList and adapter
        appointmentList = new ArrayList<>();
        appointmentAdapter = new PatientAppointmentsAdapter(this, appointmentList);

        // Set the click listener on the adapter
        appointmentAdapter.setAppointmentClickListener(this);

        // Check and set the user's booking status
        checkUserBookingStatus();

        // Set up the healthCare Spinner
        healthCareSpinner = findViewById(R.id.healthCareSpinner);
        ArrayAdapter<CharSequence> healthCareAdapter = ArrayAdapter.createFromResource(
                this, R.array.health_care_array, android.R.layout.simple_spinner_item);
        healthCareAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        healthCareSpinner.setAdapter(healthCareAdapter);

        // Set up the ListView to display appointments
        appointmentsListView = findViewById(R.id.appointmentsListView);
        appointmentsListView.setAdapter(appointmentAdapter);


        // Set a listener for item selection in the healthCare Spinner
        healthCareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get selected healthCare from the spinner
                String selectedHealthCare = healthCareSpinner.getSelectedItem().toString();

                // Retrieve and display available appointments based on the selected healthCare
                retrieveAvailableAppointments(selectedHealthCare);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case when nothing is selected
            }
        });

        // Set a click listener for the ListView items
        appointmentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected appointment
                Appointment selectedAppointment = appointmentList.get(position);

                // Check if the appointment is already booked
                if (!selectedAppointment.isBooked()) {
                    // Implement the booking process
                    bookAppointment(selectedAppointment);
                    // Show the confirmation dialog
                    showConfirmationDialog();

                } else {
                    Toast.makeText(PatientAppointmentsActivity.this, "Appointment is already booked", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // Retrieve appointments based on selected healthCare
    private void retrieveAvailableAppointments(String selectedHealthCare) {
        // Clear the existing appointments
        appointmentList.clear();
        appointmentAdapter.notifyDataSetChanged();
        // Query Firebase for available appointments based on the selected healthCare
        databaseReference.child("Available Appointments")
                .orderByChild("healthCare").equalTo(selectedHealthCare)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Appointment> availableAppointments = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Convert each DataSnapshot to an Appointment object
                            Appointment appointment = snapshot.getValue(Appointment.class);

                            if (appointment != null && !appointment.isBooked()) {
                                // Add only unbooked appointments to the list
                                availableAppointments.add(appointment);
                            }
                        }
                        // Sort the list by date (assuming date is in the format "yyyy-MM-dd")
                        sortAppointmentsByDate(availableAppointments);
                        // Add the sorted appointments to the main list
                        appointmentList.addAll(availableAppointments);

                        // Notify the adapter of the data change
                        appointmentAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        Log.e("PatientAppointments", "Error retrieving appointments", databaseError.toException());
                    }
                });
    }

    private void sortAppointmentsByDate(List<Appointment> appointments) {
        // Sort appointments by date
        appointments.sort(Comparator.comparing(appointment -> appointment.getDate()));
    }
    // Method to handle book button click from the adapter
    @Override
    public void onBookButtonClick(Appointment appointment) {

        bookAppointment(appointment);
        //showConfirmationDialog();
    }
    // Book an appointment and update the database
    private void bookAppointment(Appointment appointment) {
        // Check if the user has already booked an appointment
        if (userHasReservedAppointment()) {
            // Display a message indicating that the user has already booked another appointment
            Toast.makeText(this, "You have already booked another appointment", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the appointment is already booked
        if (!appointment.isBooked()) {
            // Get the health care provider and date for the appointment
            String healthCare = appointment.getHealthCare();
            String date = appointment.getDate();

            // Check if the user has already booked an appointment with the specified health care provider and date
            if (userHasBookedAppointmentWithHealthCareAndDate(healthCare, date)) {
                // Display a message indicating that the user has already booked an appointment for this date and health care provider
                Toast.makeText(this, "You have already booked an appointment for this date and health care provider", Toast.LENGTH_SHORT).show();
                return;
            }
            String reservedAppointmentKey = reservedAppointmentsReference.push().getKey();
            // Retrieve the patient name from Firebase based on the patient ID
            DatabaseReference patientNameRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(patientId).child("patient_FullName");
            patientNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String patientName = snapshot.getValue(String.class);

                    // Save the booked appointment to the "Reserved Appointments" node
                    appointment.setBooked(true);
                    appointment.setPatientHasCome(false);
                    appointment.setPatientId(patientId);
                    // Set patient name in the Appointment object
                    appointment.setPatientName(patientName);
                    appointment.setAppointmentId(reservedAppointmentKey); // Set the generated appointment ID
                    reservedAppointmentsReference.child(reservedAppointmentKey).setValue(appointment);

                    appointment.setAppointmentId(reservedAppointmentKey); // Set the generated appointment ID


                    getDoctorFCMToken(appointment.getDoctorId(), doctorFCMToken -> {
                        // Notify the doctor with an FCM notification
                        sendFCMNotificationToDoctor(doctorFCMToken);
                    });

                    // Remove the booked appointment from the "Available Appointments" node
                    DatabaseReference availableAppointmentsRef = databaseReference.child("Available Appointments");
                    availableAppointmentsRef.orderByChild("date").equalTo(appointment.getDate())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Appointment availableAppointment = snapshot.getValue(Appointment.class);
                                        if (availableAppointment != null && !availableAppointment.isBooked()) {
                                            // Check if the available appointment matches the booked appointment
                                            if (availableAppointment.getHealthCare().equals(healthCare)
                                                    && availableAppointment.getDoctorId().equals(appointment.getDoctorId())) {
                                                // Remove the available appointment
                                                snapshot.getRef().removeValue();
                                                break;
                                            }

                                        }

                                    }

                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle errors
                                    Log.e("PatientAppointments", "Error retrieving available appointments", databaseError.toException());
                                }
                            });



                    // Update the UI to reflect the booked appointment
                    updateUIOnAppointmentBooking(appointment);
                    saveAppointmentToMyAppointments(appointment);
                    Toast.makeText(PatientAppointmentsActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("PatientAppointments", "Error retrieving patient name", error.toException());

                }
            });

            Toast.makeText(PatientAppointmentsActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PatientAppointmentsActivity.this, "Appointment is already booked", Toast.LENGTH_SHORT).show();
        }
    }


    // Save appointment information to My Appointments activity
    private void saveAppointmentToMyAppointments(Appointment appointment) {
        Intent intent = new Intent(this, AppointmentsActivity.class);
        intent.putExtra("APPOINTMENT_ID", appointment.getAppointmentId());
        startActivity(intent);
    }

    // Check if the user has already booked an appointment with the specified health care provider and date
    private boolean userHasBookedAppointmentWithHealthCareAndDate(String healthCare, String date) {
        for (Appointment bookedAppointment : appointmentList) {
            if (bookedAppointment.isBooked() &&
                    bookedAppointment.getPatientId().equals(patientId) &&
                    bookedAppointment.getHealthCare().equals(healthCare) &&
                    bookedAppointment.getDate().equals(date)) {
                return true;
            }
        }
        return false;
    }

    // Check if the user has already reserved an appointment
    private boolean userHasReservedAppointment() {
        // Check if the user has already reserved an appointment
        for (Appointment reservedAppointment : appointmentList) {
            if (reservedAppointment.isBooked() && reservedAppointment.getPatientId().equals(patientId)) {
                return true;
            }
        }
        return false;
    }
    private void updateUIOnAppointmentBooking(Appointment appointment) {
        // Iterate through the appointmentList to find the booked appointment
        for (int i = 0; i < appointmentList.size(); i++) {
            if (appointmentList.get(i).equals(appointment)) {
                // Update the status of the booked appointment
                appointmentList.get(i).setBooked(true);

                // Update the UI to reflect the changes
                appointmentAdapter.notifyDataSetChanged();

                // Dynamically update UI elements based on the booking status
                updateAdditionalUI(appointmentList.get(i), i);

                break; // Break out of the loop once the appointment is found and updated
            }
        }
    }

    private void updateAdditionalUI(Appointment appointment, int position) {
        // Example: Change the text color of appointmentItemTextView based on the booking status
        TextView appointmentItemTextView = appointmentsListView.getChildAt(position).findViewById(R.id.appointmentItemTextView);
        if (appointment.isBooked()) {
           appointmentItemTextView.setTextColor(getResources().getColor(R.color.bookedAppointmentTextColor));
        } else {
           appointmentItemTextView.setTextColor(getResources().getColor(R.color.availableAppointmentTextColor));
        }

        // Add more dynamic UI updates based on your design
    }


    // Show the confirmation dialog
    private void showConfirmationDialog() {
        // Inflate the confirmation layout
        View confirmationView = getLayoutInflater().inflate(R.layout.confirmation_layout, null);
        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(confirmationView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void checkUserBookingStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference reservedAppointmentsRef = FirebaseDatabase.getInstance().getReference("Reserved Appointments");
            reservedAppointmentsRef.orderByChild("patientId").equalTo(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean hasBookedAppointment = snapshot.exists();
                            updateBookingFunctionality(hasBookedAppointment);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }
    private void updateBookingFunctionality(boolean hasBookedAppointment) {
        Button bookButton = findViewById(R.id.bookButton);
        if (bookButton != null) {
            bookButton.setEnabled(!hasBookedAppointment);
        }
    }


    // Modify the getDoctorFCMToken method to accept a callback
    private void getDoctorFCMToken(String doctorId, Consumer<String> callback) {
        DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference().child("Doctors");

        doctorsRef.child(doctorId).child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String doctorFCMToken = dataSnapshot.getValue(String.class);

                if (doctorFCMToken != null) {
                    // Invoke the callback with the FCM token
                    callback.accept(doctorFCMToken);
                } else {
                    Log.e("PatientAppointments", "Doctor's FCM token is null for doctorId: " + doctorId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PatientAppointments", "Error retrieving doctor's FCM token", databaseError.toException());
            }
        });
    }

    // Adjust the sendFCMNotificationToDoctor method to accept a String (FCM token)
    private void sendFCMNotificationToDoctor(String doctorFCMToken) {
        // Construct the FCM message
        Map<String, String> data = new HashMap<>();
        data.put("title", "New Appointment Booking");
        data.put("body", "A patient has booked an appointment.");

        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(doctorFCMToken)
                .setData(data)
                .build());
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

    }}
