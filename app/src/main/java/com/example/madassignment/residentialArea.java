package com.example.madassignment;

public class residentialArea {
    String name;
    String code;
    String photoLink;

    public residentialArea(String name, String code, String PhotoLink){
        this.name = name;
        this.code = code;
        this.photoLink = PhotoLink;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
