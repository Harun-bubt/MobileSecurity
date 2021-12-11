package com.mobilesecurity.mobileapp.AntiVirus;


public class ActivityData
{
    private String _package;
    public String getPackage() {return _package;}

    public ActivityData(String packageName)
    {
        _package=packageName;
    }

    public int hashCode()
    {
        return (int) _package.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == null)
            return false;

        ActivityData other = (ActivityData) o;
        return  _package.equals(other._package);
    }
}
