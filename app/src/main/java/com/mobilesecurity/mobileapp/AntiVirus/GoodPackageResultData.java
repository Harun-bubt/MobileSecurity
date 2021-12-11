package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.pm.PackageInfo;


public class GoodPackageResultData
{
    public String getPackageName() { return _packageInfo.packageName; }

    private PackageInfo _packageInfo;

    public GoodPackageResultData(PackageInfo packageInfo)
    {
        _packageInfo=packageInfo;
    }


    public int hashCode()
    {
        return (int) getPackageName().hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        GoodPackageResultData other = (GoodPackageResultData) o;
        return _packageInfo.packageName.equals(other._packageInfo);
    }
}
