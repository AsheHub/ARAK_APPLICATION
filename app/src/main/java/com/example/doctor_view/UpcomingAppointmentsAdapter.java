package com.example.doctor_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UpcomingAppointmentsAdapter extends RecyclerView.Adapter<UpcomingAppointmentsAdapter.ViewHolder> {
    private List<Appointment> appointments;

    public UpcomingAppointmentsAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_doctor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView upcomeTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView doctorNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            upcomeTextView = itemView.findViewById(R.id. upcomingAppointmentInfo);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            doctorNameTextView = itemView.findViewById(R.id.doctorNameTextView);
        }

        public void bind(Appointment appointment) {

            dateTextView.setText(appointment.getDate());
            timeTextView.setText(appointment.getTime());
            doctorNameTextView.setText(appointment.getDoctorName());
        }
    }
}
