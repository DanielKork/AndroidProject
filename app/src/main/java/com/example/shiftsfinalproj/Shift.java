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

public class Shift {
    private long startTimestamp;
    private long endTimestamp;
    private String user_id; // Ensure this matches the field name in Firestore.
    private String role;

    // Default constructor is required for Firebase's automatic data mapping.
    public Shift() {
    }

    // Parametrized constructor for manual object creation.
    public Shift(long startTimestamp, long endTimestamp, String user_id, String role) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.user_id = user_id;
        this.role = role;
    }

    // Getters and setters for startTimestamp.
    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    // Getters and setters for endTimestamp.
    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
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
}

