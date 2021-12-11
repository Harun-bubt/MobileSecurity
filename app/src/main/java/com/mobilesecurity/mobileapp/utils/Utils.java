package com.mobilesecurity.mobileapp.utils;

// TODO: Auto-generated Javadoc

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 *
 */
public class Utils {


	
	private static final String TAG = Utils.class.getName();


    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }







}
