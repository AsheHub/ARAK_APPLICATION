package com.example.doctor_view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class MyReportActivity extends AppCompatActivity {

    private DatabaseReference filesReference; // Reference to the "Medical Report" node
    private TextView fileDetailsTextView;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Context context;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_report);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleBottomNavigationItemSelected(item);
            return true; // Return true if the item selection has been handled
        });

        // Add these lines to initialize Firebase Storage and related variables
        storage = FirebaseStorage.getInstance();
        context = this; // Save the context for later use
        imageView = findViewById(R.id.imageView); // Replace with your ImageView ID

        fileDetailsTextView = findViewById(R.id.fileDetailsTextView);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        // The user is signed in
        String patientId = user.getUid();
        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        filesReference = firebaseDatabase.getReference().child("Medical Report").child(patientId);
        storageReference = storage.getReference("Medical Report");




        // Retrieve and display files for the currently logged-in patient
        retrieveAndDisplayFiles();
    }

    private void retrieveAndDisplayFiles() {
        // Dynamically retrieve the currently logged-in user's ID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // The user is signed in
            String patientId = user.getUid();
            Log.d("MyReportActivity", "Patient ID: " + patientId); // Log patientId for debugging

            filesReference.orderByChild("patientId").equalTo(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("MyReportActivity", "DataSnapshot for Files: " + dataSnapshot.toString());

                    if (dataSnapshot.exists()) {
                        for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                            FileInformation fileInformation = fileSnapshot.getValue(FileInformation.class);

                            if (fileInformation != null) {
                                Log.d("MyReportActivity", "File Name: " + fileInformation.getFileName());
                                Log.d("MyReportActivity", "Appointment Date: " + fileInformation.getDate());
                                Log.d("MyReportActivity", "Appointment Time: " + fileInformation.getTime());
                                // Display other file details as needed
                                fileDetailsTextView.setText("File Name: " + fileInformation.getFileName() + "\n" +"Appointment Date: " + fileInformation.getDate() + "\n" + "Appointment Time: " + fileInformation.getTime()+ "\n" + "Patient Name: " + fileInformation.getPatientName());
                                // Display other file details as needed

                                fileDetailsTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Handle the click event, for example, navigate to the source page
                                        downloadAndDisplayFile(fileInformation);

                                    }
                                });
                            }
                        }
                    } else {
                        Log.d("MyReportActivity", "Files data does not exist for patient ID: " + patientId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                    Log.e("MyReportActivity", "Error retrieving files", databaseError.toException());
                }
            });
        }
    }

    // Update the downloadAndDisplayFile method to accept a FileInformation parameter
    private void downloadAndDisplayFile(FileInformation fileInformation) {
        // Create a reference to the file
       // StorageReference fileRef = storageReference.child(fileInformation.getFileName());
        // Create a direct path based on patient ID and file name
        String filePath = fileInformation.getAppointmentId() + "/" + fileInformation.getFileName();

        // Create a reference to the file
        StorageReference fileRef = storageReference.child(filePath);

        // Download the file only if it exists
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use the downloaded URI to display the file
                Log.d("MyReportActivity", "Downloaded URI: " + uri.toString());

                // Display the file, e.g., in an ImageView
                Glide.with(context)
                        .load(uri)
                        .into(imageView);

                // For other types of files, consider opening them through appropriate apps using an Intent
                // Example: open PDF files using a PDF viewer app
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String mimeType = getMimeType(fileInformation.getFileName());
                intent.setDataAndType(uri, mimeType);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors that may occur during the download
                Log.e("MyReportActivity", "File download failed: " + exception.getMessage());

                // Additional logging for debugging
                Log.e("MyReportActivity", "File path: " + fileRef.getPath());
                Log.e("MyReportActivity", "Firebase Storage path: " + storageReference.child(fileInformation.getFileName()).getPath());
            }
        });
    }


    // Helper method to get MIME type based on file extension
    private String getMimeType(String fileName) {
        String fileExtension = getFileExtension(fileName);
        if (fileExtension != null) {
            switch (fileExtension) {
                case "pdf":
                    return "application/pdf";
                // Add more cases for other file types
                default:
                    return "*/*"; // Default MIME type for unknown file types
            }
        }
        return "*/*"; // Default MIME type for unknown file types
    }

    // Helper method to get the file extension
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
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


