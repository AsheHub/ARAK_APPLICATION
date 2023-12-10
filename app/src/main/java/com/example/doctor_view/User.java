package com.example.doctor_view;
import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {
    private String fullName;
    private String birthday;
    private String gender;
    private String healthCare;
    private String email;
    private String password;
    //private int userId;

    private String userType; // Add userType field
    // Appointments node under each user
     Map<String, Appointment> appointments;


    public User(){

    }
    public User(String fullName,String birthday,String gender, String healthCare, String email,String password) {

        this.fullName=fullName;
        this.birthday=birthday;
        this.gender=gender;
        this.healthCare = healthCare;
        this.email = email;
        this.password = password;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHealthCare(String healthCare) {
        this.healthCare = healthCare;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFullName() {return fullName;}


    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public String getHealthCare() {
        return healthCare;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }


    public void setAppointments(Map<String, Appointment> appointments) {
        this.appointments = appointments;
    }

    public Map<String, Appointment> getAppointments() {
        return appointments;
    }

}


