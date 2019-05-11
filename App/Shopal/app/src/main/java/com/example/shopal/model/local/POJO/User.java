package com.example.shopal.model.local.POJO;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    @Expose
    private Name name;
    @SerializedName("contact")
    @Expose
    private Contact contact;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;
    @SerializedName("user_type")
    @Expose
    private String userType;
    @SerializedName("password")
    @Expose
    private String password;


    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
