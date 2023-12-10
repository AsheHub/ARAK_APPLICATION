package com.example.doctor_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class AppointmentCompleted_ListAdapter extends ArrayAdapter<Appointment> {
     Context context;
     ArrayList<Appointment> appointmentsList;

    public AppointmentCompleted_ListAdapter(Context context, ArrayList<Appointment> appointmentsList) {
        super(context, 0, appointmentsList);
        this.context = context;
        this.appointmentsList = appointmentsList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Implement your view holder pattern here
        // Use convertView and set data from the AppointmentCompleted object
        // Example:
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_patient_appointment_completed, parent, false);
        }

        Appointment appointment = getItem(position);

        TextView textViewPatientName = convertView.findViewById(R.id.patientNameTextView);
        TextView textViewDate = convertView.findViewById(R.id.dateTextView);
        TextView textViewTime = convertView.findViewById(R.id.timeTextView);


        if (appointment != null) {

            textViewPatientName.setText(appointment.getPatientName());
            textViewDate.setText(appointment.getDate());
            textViewTime.setText(appointment.getTime());
        }

        return convertView;
    }
}
