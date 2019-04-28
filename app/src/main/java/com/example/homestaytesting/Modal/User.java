package com.example.homestaytesting.Modal;

public class User {
    private String name;
    private String email;
    private String phone;
    private String role;
    private String profileimage2;
    private String profiledescription;
    private String devicetoken;


    public User()
    {

    }

    public User(String name, String email, String phone, String role,String profileimage2, String profiledescription, String devicetoken) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profileimage2 = profileimage2;
        this.profiledescription = profiledescription;
        this.devicetoken = devicetoken;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
    }

    public String getProfileimage2() {
        return profileimage2;
    }

    public void setProfileimage2(String profileimage2) {
        this.profileimage2 = profileimage2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfiledescription() {
        return profiledescription;
    }

    public void setProfiledescription(String profiledescription) {
        this.profiledescription = profiledescription;
    }
}

