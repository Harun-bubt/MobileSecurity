package com.mobilesecurity.mobileapp.AntiVirus;


import android.content.Context;

public interface IProblem extends IJSONSerializer
{
    enum ProblemType { AppProblem, SystemProblem}

    public ProblemType getType();
    public boolean isDangerous();
    public boolean problemExists(Context context);
}


