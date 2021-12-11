package com.mobilesecurity.mobileapp.engine;

import android.content.Context;

import com.mobilesecurity.mobileapp.dao.PhoneLocationDao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneLocationEngine {
    private static final String REX_CELLPHONE = "^1[34578]\\d{9}$";
    private static final String REX_TELEPHONE = "^(0\\d{2,3}-?\\s?)\\d{7,8}(\\d{1,4})?";

    private Pattern cellphonePattern = Pattern.compile(REX_CELLPHONE);
    private Pattern telephonePattern = Pattern.compile(REX_TELEPHONE);

    public enum PhoneType {
        CELL, TELE, UNKNOWN
    }


    public String getLocation(Context context, String number) {
        PhoneLocationDao dao = new PhoneLocationDao(context);
        String location = "";
        PhoneType phoneType = matchPhone(number);
        switch (phoneType) {
            case CELL:
                location = dao.queryCellphoneLocation(number.substring(0, 7));
                break;
            case TELE:
                location = dao.queryTelephoneLocation(getTelephoneAreaNumber(number));
                break;
        }
        return location;
    }

    public PhoneType matchPhone(String number) {
        Matcher cellphoneMatcher = cellphonePattern.matcher(number);
        if(cellphoneMatcher.matches()) {
            return PhoneType.CELL;
        }
        Matcher telephoneMatcher = telephonePattern.matcher(number);
        if(telephoneMatcher.matches()) {
            return PhoneType.TELE;
        }
        return PhoneType.UNKNOWN;
    }

    public static String getTelephoneAreaNumber(String number){

        if(number.length() < 10)
            return "";
        if(number.charAt(1) == '1' || number.charAt(1) == '2') {
  
            return number.substring(0, 3);
        }
        return number.substring(0, 4);
    }

}
