package com.example.doctor_view;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<DoctorAppointmentsAdapter.ViewHolder> {

    public interface AppointmentClickListener {
        void onAppointmentItemClick(Appointment appointment);
    }

    private AppointmentClickListener clickListener;
    private Context context;
    private List<Appointment> doctorAppointmentsList;

    public DoctorAppointmentsAdapter(Context context, List<Appointment> doctorAppointmentsList) {
        this.context = context;
        this.doctorAppointmentsList = doctorAppointmentsList;
    }

    public void setAppointmentClickListener(AppointmentClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_appointment_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = doctorAppointmentsList.get(position);

        // Set appointment details to the TextView
        String appointmentInfo = String.format(
                "%s\n\nDate: %s\nTime: %s\nHealth Care: %s\nPatient: %s",
                appointment.getAppointmentName(),
                appointment.getDate(),
                appointment.getTime(),
                appointment.getHealthCare(),
                appointment.getPatientName()
        );

        // Create a SpannableString to apply styles
        SpannableString spannableString = new SpannableString(appointmentInfo);

        // Find the start and end indices of the appointment name in the string
        int startIndexOfAppointmentName = 0;
        int endIndexOfAppointmentName = appointment.getAppointmentName().length();

        // Apply bold style to the appointment name
        StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldStyleSpan, startIndexOfAppointmentName, endIndexOfAppointmentName, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        // Set the formatted text to the TextView
        holder.appointmentItemTextView.setText(spannableString);

        // Set click listener for the appointment item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onAppointmentItemClick(appointment);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorAppointmentsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentItemTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentItemTextView = itemView.findViewById(R.id.doctorAppointmentItemTextView);

        }
    }
}
