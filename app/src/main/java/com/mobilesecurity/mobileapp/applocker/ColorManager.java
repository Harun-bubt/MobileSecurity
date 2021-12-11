package com.mobilesecurity.mobileapp.applocker;

import android.content.Context;

import com.mobilesecurity.mobileapp.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ColorManager {
    Context context;
    public static String PACKAGE_NAME;
    ColorManager(Context context1){
        context = context1;
    }
    public String getColor(){
        PACKAGE_NAME = BuildConfig.APPLICATION_ID;
        String Shine = "/data/data/";
        String Url = "/files/color";

        File file = new File(Shine+PACKAGE_NAME+Url);

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {

        }
        return text.toString();
    }
    public boolean isLight(){
        return getColor().equals(Statics.LIGHT);
    }

}
