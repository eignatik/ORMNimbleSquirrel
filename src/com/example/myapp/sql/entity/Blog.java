package com.example.myapp.sql.entity;




import com.example.myapp.sql.annotations.Id;
import com.example.myapp.sql.annotations.Table;
import com.example.myapp.sql.annotations.TableField;

import java.util.Date;

/**
 * Created by Anama on 20.10.2014.
 */
@Table()
public class Blog implements IEntity
{


    @Id() private Integer id;
    @TableField(foreignKey = true, columnDefinition = "NOT NULL")  private Integer idUser;
    @TableField  private String title;
    @TableField  private Date dtBlog;
    @TableField  private String content;

    public Blog()
    {
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDtBlog() {
        return dtBlog;
    }

    public void setDtBlog(Date dtBlog) {
        this.dtBlog = dtBlog;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
