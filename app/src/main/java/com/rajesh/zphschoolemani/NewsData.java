package com.rajesh.zphschoolemani;

public class Newsdata {
    private String News_Title, News_Description, File_URL;

    // generate their respective constructors
    public Newsdata(String News_Title, String News_Description, String File_URL) {
        this.News_Title = News_Title;
        this.News_Description = News_Description;
        this.File_URL=File_URL;
    }
    // create an empty constructor
    public Newsdata() {
    }
    public void setFile_URL(String File_URL) {
        this.File_URL = File_URL;
    }
    public void setNews_Title(String News_Title) {
        this.News_Title = News_Title;
    }
    public void setNews_Description(String News_Description) {
        this.News_Description = News_Description;
    }
    public String getFile_URL() {
        return File_URL;
    }
    public String getNews_Title() {
        return News_Title;
    }
    public String getNews_Description() {
        return News_Description;
    }

}
