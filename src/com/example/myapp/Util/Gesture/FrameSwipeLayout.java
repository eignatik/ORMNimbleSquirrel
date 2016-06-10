package com.example.myapp.Util.Gesture;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * Определяет свайпы на layout. Перехватывает их до передачи дочерним view
 * Created by Anama on 26.01.2015.
 */
public  class FrameSwipeLayout extends FrameLayout
{
    private boolean isSwiped =false;
    private int mTouchSlop;
    private float mLastX;
    private float mLastY;
    private float mStartY;
    private float mStartX;
    private ActionListener actionListener;

    private  float MIN_DISTANCE;
    private  int VELOCITY;
    private  float MAX_OFF_PATH;
    private  float MAX_OFF_PATH2;
    private long timeDown;
    private boolean typeSwipe=true;//тип свайпа, по умолчанию горизонтальный

    public static final  int LEFT_TO_RIGHT=1;
    public static final   int RIGHT_TO_LEFT=2;
    public static final   int TOP_TO_BOTTOM=3;
    public static  final  int BOTTOM_TO_TOP=4;


    public FrameSwipeLayout(Context context)
    {
        this(context, null);
    }

    public FrameSwipeLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FrameSwipeLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        ViewConfiguration vc = ViewConfiguration.get(this.getContext());
        mTouchSlop = vc.getScaledTouchSlop()*3;//сделаем порог срабатывания перехвата побольше, чтобы не делать ложных срабатываний

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        MIN_DISTANCE = vc.getScaledPagingTouchSlop() * dm.density;
        VELOCITY = vc.getScaledMinimumFlingVelocity();
        MAX_OFF_PATH = MIN_DISTANCE * 2;
        MAX_OFF_PATH2 = MIN_DISTANCE * 4;




    }

    /**
     * Тип свайпа, горизонтальный или вертикальный(true горизонтальный)
     * По умолчанию горизонтальный
     * @param isHorisontal
     */
 public void setTypeSwipe(boolean isHorisontal)
 {
     typeSwipe=isHorisontal;
 }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
         /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

//перехватывает эвент касания экрана, до того как его забереть дочерний view/
        final int action = ev.getActionMasked();

        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll.
            isSwiped = false;
            return false; // Do not intercept touch event, let the child handle it
        }

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                mStartY = mLastY;
                mStartX = mLastX;
                timeDown = System.currentTimeMillis();
                break;

            case MotionEvent.ACTION_MOVE:
            {
                if (isSwiped) {
                    // We're currently scrolling, so yes, intercept the
                    // touch event!
                   // Log.v("SWIPED in onInterceptTouchEvent ACTION_MOVE");
                    return true;
                }

                // If the user has dragged her finger horizontally more than
                // the touch slop, start the scroll





                // left as an exercise for the reader
                final int xDiff = (int)calculateDistanceX(ev);
                final int yDiff = (int) calculateDistanceY(ev);

                int diff;

                if(typeSwipe)diff=xDiff;
                else diff=yDiff;
                //если больше порога сдвиг, то это свайп. Иначе мы вернем false ожидая отработки вложенных view



                if (diff > mTouchSlop)// определим что у нас жест или просто касание
                {
                    // Start scrolling!
                    isSwiped = true;
                  //  Log.v("new SWIPED in onInterceptTouchEvent ACTION_MOVE");
                    return true;
                }


                break;


            }

        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.
        return false;
    }

    private float  calculateDistanceX(MotionEvent ev)
    {

        return  Math.abs(ev.getX() - mLastX);


    }

    private float  calculateDistanceY(MotionEvent ev)
    {


        return Math.abs(ev.getY() - mLastY);
    }

    private float  calculateTotalDistanceX(MotionEvent ev)
    {

        return  Math.abs(ev.getX() - mStartX);


    }

    private float  calculateTotalDistanceY(MotionEvent ev)
    {


        return Math.abs(ev.getY() - mStartY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //Log.v("onTouch form layout" +"ACTION = ("+event.getAction()+")");
         float  upX, upY;
        switch (event.getAction()) {

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            {
                isSwiped = false;

                long timeUp = System.currentTimeMillis();
                upX = event.getX();
                upY = event.getY();

                float deltaX = mLastX - upX;
                float deltaY = mLastY - upY;

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
                //Log.v("onTouchEvent ");

                final long M_SEC = 1000;

                if(typeSwipe)
                {
                    if (absDeltaX > MAX_OFF_PATH2 && absDeltaX > time * VELOCITY / M_SEC) {
                        if(deltaX < 0) { if(actionListener!=null) this.actionListener.swipe(LEFT_TO_RIGHT); return true; }
                        if(deltaX > 0) { if(actionListener!=null) this.actionListener.swipe(RIGHT_TO_LEFT); return true; }
                    } else
                    {
                        // Log.v( String.format("X ----- absDeltaX=%.2f, MIN_DISTANCE=%.2f, absDeltaX > MIN_DISTANCE=%b", absDeltaX, MIN_DISTANCE, (absDeltaX > MIN_DISTANCE)));
                        //  Log.v(String.format("X ----- absDeltaX=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaX > time * VELOCITY / M_SEC=%b", absDeltaX, time, VELOCITY, time * VELOCITY / M_SEC, (absDeltaX > time * VELOCITY / M_SEC)));
                    }
                }else
                {
                    if (absDeltaY > MAX_OFF_PATH2 && absDeltaY > time * VELOCITY / M_SEC) {
                        if(deltaY < 0) {  if(actionListener!=null) this.actionListener.swipe(TOP_TO_BOTTOM); return true; }
                        if(deltaY > 0) { if(actionListener!=null) this.actionListener.swipe(BOTTOM_TO_TOP); return true; }
                    } else {
                        // Log.v( String.format("Y ----- absDeltaY=%.2f, MIN_DISTANCE=%.2f, absDeltaY > MIN_DISTANCE=%b", absDeltaY, MIN_DISTANCE, (absDeltaY > MIN_DISTANCE)));
                        //  Log.v( String.format("Y ----- absDeltaY=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaY > time * VELOCITY / M_SEC=%b", absDeltaY, time, VELOCITY, time * VELOCITY / M_SEC, (absDeltaY > time * VELOCITY / M_SEC)));
                    }
                }



            }

        }

        return true;



    }

    /**
     * Устанавливается listener для событий свайпа
     * @param action
     */
    public void setActionListener(ActionListener action)
    {
        actionListener=action;
    }
    public interface ActionListener {

       public void swipe(int type);

    }
}
