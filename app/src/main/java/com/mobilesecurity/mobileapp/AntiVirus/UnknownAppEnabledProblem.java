package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.mobilesecurity.mobileapp.R;

public class UnknownAppEnabledProblem extends  SystemProblem
{

    static public final String kSerializationType="unknownApp";


    final int kUnknownAppIconResId = R.drawable.information;
    final int kUnknownAppTitleId =R.string.system_app_unknown_app_menace_title;
    final int kWhiteListRemoveText=R.string.unknownApp_remove_whitelist_message;


    public UnknownAppEnabledProblem()
    {


    }


    public ProblemType getType() { return ProblemType.SystemProblem;}

    public String getSerializationTypeString() {return kSerializationType; }


    public String getWhiteListOnRemoveDescription(Context context)
    {
        return context.getString(kWhiteListRemoveText);
    }

    public String getTitle(Context context)
    {
        return context.getString(kUnknownAppTitleId);
    }
    public String getSubTitle(Context context) {return context.getString(R.string.usb_title);}

    public String getDescription(Context context)
    {
        return context.getString(R.string.unknownApp_message);
    }

    public Drawable getIcon(Context context)
    {
        return ContextCompat.getDrawable(context, kUnknownAppIconResId);
    }
    public Drawable getSubIcon(Context context) {return ContextCompat.getDrawable(context,R.drawable.gear);}

    public void doAction(Context context)
    {
        StaticTools.openSecuritySettings(context);

    }
    public boolean isDangerous()
    {
        return false;
    }

    public boolean problemExists(Context context)
    {
        return StaticTools.checkIfUnknownAppIsEnabled(context);
    }
}
