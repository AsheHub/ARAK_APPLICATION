package com.example.doctor_view;

// AppointmentCompleted.java
public class AppointmentCompleted {
    private String appointmentId;
    private boolean booked;
    private String date;
    private long dateTimeInMillis;
    private String doctorId;
    private String doctorName;
    private String healthCare;
    private boolean patientHasCome;
    private String patientId;
    private String patientName;
    private String time;

    // Add getters and setters

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateTimeInMillis() {
        return dateTimeInMillis;
    }

    public void setDateTimeInMillis(long dateTimeInMillis) {
        this.dateTimeInMillis = dateTimeInMillis;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHealthCare() {
        return healthCare;
    }

    public void setHealthCare(String healthCare) {
        this.healthCare = healthCare;
    }

    public boolean isPatientHasCome() {
        return patientHasCome;
    }

    public void setPatientHasCome(boolean patientHasCome) {
        this.patientHasCome = patientHasCome;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

