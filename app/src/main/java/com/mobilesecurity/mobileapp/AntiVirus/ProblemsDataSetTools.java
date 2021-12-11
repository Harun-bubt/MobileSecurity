package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class ProblemsDataSetTools
{



    public static Collection<? extends IProblem> getAppProblems(Collection<? extends IProblem> problems, Collection<IProblem> target)
    {
        for(IProblem p: problems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
                target.add(p);
        }

        return target;
    }

    public static Collection<? extends IProblem> getSystemProblems(Collection<? extends IProblem> problems, Collection<IProblem> target)
    {
        for(IProblem p: problems)
        {
            if(p.getType()== IProblem.ProblemType.SystemProblem)
                target.add(p);
        }

        return target;
    }

    public static Set<PackageData> getAppProblemsAsPackageDataList(IDataSet<? extends IProblem> problems)
    {
        Set<PackageData> pd=new HashSet<PackageData>();

        Set<? extends IProblem> colProblems=problems.getSet();

        for(IProblem p: colProblems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
                pd.add((AppProblem)p);
        }

        return pd;
    }

    static boolean checkIfPackageInCollection(String packageName, Collection<IProblem> problems)
    {
        for(IProblem p : problems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {
                if(((AppProblem)p).getPackageName().equals(packageName))
                    return true;
            }
        }

        return false;
    }

    static boolean removeNotExistingProblems(Context context, IDataSet<IProblem> dataSet)
    {
        boolean dirty=false;

        ArrayList<IProblem> toRemove=new ArrayList<IProblem>();

        Set<IProblem> problems=dataSet.getSet();

        for(IProblem p: problems)
        {
            if(!p.problemExists(context))
            {
                toRemove.add(p);
                dirty=true;
            }
        }

        problems.removeAll(toRemove);

        return dirty;
    }


    public static void printProblems(IDataSet<IProblem> dataSet)
    {
      for (IProblem p : dataSet.getSet())
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {

                }
            else
            {

               }
        }
    }

}
