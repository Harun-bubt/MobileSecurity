package com.mobilesecurity.mobileapp.AntiVirus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class AntiVirusDao {


    public static String checkVirus(String md5) {
        String desc = null;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db" + "/antivirus.db");
        if (file.exists()) {
            System.out.println("The database exists");
            SQLiteDatabase db = SQLiteDatabase
                    .openDatabase(
                            Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db" + "/antivirus.db",
                            null, SQLiteDatabase.OPEN_READONLY);

            String sq = "select  desc from datable where md5=?";
            Cursor cursor = db.rawQuery(sq, new String[]{md5});
            while (cursor.moveToNext()) {
                desc = cursor.getString(0);
            }
            cursor.close();
            db.close();
            return desc;
        } else {
            System.out.println("The database does not exist");
            return null;
        }
    }

    public static String checkViruss(File[] md5) {
        String desc = null;

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db" + "/antivirus.db");
        if (file.exists()) {
            System.out.println("The database exists");
            SQLiteDatabase db = SQLiteDatabase
                    .openDatabase(
                            Environment.getExternalStorageDirectory() + File.separator + "SMOM" + File.separator + "db" + "/antivirus.db",
                            null, SQLiteDatabase.OPEN_READONLY);

            String sq = "select  desc from datable where md5=?";
            Cursor cursor = db.rawQuery(sq, new String[]{String.valueOf(md5)});
            while (cursor.moveToNext()) {
                desc = cursor.getString(0);
            }
            cursor.close();
            db.close();
            return desc;
        } else {
            System.out.println("The database does not exist");
            return null;
        }
    }


    }
