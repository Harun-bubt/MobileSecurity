
package com.mobilesecurity.mobileapp.db;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobilesecurity.mobileapp.BuildConfig;

//import com.abc.security.BuildConfig;

public class LogProvider extends AppProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".log";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int LOG = 1;
    private static final int LOG_ID = 2;

    static {
        sURIMatcher.addURI(AUTHORITY, LogTable.RELATIVE_PATH, LOG);
        sURIMatcher.addURI(AUTHORITY, LogTable.RELATIVE_PATH + "/#", LOG_ID);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[]
            selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sURIMatcher.match(uri)) {
            case LOG:
                qb.setTables(LogTable.TABLE);
                break;
            case LOG_ID:
                qb.setTables(LogTable.TABLE);
                String uid = uri.getPathSegments().get(LogTable.SSID_PATH_POSITION);
                qb.appendWhereEscapeString(LogTable.UID + "=" + uid);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sURIMatcher.match(uri)) {
            case LOG:
                return LogTable.CONTENT_TYPE;
            case LOG_ID:
                return LogTable.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (sURIMatcher.match(uri)) {
            case LOG:
                return insertLogList(uri, values);
            case LOG_ID:
                throw new IllegalArgumentException("Pointless using ID for new inserts: " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private Uri insertLogList(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(LogTable.TABLE, null, values);

        if (rowId > 0) {
            Uri logUri = ContentUris.withAppendedId(LogTable.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(logUri, null);
            return logUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        switch (sURIMatcher.match(uri)) {
            case LOG:
                return deleteBySelection(uri, selection, selectionArgs);
            case LOG_ID:
                return deleteByID(uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private int deleteBySelection(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(LogTable.TABLE, selection, selectionArgs);
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    private int deleteByID(Uri uri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = ContentUris.parseId(uri);
        int rows = db.delete(LogTable.TABLE, LogTable.UID + "=?", new String[]{Long.toString(id)});
        if (rows > 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[]
            selectionArgs) {
        throw new SQLException("Updates are not supported. " + uri);
    }
}
