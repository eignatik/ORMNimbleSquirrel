package com.example.myapp.sql.DAO;

import android.database.sqlite.SQLiteDatabase;
import com.example.myapp.sql.GenericDAO;
import com.example.myapp.sql.entity.Blog;
import com.example.myapp.sql.entity.UserInfo;

import java.util.List;

public class BlogDAO extends GenericDAO<Blog>
{

    public BlogDAO(SQLiteDatabase db) {
        super(db);
    }

}
