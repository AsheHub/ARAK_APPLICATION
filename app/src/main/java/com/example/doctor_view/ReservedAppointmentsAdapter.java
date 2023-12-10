package com.example.doctor_view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ReservedAppointmentsAdapter extends ArrayAdapter<Appointment> {

    public interface AppointmentClickListener {
        void onCancelButtonClick(Appointment appointment);
    }

    private AppointmentClickListener clickListener;
    private Context context;
    private List<Appointment> appointmentList;

    public ReservedAppointmentsAdapter(Context context, List<Appointment> appointmentList) {
        super(context, R.layout.reserved_appointment_item, appointmentList);
        this.context = context;
        this.appointmentList = appointmentList;
    }

    public void setAppointmentClickListener(AppointmentClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        ViewHolder holder;

        if (listItem == null) {
            // Inflate the layout for each list item
            listItem = LayoutInflater.from(context).inflate(R.layout.reserved_appointment_item, parent, false);

            // Initialize the ViewHolder
            holder = new ViewHolder();
            holder.appointmentItemTextView = listItem.findViewById(R.id.appointmentItemTextView);
            holder.cancelButton = listItem.findViewById(R.id.cancelButton);

            listItem.setTag(holder);
        } else {
            holder = (ViewHolder) listItem.getTag();
        }

        Appointment currentAppointment = appointmentList.get(position);

        // Set appointment details to the TextView
        String appointmentInfo = String.format(
                "%s\nState: %s\nDate: %s\nTime: %s\nHealth Care: %s\nDoctor: %s\nPatient: %s",
                currentAppointment.getAppointmentName(),
                currentAppointment.isBooked() ? "Booked" : "Available",
                currentAppointment.getDate(),
                currentAppointment.getTime(),
                currentAppointment.getHealthCare(),
                currentAppointment.getPatientName(),
                currentAppointment.getDoctorName()
        );

        // Create a SpannableString to apply styles
        SpannableString spannableString = new SpannableString(appointmentInfo);

        // Find the start and end indices of the appointment name in the string
        int startIndexOfAppointmentName = 0;
        int endIndexOfAppointmentName = currentAppointment.getAppointmentName().length();

        // Apply bold style to the appointment name
        StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldStyleSpan, startIndexOfAppointmentName, endIndexOfAppointmentName, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Set the formatted text to the TextView
        holder.appointmentItemTextView.setText(spannableString);


        // Set click listener for the cancel button
        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the cancel button click
                if (clickListener != null) {
                    clickListener.onCancelButtonClick(currentAppointment);
                }
            }
        });

        return listItem;
    }

    // ViewHolder pattern for efficient view re-use
    static class ViewHolder {
        TextView appointmentItemTextView;
        Button cancelButton;
    }
}
