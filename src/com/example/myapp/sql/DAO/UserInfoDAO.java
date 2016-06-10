package com.example.myapp.sql.DAO;

import android.database.sqlite.SQLiteDatabase;

import com.example.myapp.sql.GenericDAO;
import com.example.myapp.sql.entity.UserInfo;

import java.util.List;

/**
 * Created by Anama on 20.10.2014.
 */
public class UserInfoDAO extends GenericDAO<UserInfo>
{

    public UserInfoDAO(SQLiteDatabase db) {
        super(db);
    }

}
