package com.uhcl.ted.Model;

/**
 * Created by LENOVO on 26-03-2018.
 */

public class Doctor {

    private String name;
    private String email;
    private String spec;
    private String exp;
    private String imageUrl;
    private String status;
    private String uid;
    private String type;

    public Doctor() {

    }

    public Doctor(String name, String email, String spec, String exp, String imageUrl, String status, String uid, String type) {
        this.name = name;
        this.email = email;
        this.spec = spec;
        this.exp = exp;
        this.imageUrl = imageUrl;
        this.status = status;
        this.uid = uid;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
