//package com.example.shiftsfinalproj;
//
//public class Shift {
//    public String startTime;
//    public String endTime;
//    public String role;
//    public String userid;
//
//    public Shift() {
//        // Default constructor required for Firebase
//    }
//
//    public Shift(String startTime, String endTime, String role,String userid) {
//        this.startTime = startTime;
//        this.endTime = endTime;
//        this.role = role;
//        this.userid = userid;
//    }
//
//    // Getters and setters
//    public String getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//
//    public String getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(String endTime) {
//        this.endTime = endTime;
//    }
//
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//}

package com.example.shiftsfinalproj;

import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Shift {
    String shift_id;
    private long startTimestamp;
    private long duration;
    private long endTimestamp;
    private String user_id; // Ensure this matches the field name in Firestore.
    private String role;
    String startDate;
    String endDate;
    Double hours, total, salary = 45.0;



    // Default constructor is required for Firebase's automatic data mapping.
    public Shift() {
    }
    public Shift(long startTimestamp, long endTimestamp, String user_id, String role) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.user_id = user_id;
        this.role = role;

        calculateHours();
        setDates();
        setTotal();
    }

    // Getters and setters for startTimestamp.
    public long getStartTimestamp() {
        return startTimestamp;
    }
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
        calculateHours();  // Recalculate hours if start time changes
        setDates();        // Update date strings
    }


    // Getters and setters for endTimestamp.
    public long getEndTimestamp() {
        return endTimestamp;
    }
    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
        calculateHours();  // Recalculate hours if end time changes
        setDates();        // Update date strings
    }




    public String getShiftid() {
        return shift_id;
    }
    public void setShift_id(String shift_id) {
        this.shift_id = shift_id;
        calculateHours();  // Recalculate hours if end time changes
        setDates();        // Update date strings
    }




    // Getters and setters for userId.
    public String getuser_id() {
        return user_id;
    }
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }




    // Getters and setters for role.
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }


    public void setStartDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.startDate = dateFormat.format(new Date(startTimestamp));
    }
    public void setEndDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.endDate = dateFormat.format(new Date(endTimestamp));
    }



    public void setSalary(double salary) {
        if (salary > 0) {
            this.salary = salary;
            setTotal();  // Recalculate total whenever salary changes
            System.out.println("Salary updated: " + salary);
        } else {
            System.out.println("Attempt to set invalid salary: " + salary);
        }
    }



    public Double getTotal() {
        return total;
    }
    private void setTotal() {
        total = hours * salary;
    }




    private void calculateHours() {
        if (endTimestamp > startTimestamp) {
            duration = endTimestamp - startTimestamp;
            hours = (double) duration / TimeUnit.HOURS.toMillis(1);
            setTotal();  // Call setTotal here to ensure it updates
        } else {
            hours = 0.0;
        }
    }

    private void setDates() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.startDate = dateFormat.format(new Date(startTimestamp));
        this.endDate = dateFormat.format(new Date(endTimestamp));
    }



    @NonNull
    @Override
    public String toString() {
        return "Start time: " + this.startDate + '\n' +
                "End time: " + this.endDate + '\n' +
                "Role: " + this.role + '\n' +
                "Hours Worked: " + (hours != null ? hours : "Not calculated") + '\n' +
                "Total Pay: " + (total != null ? total : "Not calculated");
    }
}

