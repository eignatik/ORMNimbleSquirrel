package com.example.myapp.sql.entity;




import com.example.myapp.sql.annotations.Id;
import com.example.myapp.sql.annotations.Table;
import com.example.myapp.sql.annotations.TableField;


@Table()
public class User implements IEntity
{


    @Id() private Integer id;
    @TableField  private String login;
    @TableField  private String pass;

    public User()
    {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
