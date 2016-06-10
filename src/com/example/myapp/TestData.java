package com.example.myapp;

import java.util.Date;

/**
 * Created by eignatik on 28/05/15.
 */
public class TestData {
    private String nameUser;
    private String titleBlog;
    private Date dateBlog;

    public Date getDateBlog() {
        return dateBlog;
    }

    public void setDateBlog(Date dateBlog) {
        this.dateBlog = dateBlog;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getTitleBlog() {
        return titleBlog;
    }

    public void setTitleBlog(String titleBlog) {
        this.titleBlog = titleBlog;
    }
}
