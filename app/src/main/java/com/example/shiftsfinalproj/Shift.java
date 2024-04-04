package com.example.shiftsfinalproj;

public class Shift {
    public String startTime;
    public String endTime;
    public String role;
    public String userid;

    public Shift() {
        // Default constructor required for Firebase
    }

    public Shift(String startTime, String endTime, String role,String userid) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.role = role;
        this.userid = userid;
    }

    // Getters and setters
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
