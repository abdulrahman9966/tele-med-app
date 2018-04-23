package com.uhcl.ted.Model;

/**
 * Created by LENOVO on 24-03-2018.
 */

public class Patient {


    private String name;
    private String email;
    private String age;
    private String phone;
    private String imageUrl;
    private String type;

    public Patient() {
    }

    public Patient(String name, String email, String age, String phone, String imageUrl, String type) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.type = type;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
