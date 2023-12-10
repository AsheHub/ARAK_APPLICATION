package com.example.doctor_view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    public interface AppointmentKeyCallback {
        void onKeyReceived(String key);
    }

    DatabaseReference databaseReference;
    List<Appointment> appointmentList;
    Context context;


    public ScheduleAdapter(Context context, DatabaseReference databaseReference, List<Appointment> appointmentList, AppointmentKeyCallback keyCallback) {
        this.context = context;
        this.databaseReference = databaseReference;
        this.appointmentList = appointmentList;

        // Add a ValueEventListener to listen for changes in the Firebase database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    appointmentList.add(appointment);
                }
                notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Appointment appointment = appointmentList.get(position);
        holder.dateTextView.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getTime());


        // Handle delete button click
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment appointment = appointmentList.get(position);

                // Get the key using the callback
                getAppointmentKey(appointment, new AppointmentKeyCallback() {
                    @Override
                    public void onKeyReceived(String key) {
                        if (key != null) {
                            // Remove the appointment locally
                            int removedPosition = appointmentList.indexOf(appointment);
                            appointmentList.remove(removedPosition);
                            notifyItemRemoved(removedPosition);

                            // Remove the appointment from Firebase
                            databaseReference.child(key).removeValue();
                        } else {
                            // Handle the case when the key is not found
                            Toast.makeText(context, "Appointment not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void getAppointmentKey(Appointment appointment, AppointmentKeyCallback callback) {
        databaseReference.orderByChild("date").equalTo(appointment.getDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    callback.onKeyReceived(snapshot.getKey());
                    return; // Stop after finding the first match
                }
                callback.onKeyReceived(null); // No match found
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onKeyReceived(null); // Error occurred
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView timeTextView;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
