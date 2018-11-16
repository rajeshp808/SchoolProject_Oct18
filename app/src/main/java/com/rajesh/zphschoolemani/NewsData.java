package com.rajesh.zphschoolemani;

public class Newsdata {
    private String title, description, url;

    // generate their respective constructors
    public Newsdata(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url=url;
    }
    // create an empty constructor
    public Newsdata() {
    }
    public void seturl(String url) {
        this.url = url;
    }
    public void settitle(String title) {
        this.title = title;
    }
    public void setdescription(String News_Description) {
        this.description = News_Description;
    }
    public String geturl() {
        return url;
    }
    public String gettitle() {
        return title;
    }
    public String getdescription() {
        return description;
    }

}
