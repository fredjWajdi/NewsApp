package com.wizzapps.android.newsapp.model;

import java.util.List;

public class News {
    private String sectionName;
    private String title;
    private String webUrl;
    private String publicationDate;
    private List<String> authorName;

    public News(String sectionName, String webTitle, String webUrl, String publicationDate){
        this.sectionName = sectionName;
        this.title = webTitle;
        this.webUrl = webUrl;
        this.publicationDate = publicationDate;
    }

    public News(String sectionName, String webTitle, String webUrl, String publicationDate, List<String> authorName){
        this.sectionName = sectionName;
        this.title = webTitle;
        this.webUrl = webUrl;
        this.publicationDate = publicationDate;
        this.authorName = authorName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String webTitle) {
        this.title = webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public List<String> getAuthorName() {
        return authorName;
    }

    public void setAuthorName(List<String> authorName) {
        this.authorName = authorName;
    }

    public void addAuthorName(String authorName){
        this.authorName.add(authorName);
    }

}
