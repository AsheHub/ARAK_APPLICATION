package com.example.doctor_view;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@IgnoreExtraProperties
public class Appointment implements Serializable {


    private String appointmentName;
    private String date;
    private String time;
    private String healthCare;
    private String doctorId;
    private String doctorName;
    private String patientName;
    private String patientId; // Add this field for patient ID
    boolean booked;  // Add this field to indicate whether the appointment is booked
    boolean patientHasCome;
    String appointmentId;

    // Add fields for file information

    //private String patientId;
    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String appointmentName,String date, String time, String healthCare, String doctorId, String doctorName) {
       this.appointmentName=appointmentName;
        this.date = date;
        this.time = time;
        this.healthCare = healthCare;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.booked = false; // Initialize booked status to false

    }

    public String getAppointmentName() {
        return appointmentName;
    }

    public void setAppointmentName(String appointmentName) {
        this.appointmentName = appointmentName;
    }

    public long getDateTimeInMillis() {
        // Assuming that date and time are stored as strings
        String dateTimeString = date + " " + time;

        // Define the date and time format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            // Parse the date and time string to a Date object
            Date dateTime = dateFormat.parse(dateTimeString);

            // Get the current date without the time component
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            // Get the appointment date in milliseconds
            long appointmentDateMillis = dateTime.getTime();

            // Check if the appointment date is today or in the future
            if (appointmentDateMillis >= currentDate.getTimeInMillis()) {
                // Return the appointment date in milliseconds for upcoming or current appointments
                return appointmentDateMillis;
            } else {
                // Return 0 for past appointments
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // Handle the parse exception, return a default value, or throw an exception as needed
            return 0;
        }
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setHealthCare(String healthCare) {
        this.healthCare = healthCare;
    }


    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getHealthCare() {
        return healthCare;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    // Getter and Setter methods for other fields

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    // Method to book the appointment
    public void bookAppointment(String patientId, String patientName) {
        if (!booked) {
            this.patientId = patientId;
            this.patientName = patientName; // Set the patient name
            this.booked = true;
        }
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isPatientHasCome() {
        return patientHasCome;
    }

    public void setPatientHasCome(boolean patientHasCome) {
        this.patientHasCome = patientHasCome;
    }
}