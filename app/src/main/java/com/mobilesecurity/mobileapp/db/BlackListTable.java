package com.mobilesecurity.mobileapp.db;

import android.net.Uri;


public class BlackListTable {
    public static final String UID = "_id";
    public static final String NORMALIZED_NUMBER = "normalized_number";
    public static final String DISPLAY_NUMBER = "display_number";
    public static final String DISPLAY_NAME = "display_name";
    public static final String BEGIN_WITH = "begin_with";
    public static final String ENABLED = "enabled";

    public static final String TABLE = "black_list";
    public static final String RELATIVE_PATH = "blacklist";

    public static final Uri CONTENT_URI = Uri.parse("content://" + BlackListProvider.AUTHORITY +
            "/" + RELATIVE_PATH);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + RELATIVE_PATH;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + RELATIVE_PATH;

    public static final int SSID_PATH_POSITION = 1;
    public static final String BEGIN_WITH_SYMBOL = "?";
}
