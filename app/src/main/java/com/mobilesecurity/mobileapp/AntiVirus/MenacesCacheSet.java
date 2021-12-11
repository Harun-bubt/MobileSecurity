package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;

public class MenacesCacheSet extends JSONDataSet<IProblem>
{
    public MenacesCacheSet(Context context)
    {
        super(context,"menacescache.json",new ProblemFactory());
    }
}
