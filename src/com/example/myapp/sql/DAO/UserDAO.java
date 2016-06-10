package com.example.myapp.sql.DAO;

import android.database.sqlite.SQLiteDatabase;
import com.example.myapp.sql.GenericDAO;
import com.example.myapp.sql.entity.User;
import com.example.myapp.sql.entity.UserInfo;

import java.util.List;


public class UserDAO extends GenericDAO<User>
{

    public UserDAO(SQLiteDatabase db) {
        super(db);
    }
}
