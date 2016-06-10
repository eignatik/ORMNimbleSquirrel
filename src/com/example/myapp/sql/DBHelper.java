package com.example.myapp.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myapp.Util.Log;


/**
   Обновление  БД идет не через самописный ORM, это скрипты БД. Entity классы должны быть правильного вида к новой версии базы.

 */
public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db=null;
    private ModelDataApp mda=null;

    public DBHelper(Context context,int ver,String name) {

        super(context, name, null, ver);
        mda=new ModelDataApp();
        Log.v("DBHelper");
    }

    public ModelDataApp getMda() {
        return mda;
    }

private SQLiteDatabase getDb()
{
    if(db==null) open();
    return db;
}

    public void open()
    {
        db = this.getWritableDatabase();
        if(db!=null)db.execSQL("PRAGMA encoding = \"UTF-8\";");
        else Log.v("А база то не открылась!!");


        if(db!=null)  Log.v("Путь к базе "+db.getPath());
        if(db!=null)  Log.v("Версия базы "+db.getVersion());
        showTablesToLog(db);

    }

    public void close()
    {
        this.close();
        db=null;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        this.mda.initDAO(db);
        this.mda.initModelDataApp();
        Log.v(db.toString());
        Log.v("onOpen");

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        this.mda.initDAO(db);

        Log.v("onCreateDB");

            this.mda.createTables();



    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.mda.initDAO(db);
        Log.v("onUpgradeDB old="+oldVersion+" new="+newVersion);

            this.mda.deleteTables();
            this.mda.createTables();


    }

    public void showTablesToLog(SQLiteDatabase db)
    {
        if(db!=null)
        {

            Cursor cursor = db.rawQuery("select * from sqlite_master where type = 'table'", null);
            if (cursor != null) {
                cursor.moveToFirst();
                do
                {
                    String  s="";
                    for (int i = 0; i < cursor.getColumnCount(); i++)s+=cursor.getString(i)+" ";
                    Log.v(s);
                }while(cursor.moveToNext()      );
            }


        }
    }
}
