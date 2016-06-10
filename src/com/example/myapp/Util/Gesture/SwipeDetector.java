package com.example.myapp.Util.Gesture;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.example.myapp.Util.Log;


/**
 *
 * Работает только для отдельных View, не являющимияс Layout.
 *
 * it is used like this:

 SwipeDetector swipe = new SwipeDetector(this);
 LinearLayout swipe_layout = (LinearLayout) findViewById(R.id.swipe_layout);
 swipe_layout.setOnTouchListener(swipe);

 And in implementing Activity you need to implement methods from SwipeInterface, and you can find out on which View the Swipe Event was called.

 @Override
 public void left2right(View v) {
 switch(v.getId()){
 case R.id.swipe_layout:
 // do your stuff here
 break;
 }
 }
 * Created by Anama on 22.01.2015.
 */
public class SwipeDetector implements View.OnTouchListener {


    public interface SwipeInterface {

        public void bottom2top(View v);

        public void left2right(View v);

        public void right2left(View v);

        public void top2bottom(View v);

    }


    private SwipeInterface activity;
    private final float MIN_DISTANCE;
    private final int VELOCITY;
    private final float MAX_OFF_PATH;
    private float downX, downY, upX, upY;
    private long timeDown;
    private SwipeInterface actionListener;

/*
    DisplayMetrics dm = getResources().getDisplayMetrics();
    int REL_SWIPE_MIN_DISTANCE = (int)(SWIPE_MIN_DISTANCE * dm.densityDpi / 160.0f);
    int REL_SWIPE_MAX_OFF_PATH = (int)(SWIPE_MAX_OFF_PATH * dm.densityDpi / 160.0f);
    int REL_SWIPE_THRESHOLD_VELOCITY = (int)(SWIPE_THRESHOLD_VELOCITY * dm.densityDpi / 160.0f);
*/
    public SwipeDetector(Context context, SwipeInterface activity){
        this.activity = activity;

        if(activity instanceof SwipeInterface) actionListener=activity;

        final ViewConfiguration vc = ViewConfiguration.get(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        MIN_DISTANCE = vc.getScaledPagingTouchSlop() * dm.density;
        VELOCITY = vc.getScaledMinimumFlingVelocity();
        MAX_OFF_PATH = MIN_DISTANCE * 2;

    }

    public SwipeDetector(Context context){


        if(activity instanceof SwipeInterface) actionListener=activity;

        final ViewConfiguration vc = ViewConfiguration.get(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        MIN_DISTANCE = vc.getScaledPagingTouchSlop() * dm.density;
        VELOCITY = vc.getScaledMinimumFlingVelocity();
        MAX_OFF_PATH = MIN_DISTANCE * 2;

    }
    public void setActionListener(SwipeInterface actionListener)
    {
        this.actionListener=actionListener;
    }
    public void onRightToLeftSwipe(View v){
        Log.v("RightToLeftSwipe!");
      if(actionListener!=null)  actionListener.right2left(v);
    }

    public void onLeftToRightSwipe(View v){
        Log.v("LeftToRightSwipe!");
        if(actionListener!=null)  actionListener.left2right(v);
    }

    public void onTopToBottomSwipe(View v){
        Log.v("onTopToBottomSwipe!");
        if(actionListener!=null)  actionListener.top2bottom(v);
    }

    public void onBottomToTopSwipe(View v){
        Log.v("onBottomToTopSwipe!");
        if(actionListener!=null)  actionListener.bottom2top(v);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            {
                timeDown = System.currentTimeMillis();
                downX = event.getX();
                downY = event.getY();
                return true;
            }

            case MotionEvent.ACTION_UP:
            {
                long timeUp = System.currentTimeMillis();
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);

                long time = timeUp - timeDown;

/*
                //просто клик если долго
                if (absDeltaY > MAX_OFF_PATH) {
                    Log.v( String.format("absDeltaY=%.2f, MAX_OFF_PATH=%.2f", absDeltaY, MAX_OFF_PATH));
                    return v.performClick();
                }

*/


                final long M_SEC = 1000;
                if (absDeltaX > MIN_DISTANCE && absDeltaX > time * VELOCITY / M_SEC) {
                    if(deltaX < 0) { this.onLeftToRightSwipe(v); return true; }
                    if(deltaX > 0) { this.onRightToLeftSwipe(v); return true; }
                } else {
                    Log.v( String.format("absDeltaX=%.2f, MIN_DISTANCE=%.2f, absDeltaX > MIN_DISTANCE=%b", absDeltaX, MIN_DISTANCE, (absDeltaX > MIN_DISTANCE)));
                    Log.v(String.format("absDeltaX=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaX > time * VELOCITY / M_SEC=%b", absDeltaX, time, VELOCITY, time * VELOCITY / M_SEC, (absDeltaX > time * VELOCITY / M_SEC)));
                }

                if (absDeltaY > MIN_DISTANCE && absDeltaY > time * VELOCITY / M_SEC) {
                    if(deltaY < 0) { this.onTopToBottomSwipe(v); return true; }
                    if(deltaY > 0) { this.onBottomToTopSwipe(v); return true; }
                } else {
                    Log.v( String.format("absDeltaX=%.2f, MIN_DISTANCE=%.2f, absDeltaX > MIN_DISTANCE=%b", absDeltaX, MIN_DISTANCE, (absDeltaX > MIN_DISTANCE)));
                    Log.v( String.format("absDeltaX=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaX > time * VELOCITY / M_SEC=%b", absDeltaX, time, VELOCITY, time * VELOCITY / M_SEC, (absDeltaX > time * VELOCITY / M_SEC)));
                }




            }
        }
        return false;
    }

}
