package com.vansuita.passwordvault.serv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.vansuita.passwordvault.bean.Bean;
import com.vansuita.passwordvault.enums.ECategory;
import com.vansuita.passwordvault.view.floating.FloatingPasswordView;
import com.vansuita.passwordvault.view.floating.TouchForFloatingView;

/**
 * Created by jrvansuita on 06/03/17.
 */

public class FloatingPasswordService extends Service {

    private boolean isRunning = false;
    private Bean object;
    private View anchor;
    private FloatingPasswordView floating;

    private TouchForFloatingView touch;

    private static final String OBJECT = "OBJECT";

    private static Intent intent(Context context, Bean bean) {
        Intent intent = new Intent(context, FloatingPasswordService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(OBJECT, bean);
        intent.putExtras(bundle);
        return intent;
    }

    public static void start(Context context, Bean bean) {
        if (bean.getCategory() != ECategory.NOTES)
            context.startService(intent(context, bean));
    }

    public static void stop(Context context) {
        context.stopService(intent(context, null));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isRunning) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                object = (Bean) bundle.getSerializable(OBJECT);
            }

            isRunning = true;
            run();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void run() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        floating = new FloatingPasswordView(this);
        floating.load(object);

        anchor = new View(this);
        touch = new TouchForFloatingView(floating, anchor);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = touch.getXPos();
        params.y = touch.getYPos();

        windowManager.addView(floating, params);

        WindowManager.LayoutParams anchorParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);
        anchorParams.gravity = Gravity.LEFT | Gravity.TOP;
        anchorParams.x = 0;
        anchorParams.y = 0;
        anchorParams.width = 0;
        anchorParams.height = 0;
        windowManager.addView(anchor, anchorParams);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
        touch.destroy();
    }


}