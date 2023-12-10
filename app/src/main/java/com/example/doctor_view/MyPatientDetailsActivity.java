package com.example.doctor_view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class MyPatientDetailsActivity extends AppCompatActivity {

    private static final int FILE_PICK_REQUEST = 1; // Choose any unique integer
    // Firebase Storage
    FirebaseStorage storage;
    StorageReference storageReference;

    private TextView patientDetailsTextView;
    private TextView appointmentDetailsTextView;

    ImageView imageUpload;
    DatabaseReference completedAppointmentsReference;
    DatabaseReference patientsReference; // New reference for patient information

    DatabaseReference usersReference;
    DatabaseReference filesReference; // Added for storing file information
     String patientPhoneNumber;  // Variable to store patient's phone number


    Button uploadButton;

    String appointmentId;

    Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patient_details);


        patientDetailsTextView = findViewById(R.id.patientDetailsTextView);
        appointmentDetailsTextView = findViewById(R.id.appointmentDetailsTextView);
        imageUpload = findViewById(R.id.imageUpload);
        uploadButton = findViewById(R.id.uploadButton);


        // Initialize Firebase
        // Initialize your Firebase components

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        completedAppointmentsReference = firebaseDatabase.getReference().child("Appointments").child("Appointment completed");
        patientsReference = firebaseDatabase.getReference().child("Patients"); // New reference
        usersReference = firebaseDatabase.getReference().child("users");
        filesReference = FirebaseDatabase.getInstance().getReference().child("Medical Report");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference().child("Medical Report");


        // Retrieve the appointment ID from the Intent
        appointmentId = getIntent().getStringExtra("APPOINTMENT_ID");


        // Retrieve and display appointment information
        retrieveAndDisplayAppointmentInformation(appointmentId);
        // Retrieve and display patient information
        retrieveAndDisplayPatientInformation(appointmentId);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Allow all file types
                startActivityForResult(intent, FILE_PICK_REQUEST);
            }
        });

        Button buttonOpenWebsite = findViewById(R.id.chat);

        buttonOpenWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });

    }




    // Modify the openWhatsApp method to use the retrieved patientPhoneNumber
    private void openWhatsApp() {
        if (patientPhoneNumber != null) {
            // Create a Uri object with the WhatsApp API link
            Uri uri = Uri.parse("https://wa.me/" + patientPhoneNumber);

            // Create an Intent with the ACTION_VIEW action and the Uri
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            // Check if there is an app that can handle this intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the activity
                startActivity(intent);
            } else {
                // WhatsApp is not installed, display a message or take alternative action
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where patientPhoneNumber is not available
            Toast.makeText(this, "Patient's phone number not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {

            fileUri = data.getData();

            if (fileUri != null) {
                // Display the selected image in the ImageView
                Picasso.get().load(fileUri).into(imageUpload);
                uploadFileToStorage(fileUri, appointmentId);

            }
        }
    }
// Upload the selected file to Firebase Storage

    private void uploadFileToStorage(Uri fileUri, String appointmentId) {
        // Get the file extension
        String fileExtension = getFileExtension(fileUri);

        // Create a unique file name using the appointment ID
        String fileName = "Medical_Report." + fileExtension;
        String filePath = appointmentId + "/" + fileName;

        // Retrieve appointment information
        completedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract details of the completed appointment
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);

                    if (appointment != null) {
                        String patientId = appointment.getPatientId();
                        String doctorId = appointment.getDoctorId();

                        // Retrieve patient information
                        patientsReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    patientUser patient = dataSnapshot.getValue(patientUser.class);

                                    if (patient != null) {
                                        // Retrieve doctor information
                                        usersReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    User doctor = dataSnapshot.getValue(User.class);

                                                    if (doctor != null) {
                                                        // Now, you have all the information needed
                                                        // Create a reference to 'Medical Report/appointmentId_filename'
                                                        //StorageReference fileReference = storageReference.child(patientId).child(appointmentId);
                                                        StorageReference fileReference = storageReference.child(filePath);

                                                        // Upload the file to Firebase Storage
                                                        fileReference.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        // File uploaded successfully
                                                                        Toast.makeText(MyPatientDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                                        // Save file information in the database
                                                                        saveFileInformationInDatabase(patientId,fileName ,filePath, appointmentId, doctorId, appointment.getDate(), appointment.getTime(), appointment.getHealthCare(), patient.getPatient_FullName(), doctor.getFullName());
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        // Handle unsuccessful uploads
                                                                        Toast.makeText(MyPatientDetailsActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        Log.d("MyPatientDetailsActivity", "Doctor is null.");
                                                    }
                                                } else {
                                                    Log.d("MyPatientDetailsActivity", "Doctor data does not exist.");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // Handle errors
                                                Log.e("MyPatientDetailsActivity", "Error retrieving doctor data: " + databaseError.getMessage());
                                            }
                                        });
                                    } else {
                                        Log.d("MyPatientDetailsActivity", "Patient is null.");
                                    }
                                } else {
                                    Log.d("MyPatientDetailsActivity", "Patient data does not exist.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors
                                Log.e("MyPatientDetailsActivity", "Error retrieving patient data: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Log.d("MyPatientDetailsActivity", "Appointment is null.");
                    }
                } else {
                    Log.d("MyPatientDetailsActivity", "Appointment data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("MyPatientDetailsActivity", "Error retrieving appointment data: " + databaseError.getMessage());
            }
        });

    }

    // Save file information in the database
    private void saveFileInformationInDatabase(String patientId, String fileName,String filePath ,String appointmentId, String doctorId, String appointmentDate, String appointmentTime, String healthCare, String patientName, String doctorName) {
        DatabaseReference filesReference = FirebaseDatabase.getInstance().getReference().child("Medical Report").child(patientId).child(appointmentId);
        String fileId = filesReference.push().getKey();
        FileInformation fileInformation = new FileInformation();
        fileInformation.setFileId(fileId);
        fileInformation.setFileName(fileName);
        fileInformation.setFilePath(filePath);
        fileInformation.setPatientId(patientId);
        fileInformation.setAppointmentId(appointmentId);
        fileInformation.setDoctorId(doctorId);
        fileInformation.setDate(appointmentDate);
        fileInformation.setTime(appointmentTime);
        fileInformation.setHealthCare(healthCare);
        fileInformation.setPatientName(patientName);
        fileInformation.setDoctorName(doctorName);
        // Set other details as needed
        filesReference.setValue(fileInformation);
    }

    // Get the file extension from the URI
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }



//ends of upload file part

    //Here is information of appointment and patient
    private void retrieveAndDisplayAppointmentInformation(String appointmentId) {
        completedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract details of the completed appointment
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);

                    if (appointment != null) {
                        // Display appointment details
                        String appointmentDetails = "Appointment Details:\n" +
                                "Date: " + appointment.getDate() + "\n" +
                                "Time: " + appointment.getTime() + "\n" +
                                "Doctor: " + appointment.getDoctorName() + "\n" +
                                // Add more fields as needed
                                "Health Care: " + appointment.getHealthCare();

                        // Set the formatted appointment details to a TextView
                        appointmentDetailsTextView.setText(appointmentDetails);


                    } else {
                        Log.d("MyPatientDetailsActivity", "Appointment is null.");
                    }
                } else {
                    Log.d("MyPatientDetailsActivity", "Appointment data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }


    private void retrieveAndDisplayPatientInformation(String appointmentId) {
        completedAppointmentsReference.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract details of the completed appointment
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);

                    if (appointment != null) {
                        String patientId = appointment.getPatientId();

                        // Retrieve and display patient information
                        patientsReference.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    patientUser patient = dataSnapshot.getValue(patientUser.class);

                                    if (patient != null) {
                                        // Display patient details
                                        // Store the patient's phone number
                                        patientPhoneNumber = patient.getPhone_Number();

                                        String patientDetails = "Patient Details:\n" +
                                                "Name: " + patient.getPatient_FullName() + "\n" +
                                                "Birthday: " + patient.getPatient_Birthday() + "\n" +
                                                "Gender: " + patient.getPatient_Gender() + "\n"+
                                                "Phone Number: " + patient.getPhone_Number() + "\n" +
                                                "Chronic Diseases: " + patient.getChronic_Diseases() + "\n"
                                                 // Add more fields as needed
                                                ; // Example field

                                        // Set the formatted patient details to a TextView
                                        patientDetailsTextView.setText(patientDetails);

                                        // Optionally, set individual fields as well
                                        //patientNameTextView.setText(patient.getPatient_FullName());
                                        //patientEmailTextView.setText(patient.getPatient_Email());
                                        //patientBirthdayTextView.setText(patient.getPatient_Birthday());
                                        //patientGenderTextView.setText(patient.getPatient_Gender());
                                        // You can add more fields as needed
                                    } else {
                                        Log.d("MyPatientDetailsActivity", "Patient is null.");
                                    }
                                } else {
                                    Log.d("MyPatientDetailsActivity", "Patient data does not exist.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle errors
                            }
                        });

                    } else {
                        Log.d("MyPatientDetailsActivity", "Appointment is null.");
                    }
                } else {
                    Log.d("MyPatientDetailsActivity", "Appointment data does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });

    }
}
