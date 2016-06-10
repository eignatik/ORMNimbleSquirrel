package com.example.myapp.sql.entity;




import com.example.myapp.sql.annotations.Id;
import com.example.myapp.sql.annotations.Table;
import com.example.myapp.sql.annotations.TableField;

import java.util.Date;

/**
 * Created by Anama on 20.10.2014.
 */
@Table()
public class UserInfo implements IEntity
{


    @Id() private Integer id;
    @TableField(columnDefinition = "NOT NULL", foreignKey = true)  private Integer idUser;
    @TableField  private String userName;
    @TableField  private Date bornDate;
    @TableField  private String descUser;
    @TableField  private Boolean gender;



    public UserInfo()
    {
    }

    public Date getBornDate() {
        return bornDate;
    }

    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }

    public String getDescUser() {
        return descUser;
    }

    public void setDescUser(String descUser) {
        this.descUser = descUser;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
