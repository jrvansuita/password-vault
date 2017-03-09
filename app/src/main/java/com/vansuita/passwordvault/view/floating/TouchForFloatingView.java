package com.vansuita.passwordvault.view.floating;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by jrvansuita on 07/03/17.
 */

public class TouchForFloatingView implements View.OnTouchListener {


    private static final String OVERLAY_X = "OVERLAY_X";
    private static final String OVERLAY_Y = "OVERLAY_Y";

    private WindowManager windowManager;
    private View overlayedView;
    private View anchor;

    private float offsetX;
    private float offsetY;
    private int originalXPos;
    private int originalYPos;
    private boolean moving;

    public TouchForFloatingView(View overlayedView, View anchor) {
        this.setAnchor(anchor);
        this.setOverlayedView(overlayedView);
    }

    public void setAnchor(View anchor) {
        this.anchor = anchor;
        this.windowManager = (WindowManager) anchor.getContext().getSystemService(Context.WINDOW_SERVICE);;
    }

    public void setOverlayedView(View overlayedView) {
        this.overlayedView = overlayedView;
        this.overlayedView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getRawX();
            float y = event.getRawY();

            moving = false;

            int[] location = new int[2];
            overlayedView.getLocationOnScreen(location);

            originalXPos = location[0];
            originalYPos = location[1];

            offsetX = originalXPos - x;
            offsetY = originalYPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            anchor.getLocationOnScreen(topLeftLocationOnScreen);

            //System.out.println("topLeftY=" + topLeftLocationOnScreen[1]);
            //System.out.println("originalY=" + originalYPos);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayedView.getLayoutParams();

            int newX = (int) (offsetX + x);
            int newY = (int) (offsetY + y);

            if (Math.abs(newX - originalXPos) < 1 && Math.abs(newY - originalYPos) < 1 && !moving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            windowManager.updateViewLayout(overlayedView, params);
            moving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (moving) {
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayedView.getLayoutParams();

                PreferenceManager.getDefaultSharedPreferences(anchor.getContext())
                        .edit()
                        .putInt(OVERLAY_X, params.x)
                        .putInt(OVERLAY_Y, params.y)
                        .apply();

                return true;
            }
        }

        return false;
    }


    public int getXPos() {
        return PreferenceManager.getDefaultSharedPreferences(anchor.getContext()).getInt(OVERLAY_X, 0);
    }

    public int getYPos() {
        return PreferenceManager.getDefaultSharedPreferences(anchor.getContext()).getInt(OVERLAY_Y, 0);
    }


    public void destroy() {
        if (windowManager != null) {
            windowManager.removeView(overlayedView);
            windowManager.removeView(anchor);
        }
    }
}
