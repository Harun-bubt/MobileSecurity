package com.mobilesecurity.mobileapp.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobilesecurity.mobileapp.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class PhoneLocationDao {
    private static final String DB_NAME = "phone_location.db";
    private static final String TB_NUMBER = "number";
    private static final String TB_LOCATION = "location";

    private Context context;

    public PhoneLocationDao(Context context) {
        this.context = context;
    }

    private SQLiteDatabase getWritableDatabase() {
        try {
            File file = new File(context.getFilesDir(), DB_NAME);
            if(!file.exists()) {
                InputStream in = context.getAssets().open(DB_NAME);
                FileUtils.saveFileWithStream(file, in);
            }
            return SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String queryCellphoneLocation(String areaNumber) {
    
        if(areaNumber.length() != 7)
            throw new IllegalArgumentException("the length of areaNumber is wrong. Excepted length is 7, the actual length is " + areaNumber.length());

        String location = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select location from location where id = (select outkey from number where id = ?)",
                new String[]{areaNumber});
        if(cursor.moveToNext()) {
            location = cursor.getString(cursor.getColumnIndex("location"));
        }
        cursor.close();
        db.close();
        return location;
    }


    public String queryTelephoneLocation(String areaNumber) {
       
        if(areaNumber.length() > 4 || areaNumber.length() < 3)
            throw new IllegalArgumentException("the length of areaNumber is not in range. Excepted length is 3 or 4, the actual length is " + areaNumber.length());

        String location = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select substr(location, 1, length(location) - 2) as loc from location where area = ? limit 1",
                new String[]{areaNumber.substring(1)});
        if(cursor.moveToNext()) {
            location = cursor.getString(cursor.getColumnIndex("loc"));
        }
        cursor.close();
        db.close();
        return location;
    }

}
