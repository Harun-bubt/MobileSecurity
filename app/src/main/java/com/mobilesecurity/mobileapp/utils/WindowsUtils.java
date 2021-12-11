package com.mobilesecurity.mobileapp.utils;

import android.app.Activity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

/**

 * Some windows method.
 */

public class WindowsUtils {
    /**
     * set the activity display in full screen
     * @param context the activity that need be show full screen.
     */
    public static void setFullScreen(Activity context) {
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * hide action bar
     * @param context he activity that need hide action bar
     */
    public static void hideActionBar(AppCompatActivity context) {
        // Hide UI
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
