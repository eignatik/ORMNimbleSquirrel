package com.example.myapp.Util.MidifyViews;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapp.R;


/**
 * Created by Anama on 08.02.2015.
 */
public class BiotestToast
{
    public static final int NORMAL_MESSAGE=1;
    public static final int WARNING_MESSAGE=2;
    public static final int ERROR_MESSAGE=3;


   public static Toast makeMessage(Context ctx, String message,int duration, int typeMessage )
   {
     // Toast toast = Toast.makeText(ctx,message,duration);

       LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View layout = inflater.inflate(R.layout.toast,null);
       Toast toast = new Toast(ctx.getApplicationContext());

       ViewGroup group = (ViewGroup) layout.findViewById(R.id.toast_layout_root);
       switch(typeMessage)
       {
           case NORMAL_MESSAGE:
               group.setBackgroundResource(R.drawable.toast_bg_normal);
               break;
           case WARNING_MESSAGE:
               group.setBackgroundResource(R.drawable.toast_bg_warning);
               break;
           case ERROR_MESSAGE:
               group.setBackgroundResource(R.drawable.toast_bg_error);
               break;

       }
       TextView txt = (TextView) layout.findViewById(R.id.toast_text);

       txt.setText(message);

       toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
       toast.setDuration(duration);
       toast.setView(layout);

       return toast;
   }

    public static void makeMessageShow(Context ctx, String message,int duration, int typeMessage )
    {
        makeMessage( ctx,  message, duration,  typeMessage ).show();
    }
}
