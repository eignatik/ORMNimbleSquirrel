package com.example.myapp;

import android.app.Application;
import android.content.res.Configuration;
import com.example.myapp.sql.DBHelper;
import com.example.myapp.sql.ModelDataApp;
import com.example.myapp.sql.entity.User;

/**
 * Created by eignatik on 20.05.15.
 */
public class App extends Application {
    private boolean loggened =false;

    /**
     * Инкрементируется при изменении структуры базы, если она требует скрипта обновления.
     */
    private static int BD_VERSION=1;
    private static String BD_NAME="blogtest.db";
    private DBHelper dbHelper;
    private ModelDataApp modelDataApp=null;
    private User user=null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ModelDataApp getModelDataApp() {
        return modelDataApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        closeDB();
    }

    private void initContext()
    {
       initDB();
    }

    private void initDB()
    {
        this.dbHelper=new DBHelper(this,BD_VERSION,BD_NAME);
        this.dbHelper.open();
        this.modelDataApp=this.dbHelper.getMda();
    }
    public void closeDB(){dbHelper.close();}

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

    }

    public boolean isLoggened() {
        return loggened;
    }

    public void setIsLoggened(boolean isLoggened) {
        this.loggened = isLoggened;
    }

}
