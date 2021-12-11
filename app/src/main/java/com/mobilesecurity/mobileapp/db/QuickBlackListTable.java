package com.mobilesecurity.mobileapp.db;

import android.net.Uri;


public class QuickBlackListTable {
    public static final String RELATIVE_PATH = "quickBlackList";
    public static final String TABLE = "quick_black_list";

    public static final String UID = "_id";
    public static final String CALLER_ID = "caller_id";

    public static final Uri CONTENT_URI = Uri.parse("content://" + QuickBlackListProvider
            .AUTHORITY + "/" +
            RELATIVE_PATH);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + RELATIVE_PATH;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + RELATIVE_PATH;

    public static final int SSID_PATH_POSITION = 1;
}
