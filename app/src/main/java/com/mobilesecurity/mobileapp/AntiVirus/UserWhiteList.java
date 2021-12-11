package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;
import java.util.Set;


public class UserWhiteList extends JSONDataSet<IProblem>
{
    public UserWhiteList(Context context)
    {
        super(context,"userwhitelist.json",new ProblemFactory());
    }

    boolean checkIfSystemPackageInList(Class<?> type)
    {
        Set<IProblem> problems=getSet();

        SystemProblem problem=null;

        for(IProblem p : problems)
        {
            if(p.getType()== IProblem.ProblemType.SystemProblem)
            {
                problem=(SystemProblem) p;
                if(problem.getClass()==type)
                    return true;
            }
        }

        return false;
    }
}
