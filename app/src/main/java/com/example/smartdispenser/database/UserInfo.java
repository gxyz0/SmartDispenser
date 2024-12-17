package com.example.smartdispenser.database;

public class UserInfo {
    private int userId;
    private String userNickname;
    private String userPhone;
    private String userEmail;
    private String userGender;
    private String userBirthday;
    private String userHealthStatus;
    private String userImage;

    public UserInfo(int userId, String userNickname, String userPhone, String userEmail, String userGender, String userBirthday, String userHealthStatus, String userImage) {
        this.userId = userId;
        this.userNickname = userNickname;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userBirthday = userBirthday;
        this.userHealthStatus = userHealthStatus;
        this.userImage = userImage;
    }

    public void setUserInfo(String userNickname, String userPhone, String userEmail, String userGender, String userBirthday, String userHealthStatus, String userImage) {
        this.userNickname = userNickname;
        this.userPhone = userPhone;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userBirthday = userBirthday;
        this.userHealthStatus = userHealthStatus;
        this.userImage = userImage;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public String getUserHealthStatus() {
        return userHealthStatus;
    }

    public String getUserImage() {
        return userImage;
    }
}
