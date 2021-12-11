package com.mobilesecurity.mobileapp.db;

import android.content.ContentProvider;

public abstract class AppProvider extends ContentProvider {
    DBHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }


}
