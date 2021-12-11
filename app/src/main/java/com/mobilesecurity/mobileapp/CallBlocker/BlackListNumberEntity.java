package com.mobilesecurity.mobileapp.CallBlocker;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.mobilesecurity.mobileapp.db.BlackListTable;


public class BlackListNumberEntity {
    private Long uid;
    private String normalizedNumber = "";
    private String displayNumber = "";
    private String displayName = "";
    private boolean beginWith = false;
    private boolean enabled = true;

    public BlackListNumberEntity() {
    }

    public BlackListNumberEntity(Cursor cur) {
        for (String colName : cur.getColumnNames()) {
            int colIndex = cur.getColumnIndex(colName);

            switch (colName) {
                case BlackListTable.NORMALIZED_NUMBER:
                    this.normalizedNumber = (cur.getString(colIndex));
                    continue;
                case BlackListTable.DISPLAY_NUMBER:
                    this.displayNumber = (cur.getString(colIndex));
                    continue;
                case BlackListTable.BEGIN_WITH:
                    this.beginWith = (cur.getInt(colIndex) != 0);
                    continue;
                case BlackListTable.ENABLED:
                    this.enabled = (cur.getInt(colIndex) != 0);
                    continue;
                case BlackListTable.UID:
                    this.uid = cur.getLong(colIndex);
                    continue;
                case BlackListTable.DISPLAY_NAME:
                    this.displayName = cur.getString(colIndex);
            }
        }
    }

    public boolean isBeginWith() {
        return beginWith;
    }

    public void setBeginWith(boolean beginWith) {
        this.beginWith = beginWith;
    }

    public String getNormalizedNumber() {
        return normalizedNumber;
    }

    public void setNormalizedNumber(String normalizedNumber) {
        this.normalizedNumber = normalizedNumber;
    }

    public String getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(String displayNumber) {
        this.displayNumber = displayNumber;
    }

    @SuppressWarnings("unused")
    public Long getUid() {
        return uid;
    }

    @SuppressWarnings("unused")
    public void setUid(Long uid) {
        this.uid = uid;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void toContentValues(@NonNull ContentValues contentValue) {
        if (uid != null) contentValue.put(BlackListTable.UID, uid);
        contentValue.put(BlackListTable.BEGIN_WITH, beginWith ? "1" : "0");
        contentValue.put(BlackListTable.ENABLED, enabled ? "1" : "0");
        contentValue.put(BlackListTable.DISPLAY_NUMBER, displayNumber);
        contentValue.put(BlackListTable.DISPLAY_NAME, displayName);
        contentValue.put(BlackListTable.NORMALIZED_NUMBER, normalizedNumber);
    }

    public Uri getUniqueUri() {
        if (uid == null) {
            throw new IllegalStateException("uid is null");
        }
        return ContentUris.withAppendedId(BlackListTable.CONTENT_URI, uid);
    }

    @Override
    public String toString() {
        return "BlackListNumberEntity{" +
                "beginWith=" + beginWith +
                ", uid=" + uid +
                ", normalizedNumber='" + normalizedNumber + '\'' +
                ", displayNumber='" + displayNumber + '\'' +
                ", displayName='" + displayName + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
