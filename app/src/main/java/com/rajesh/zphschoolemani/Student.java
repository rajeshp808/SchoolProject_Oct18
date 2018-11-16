package com.rajesh.zphschoolemani;

public class student {
    private String fullname;
    private String sscyear;

    public student() {
    }

    public student(String fullname, String sscyear) {
        this.fullname = fullname;
        this.sscyear = sscyear;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSscyear() {
        return sscyear;
    }

    public void setSscyear(String sscyear) {
        this.sscyear = sscyear;
    }
}
