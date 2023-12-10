package com.example.doctor_view;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
public class SelectUniversityActivity extends AppCompatActivity {

    private Spinner universitySpinner;
    private Button submitButton;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_university);

        universitySpinner = findViewById(R.id.universitySpinner);
        submitButton = findViewById(R.id.submitButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.universities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        universitySpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedUniversity = universitySpinner.getSelectedItem().toString();
                Intent intent = new Intent(SelectUniversityActivity.this, SecondActivity.class);
                intent.putExtra("university", selectedUniversity);
                startActivity(intent);
            }
        });

    }

}
