package com.example.myapp.Util.Options;

import android.content.Context;
import com.example.myapp.Util.Log;


/**
 * Опции приложения
 * Created by Anama on 09.04.2015.
 */
public class AppOptions extends AbsOptions
{
    private static AppOptions instance=null;

    public static AppOptions instance(Context ctx)
    {
        if(instance==null)instance=new AppOptions(ctx);
        return instance;

    }


    public AppOptions(Context ctx)
    {
        super(ctx,"MAIN_PREFERENCIES");
    }

    /**
     * Здесь задаем настройки
     */
    @Override
    void createOptions()
    {
        OptionsItem item;
        try {
            item = createOption(new FloatItem((float) 0.35, "lowPorogPulse"));
            item.setMax((float) 0.35);
            item.setMin((float) 0.25);

            item=createOption(new StringItem("BiomedisHealer","fileDirApp"));

        } catch (Exception e){
            Log.v(e.getMessage());

        }

    }
}
