package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.R;

public class InfoAppFragment extends Fragment
{

    public static ListView _listview;

    IProblem _problem = null;
    boolean  _uninstallingPackage = false;
    private LinearLayout _containerButtonsApp = null;
    private LinearLayout _containerButtonsSystem= null;


    private Button _uninstallButton = null;

    AntivirusActivity getMainActivity()
    {
        return (AntivirusActivity) getActivity();
    }

    public void setData(IProblem suspiciousAppList)
    {
        _problem = suspiciousAppList;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.app_info_fragment, container, false);

        _setupFragment(rootView);
        return rootView;
    }

    protected void _setupFragment(View view)
    {
        _containerButtonsApp = (LinearLayout) view.findViewById(R.id.buttonsAppContainer);
        _containerButtonsSystem = (LinearLayout) view.findViewById(R.id.buttonsConfigContainer);

        TextView appTitleTextView = (TextView) view.findViewById(R.id.titleApp);
        TextView warningLevel = (TextView) view.findViewById(R.id.warningLevel);
        ImageView iconApp = (ImageView) view.findViewById(R.id.iconGeneral);
        _listview = (ListView) view.findViewById(R.id.listView);

        if(_problem.isDangerous())
        {
            warningLevel.setTextColor(ContextCompat.getColor(getContext(),R.color.HighRiskColor));
            warningLevel.setText(R.string.high_risk);
        }
        else
        {
            warningLevel.setTextColor(ContextCompat.getColor(getContext(),R.color.MediumRiskColor));
            warningLevel.setText(R.string.medium_risk);
        }

        if(_problem.getType()== IProblem.ProblemType.AppProblem)
        {
            _containerButtonsApp.setVisibility(View.VISIBLE);
            _containerButtonsSystem.setVisibility(View.GONE);

            final AppProblem appProblem=(AppProblem) _problem;

            Drawable s = StaticTools.getIconFromPackage(appProblem.getPackageName(), getContext());
            _uninstallButton = (Button) view.findViewById(R.id.buttonUninstall);
            final Button buttonTrust = (Button) view.findViewById(R.id.buttonTrust);


            _uninstallButton.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                    _uninstallingPackage = true;
                    Uri uri = Uri.fromParts("package", appProblem.getPackageName(), null);
                    Intent it = new Intent(Intent.ACTION_DELETE, uri);
                    startActivity(it);
                    _uninstallButton.setEnabled(false);
                }
            });

            buttonTrust.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    buttonTrust.setEnabled(false);

                    new AlertDialog.Builder(getContext())
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.dialog_trust_app))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    UserWhiteList userWhiteList = getMainActivity().getUserWhiteList();

                                    userWhiteList.addItem(appProblem);
                                    userWhiteList.writeToJSON();

                                    MenacesCacheSet menacesCacheSet = getMainActivity().getMenacesCacheSet();
                                    menacesCacheSet.removeItem(appProblem);
                                    menacesCacheSet.writeToJSON();


                                   getMainActivity().goBack();
                                    buttonTrust.setEnabled(true);
                                }
                            }).setNegativeButton("no", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            buttonTrust.setEnabled(true);
                        }
                    }).show();

                }
            });

            appTitleTextView.setText(StaticTools.getAppNameFromPackage(getContext(), appProblem.getPackageName()));
            iconApp.setImageDrawable(s);
        }
        else
        {
            _containerButtonsApp.setVisibility(View.GONE);
            _containerButtonsSystem.setVisibility(View.VISIBLE);

            final SystemProblem systemProblem=(SystemProblem) _problem;

            Drawable s = systemProblem.getIcon(getContext());
            iconApp.setImageDrawable(s);
            appTitleTextView.setText(systemProblem.getTitle(getContext()));


            Button buttonOpenSetting = (Button) view.findViewById(R.id.buuttonOpenSettings);
            Button buttonIgnoredSetting = (Button) view.findViewById(R.id.buttonIgnoreConfig);

            buttonOpenSetting.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    systemProblem.doAction(getContext());
                }
            });


            buttonIgnoredSetting.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    UserWhiteList _userWhiteList = getMainActivity().getUserWhiteList();
                    _userWhiteList.addItem(_problem);
                    _userWhiteList.writeToJSON();

                    MenacesCacheSet menaceCacheSet= getMainActivity().getMenacesCacheSet();
                    menaceCacheSet.removeItem(_problem);
                    menaceCacheSet.writeToJSON();

                    getMainActivity().goBack();
                }
            });
        }

        _listview.setAdapter(new WarningsAdapter(getMainActivity(), _problem));

    }

    @Override
    public void onResume()
    {
        super.onResume();

        AntivirusActivity antivirusActivity=getMainActivity();

        if( _uninstallingPackage==true)
        {
            if(_problem!=null)
            {
                final AppProblem appProblem=(AppProblem) _problem;

                if (!StaticTools.isPackageInstalled(getMainActivity(), appProblem.getPackageName()))
                {
                  MenacesCacheSet menacesCacheSet = antivirusActivity.getMenacesCacheSet();
                    menacesCacheSet.removeItem(appProblem);
                    menacesCacheSet.writeToJSON();
                }
            }

            _uninstallingPackage=false;
            getMainActivity().goBack();

        }
        else if(_problem.getType()== IProblem.ProblemType.AppProblem)
        {
            MenacesCacheSet menacesCacheSet=antivirusActivity.getMenacesCacheSet();
            final AppProblem appProblem=(AppProblem) _problem;
            if(!ProblemsDataSetTools.checkIfPackageInCollection(appProblem.getPackageName(), menacesCacheSet.getSet()))
            {
                if(!StaticTools.isPackageInstalled(antivirusActivity, appProblem.getPackageName()))
                {
                    menacesCacheSet = antivirusActivity.getMenacesCacheSet();
                    menacesCacheSet.removeItem(appProblem);
                    menacesCacheSet.writeToJSON();

                    antivirusActivity.goBack();
                }
            }
        }
        else if(_problem.getType()== IProblem.ProblemType.SystemProblem)
        {
            MenacesCacheSet menacesCacheSet=antivirusActivity.getMenacesCacheSet();
            final SystemProblem systemProblem=(SystemProblem) _problem;
            if(!systemProblem.problemExists(getContext()))
            {
                menacesCacheSet = antivirusActivity.getMenacesCacheSet();
                menacesCacheSet.removeItem(systemProblem);
                menacesCacheSet.writeToJSON();

                antivirusActivity.goBack();
            }
        }

       if(_uninstallButton!=null)
            _uninstallButton.setEnabled(true);
    }

}
