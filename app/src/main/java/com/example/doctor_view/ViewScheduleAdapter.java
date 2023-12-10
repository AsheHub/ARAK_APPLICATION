package com.example.doctor_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewScheduleAdapter extends RecyclerView.Adapter<ViewScheduleAdapter.ViewHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public ViewScheduleAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.titleTextView.setText(appointment.getAppointmentName());
        holder.dateTextView.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getTime());


        // Set background color based on appointment state
        int backgroundColor = appointment.isBooked() ? ContextCompat.getColor(context, R.color.colorRed) :
                ContextCompat.getColor(context, R.color.colorBabyBlue);
        holder.itemView.setBackgroundColor(backgroundColor);


        // Convert boolean to String for display
        String state = appointment.isBooked() ? "Booked" : "Available";
        holder.stateTextView.setText(state);    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView dateTextView;
        public TextView timeTextView;
        public TextView stateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView=itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            stateTextView = itemView.findViewById(R.id.stateTextView);
        }
    }
}
