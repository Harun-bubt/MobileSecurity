package com.mobilesecurity.mobileapp.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

//import com.abc.security.engine.Bean;

import java.util.ArrayList;
import java.util.List;


public class ContactsEngine {
    private static final String AUTHORITIES_CONTACTS = "com.android.contacts";
    private static final String AUTHORITIES_CALL_LOG = "call_log";
    private static final String AUTHORITIES_SMS = "sms";
    private static final String CONTENT = "content://";

    public static List<ContactBean> readContacts(Context context) {
        List<ContactBean> contacts = new ArrayList<>();
      
        ContentResolver resolver = context.getContentResolver();
       
        Uri contactsUri = Uri.parse(CONTENT + AUTHORITIES_CONTACTS + "/raw_contacts");
        Cursor contactsCursor = resolver.query(contactsUri, new String[]{"contact_id"}, null, null, null);
        if (null == contactsCursor)
            return contacts;
        while (contactsCursor.moveToNext()) {
          
            int contactId = contactsCursor.getInt(0);
         
            ContactBean contact = new ContactBean();
            contact.setId(contactId);
            
            contacts.add(contact);
        }
        contactsCursor.close();

        
        for (ContactBean contact : contacts) {
            Uri dataUri = Uri.parse(CONTENT + AUTHORITIES_CONTACTS + "/data");
            Cursor dataCursor = resolver.query(dataUri, new String[]{"mimetype", "data1"},
                    "raw_contact_id=?", new String[]{contact.getId() + ""}, null);
            while (dataCursor.moveToNext()) {

                String mimetype = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                String data1 = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                if("vnd.android.cursor.item/email_v2".equals(mimetype)) {
                    contact.setEmail(data1);
                } else if("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    contact.setPhone(data1);
                } else if("vnd.android.cursor.item/name".equals(mimetype)) {
                    contact.setName(data1);
                }
            }
            dataCursor.close();
        }
        return contacts;
    }


    public static List<ContactBean> readCallLogContacts(Context context) {
        List<ContactBean> contacts = new ArrayList<ContactBean>();
     
        ContentResolver resolver = context.getContentResolver();
     
        Uri contactsUri = Uri.parse(CONTENT + AUTHORITIES_CALL_LOG + "/calls");
        Cursor cursor = resolver.query(contactsUri, new String[]{"_id", "number", "name"}, null, null, "date desc");
        if (null == cursor)
            return contacts;
        while (cursor.moveToNext()) {
       
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
          
            ContactBean contact = new ContactBean(id, number, null, name);
          
            contacts.add(contact);
        }
        cursor.close();

        return contacts;
    }


    public static List<ContactBean> readSmsContacts(Context context) {
        List<ContactBean> contacts = new ArrayList<ContactBean>();
      
        ContentResolver resolver = context.getContentResolver();
        
        Uri contactsUri = Uri.parse(CONTENT + AUTHORITIES_SMS);
        Cursor cursor = resolver.query(contactsUri, new String[]{"_id", "address", "person"}, null, null, "date desc");
        if (null == cursor)
            return contacts;
        while (cursor.moveToNext()) {
           
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String number = cursor.getString(cursor.getColumnIndex("address"));
            String name = cursor.getString(cursor.getColumnIndex("person"));
           
            ContactBean contact = new ContactBean(id, number, null, name);
          
            contacts.add(contact);
        }
        cursor.close();

        return contacts;
    }

}
