package com.mobilesecurity.mobileapp.AntiVirus;

import org.json.JSONException;
import org.json.JSONObject;

public class PackageData
{
    private String _packageName;
    public String getPackageName() { return _packageName; }
    public void setPackageName(String packageName) { _packageName=packageName;}

    public PackageData() {}

    public PackageData(String packageName)
    {
        setPackageName(packageName);
    }

    public int hashCode()
    {
        return (int) _packageName.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == null || o.getClass()!=this.getClass())
            return false;

        PackageData other = (PackageData) o;
        return _packageName.equals(other._packageName);
    }

    public JSONObject buildJSONObject() throws JSONException
    {
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("packageName",_packageName);
        return jsonObj;
    }




}
