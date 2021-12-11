package com.mobilesecurity.mobileapp.AntiVirus;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import com.mobilesecurity.mobileapp.R;
import java.util.ArrayList;
import java.util.List;


public class ResultsFragment extends Fragment
{

    AntivirusActivity getMainActivity() {return (AntivirusActivity) getActivity();}

    List<IProblem> _problems =null;

    private ListView _listview;

    ResultsAdapter _resultAdapter=null;

    TextView _threatsFoundSummary=null;

    public void setData(AntivirusActivity antivirusActivity, List<IProblem> problems)
    {
        _problems =problems;
        _resultAdapter=new ResultsAdapter(antivirusActivity, problems);

        _resultAdapter.setResultItemSelectedStateChangedListener(new IResultItemSelectedListener()
        {
            @Override
            public void onItemSelected(IProblem bpd)
            {
                showInfoAppFragment(bpd);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.results_fragment, container, false);

        _threatsFoundSummary = (TextView) rootView.findViewById(R.id.counterApps);

        _setupFragment(rootView);

        return rootView;
    }



    protected void _setupFragment(View view)
    {
        _listview = (ListView) view.findViewById(R.id.list);
        _listview.setAdapter(_resultAdapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        MenacesCacheSet menacesCache = getMainActivity().getMenacesCacheSet();


        getMainActivity().updateMenacesAndWhiteUserList();

        //Add new existant apps
        _resultAdapter.refreshByProblems(new ArrayList<IProblem>(menacesCache.getSet()));

        _updateFoundThreatsText(_threatsFoundSummary, _resultAdapter.getRealCount());

        if(menacesCache.getItemCount()<=0)
        {
            getMainActivity().goBack();
        }
    }

    void _updateFoundThreatsText(TextView textView, int appCount)
    {
        String finalStr=getString(R.string.threats_found);
        finalStr=StaticTools.fillParams(finalStr, "#", Integer.toString(appCount));
        textView.setText(finalStr);
    }

    void showInfoAppFragment(final IProblem problem)
    {
        InfoAppFragment newFragment =(InfoAppFragment) getMainActivity().slideInFragment(AntivirusActivity.kInfoFragmnetTag);

        newFragment.setData(problem);


    }
}

