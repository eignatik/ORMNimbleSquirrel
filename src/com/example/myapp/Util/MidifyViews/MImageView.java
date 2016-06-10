package com.example.myapp.Util.MidifyViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * ListView, by design, does not pass perform click events on list items when those items contain FOCUSABLE views, regardless of how you configured any of its other flags (ListView actually protects the PerformClick method by first checking hasFocusable() on any list item).
 * Created by Anama on 31.01.2015.
 */
public class MImageView extends ImageButton
{
    public MImageView(Context context)
    {
        super(context,null);
    }

    public MImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs,-1);
    }

    public MImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);


    }

    @Override
    public void setPressed(boolean pressed) {
        // If the parent is pressed, do not set to pressed.
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }

}
