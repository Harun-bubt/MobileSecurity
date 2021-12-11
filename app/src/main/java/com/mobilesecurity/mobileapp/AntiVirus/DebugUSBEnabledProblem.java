package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.mobilesecurity.mobileapp.R;

public class DebugUSBEnabledProblem extends SystemProblem
{
    static public final String kSerializationType="usb";



    final int kUsbIconResId= R.drawable.usb_icon;
    final int kUsbDescriptionId=R.string.usb_message;
    final int kUsbTitleId=R.string.system_app_usb_menace_title;
    final int kWhiteListAddText= R.string.usb_add_whitelist_message;
    final int kWhiteListRemoveText=R.string.usb_remove_whitelist_message;


   public DebugUSBEnabledProblem()
    {
    }


    public ProblemType getType() { return ProblemType.SystemProblem;}

    public String getSerializationTypeString() {return kSerializationType; }


    public String getWhiteListOnAddDescription(Context context)
    {
        return context.getString(kWhiteListAddText);
    }

    public String getWhiteListOnRemoveDescription(Context context)
    {
        return context.getString(kWhiteListRemoveText);
    }

    public String getTitle(Context context)
    {
        return context.getString(kUsbTitleId);
    }
    public String getSubTitle(Context context)
    {
        return context.getString(R.string.usb_title);
    }

    public String getDescription(Context context)
    {
        return context.getString(kUsbDescriptionId);
    }

    public Drawable getIcon(Context context)
    {
        return ContextCompat.getDrawable(context, kUsbIconResId);
    }

    public Drawable getSubIcon(Context context)
    {
        return ContextCompat.getDrawable(context, R.drawable.gear);
    }

    public void doAction(Context context)
    {
        StaticTools.openDeveloperSettings(context);

    }

    public boolean isDangerous()
    {
        return false;
    }

    public boolean problemExists(Context context)
    {
        return StaticTools.checkIfUSBDebugIsEnabled(context);
    }
    
}
