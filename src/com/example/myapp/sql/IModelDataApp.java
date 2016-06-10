package com.example.myapp.sql;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Anama on 22.10.2014.
 */
public interface IModelDataApp {
    public void initDAO(SQLiteDatabase db);
    public void createTables();
    public void deleteTables();
    public void initModelDataApp();
}
