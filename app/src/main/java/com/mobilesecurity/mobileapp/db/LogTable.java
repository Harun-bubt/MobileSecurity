package com.mobilesecurity.mobileapp.db;

import android.net.Uri;

public class LogTable {

    public static final String RELATIVE_PATH = "log";
    public static final String TABLE = "log";

    public static final String UID = "_id";
    public static final String CALLER_ID = "caller_id";
    public static final String DISPLAY_NUMBER = "display_number";
    public static final String DISPLAY_NAME = "display_name";
    public static final String DATE = "date";
    public static final String BLOCK_ORIGIN = "block_origin";

    public static final Uri CONTENT_URI = Uri.parse("content://" + LogProvider.AUTHORITY + "/" +
            RELATIVE_PATH);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + RELATIVE_PATH;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + RELATIVE_PATH;

    public static final int SSID_PATH_POSITION = 1;

}
