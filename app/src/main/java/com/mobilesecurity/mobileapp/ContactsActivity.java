package com.mobilesecurity.mobileapp;

import android.Manifest;

import com.mobilesecurity.mobileapp.base.BaseContactsActivity;
import com.mobilesecurity.mobileapp.engine.ContactBean;
import com.mobilesecurity.mobileapp.engine.ContactsEngine;

import java.util.List;


public class ContactsActivity extends BaseContactsActivity {

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_CONTACTS
        };
    }

    @Override
    protected List<ContactBean> getContactDatas() {
        return ContactsEngine.readContacts(ContactsActivity.this);
    }
}
