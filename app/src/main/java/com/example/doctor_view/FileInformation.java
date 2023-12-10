package com.example.doctor_view;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FileInformation {

    private String patientId;
    private String fileName;
    private String fileId;


    private String filePath;
    private String appointmentId;
    private String doctorId;

    private String date;

    private String time;
    private String healthCare;
    private String patientName;
    private String doctorName;

    public FileInformation() {
        // Default constructor required for calls to DataSnapshot.getValue(FileInformation.class)
    }

    // Add getters and setters for each attribute

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHealthCare() {
        return healthCare;
    }

    public void setHealthCare(String healthCare) {
        this.healthCare = healthCare;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
