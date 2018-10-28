package com.rajesh.zphschoolemani;

public class NewsData {
    private String News_Title, News_Description, File_URL;

    // generate their respective constructors
    public NewsData(String title, String desc, String imageUrl) {
        this.News_Title = title;
        this.News_Description = desc;
        this.File_URL=imageUrl;
    }
    // create an empty constructor
    public NewsData() {
    }
    public void setImageUrl(String imageUrl) {
        this.File_URL = imageUrl;
    }
    public void setTitle(String title) {
        this.News_Title = title;
    }
    public void setDesc(String desc) {
        this.News_Description = desc;
    }
    public String getImageUrl() {
        return File_URL;
    }
    public String getTitle() {
        return News_Title;
    }
    public String getDesc() {
        return News_Description;
    }

}
