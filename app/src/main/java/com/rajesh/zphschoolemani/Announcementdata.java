package com.rajesh.zphschoolemani;

public class Announcementdata {
    private String Announcement_Title, Announcement_Title_Description, File_URL;

    // generate their respective constructors
    public Announcementdata(String Announcement_Title, String Announcement_Title_Description, String File_URL) {
        this.Announcement_Title = Announcement_Title;
        this.Announcement_Title_Description = Announcement_Title_Description;
        this.File_URL=File_URL;
    }
    // create an empty constructor
    public Announcementdata() {
    }
    public void setFile_URL(String File_URL) {
        this.File_URL = File_URL;
    }
    public void setAnnouncement_Title(String News_Title) {
        this.Announcement_Title = Announcement_Title;
    }
    public void setAnnouncement_Title_Description(String Announcement_Title_Description) {
        this.Announcement_Title_Description = Announcement_Title_Description;
    }
    public String getFile_URL() {
        return File_URL;
    }
    public String getAnnouncement_Title() {
        return Announcement_Title;
    }
    public String getAnnouncement_Title_Description() {
        return Announcement_Title_Description;
    }

}
