package com.mobilesecurity.mobileapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int FIRST_DATABASE_VERSION = 3;
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.onUpgrade(db, FIRST_DATABASE_VERSION - 1, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion + 1) {
            case FIRST_DATABASE_VERSION:
                db.execSQL("CREATE TABLE black_list (" +
                        "        _id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "        normalized_number TEXT," +
                        "        display_number    TEXT," +
                        "        display_name      TEXT," +
                        "        begin_with        INTEGER," +
                        "        enabled           INTEGER," +
                        "        data1             TEXT," + //For future use
                        "        data2             TEXT," + //For future use
                        "        data3             TEXT" + //For future use
                        ");");

                db.execSQL("CREATE TABLE log (" +
                        "    _id           INTEGER PRIMARY KEY ASC AUTOINCREMENT NOT NULL," +
                        "    caller_id         TEXT," +
                        "    display_number    TEXT," +
                        "    display_name      TEXT," +
                        "    date              NUMERIC," +
                        "    block_origin      TEXT," +
                        "    data1             TEXT," + //For future use
                        "    data2             TEXT," + //For future use
                        "    data3             TEXT" + //For future use
                        ");");

                db.execSQL("CREATE INDEX i_bl_normalized_number ON black_list (normalized_number)" +
                        ";");
                db.execSQL("CREATE INDEX i_bl_block ON black_list (normalized_number, begin_with," +
                        " " +
                        "enabled);");
                db.execSQL("CREATE INDEX i_bl_begin_with ON black_list (begin_with);");
                db.execSQL("CREATE INDEX i_log_date ON log (date);");
            case 4:
                db.execSQL("CREATE TABLE quick_black_list (" +
                        "    _id               INTEGER," +
                        "    caller_id         TEXT," +
                        "    data1             TEXT," + //For future use
                        "    data2             TEXT," + //For future use
                        "    data3             TEXT" + //For future use
                        ");");
                db.execSQL("CREATE INDEX i_qbl_id ON quick_black_list (_id);");
                db.execSQL("CREATE INDEX i_qbl_caller_id ON quick_black_list (caller_id);");
        }
    }
}


