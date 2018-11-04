package com.rajesh.zphschoolemani;

public class student {
    private String fullname;
    private String ssc;

    public student() {
    }

    public student(String fullname, String ssc) {
        this.fullname = fullname;
        this.ssc = ssc;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSsc() {
        return ssc;
    }

    public void setSsc(String ssc) {
        this.ssc = ssc;
    }
}
