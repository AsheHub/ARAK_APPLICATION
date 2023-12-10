package com.example.doctor_view;

import android.widget.Spinner;

import java.io.Serializable;

public class patientUser implements Serializable {
    private String patient_FullName;
    private String patient_Birthday;
    private String patient_Gender;
    private String patient_Email;
    private String patient_Password;

    private String patient_Uid;

    private String userType; // Add userType field
    String phone_Number;

    public patientUser() {
    }

    public patientUser(String patient_FullName, String patient_Birthday,
                       String patient_Gender,String patient_Email, String patient_Password) {

        this.patient_FullName=patient_FullName;
        this.patient_Birthday=patient_Birthday;
        this.patient_Gender=patient_Gender;
        this.patient_Email=patient_Email;
        this.patient_Password=patient_Password;
        this.chronic_Diseases=chronic_Diseases;
        this.phone_Number=phone_Number;

    }

    public void setPatient_FullName(String patient_FullName) {
        this.patient_FullName = patient_FullName;
    }

    public void setPatient_Birthday(String patient_Birthday) {
        this.patient_Birthday = patient_Birthday;
    }

    public void setPatient_Gender(String patient_Gender) {
        this.patient_Gender = patient_Gender;
    }

    public void setPatient_Email(String patient_Email) {
        this.patient_Email = patient_Email;
    }

    public void setPatient_Password(String patient_Password) {
        this.patient_Password = patient_Password;
    }

    public String getPatient_Uid() {
        return patient_Uid;
    }

    public void setPatient_Uid(String patient_Uid) {
        this.patient_Uid = patient_Uid;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }



    public String getPatient_FullName() {
        return patient_FullName;
    }

    public String getPatient_Birthday() {
        return patient_Birthday;
    }

    public String getPatient_Gender() {
        return patient_Gender;
    }

    public String getPatient_Email() {
        return patient_Email;
    }

    public String getPatient_Password() {return patient_Password;}

    public String getUserType() {
        return userType;
    }

    public String getPhone_Number() {
        return phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        this.phone_Number = phone_Number;
    }

    String chronic_Diseases;

    public String getChronic_Diseases() {
        return chronic_Diseases;
    }

    public void setChronic_Diseases(String chronic_Diseases) {
        this.chronic_Diseases = chronic_Diseases;
    }
}
