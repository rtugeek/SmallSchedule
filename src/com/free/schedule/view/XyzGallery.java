package com.free.schedule.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class XyzGallery extends Gallery {

    public XyzGallery(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        setStaticTransformationsEnabled(true);
    }

    public XyzGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setStaticTransformationsEnabled(true);
    }

    public XyzGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        setStaticTransformationsEnabled(true);
    }
    
    

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        // TODO Auto-generated method stub
        if (velocityX > 0) {
            onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
        } else {
            onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        }
        return true;
    }
}
