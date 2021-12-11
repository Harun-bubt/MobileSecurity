package com.mobilesecurity.mobileapp.AntiVirus;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobilesecurity.mobileapp.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;



public class ScanningFileSystemAsyncTask extends AsyncTask<Void,ScanningFileSystemAsyncTask.DataToPublish,Void>
{
    IOnActionFinished _asyncTaskCallBack;
    public void setAsyncTaskCallback(IOnActionFinished asyncTaskCallback) { _asyncTaskCallBack=asyncTaskCallback;}

    ImageView _progressPanelIconImageView;
    TextView _progressPanelTextView;
    CircleProgressView _circleProgressBar;
    TextView _bottomMenacesCounterText;
    TextView _bottomScannedAppsText;

    boolean _isPaused=false;
    public void pause() { _isPaused=true;}
    public void resume() { _isPaused=false; }

    class DataToPublish
    {
        public int foundMenaces;
        public int scannedFiles;
        public String appName;
        public Drawable icon;
        public int totalFiles;
    };



    boolean running=true;


    Context _activity;
    List<PackageInfo> _packagesToScan;
    Collection<AppProblem> _menaces;

    public ScanningFileSystemAsyncTask(AntivirusActivity activity, List<PackageInfo> allPackages, Collection<IProblem> menaces)
    {
        _activity=activity;
        _packagesToScan = allPackages;

        _menaces=new ArrayList<AppProblem>();
        for(IProblem p:menaces)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
                _menaces.add((AppProblem)p);
        }

        _progressPanelIconImageView =(ImageView)activity.findViewById(R.id.animationProgressPanelIconImageView);
        _progressPanelTextView =(TextView)activity.findViewById(R.id.animationProgressPanelTextView);;
        _circleProgressBar=(CircleProgressView) activity.findViewById(R.id.circleView);
        _bottomMenacesCounterText=(TextView) activity.findViewById(R.id.bottomFoundMenacesCount);
        _bottomScannedAppsText=(TextView) activity.findViewById(R.id.bottomScannedApp);

        _circleProgressBar.setMaxValue(_packagesToScan.size());
    }


    DataToPublish dtp=new DataToPublish();

    @Override
    protected void onPreExecute()
    {
    }

    @Override
    protected void onCancelled()
    {
        running = false;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        int currentIndex=0;

        try
        {
            PackageInfo pi=null;
            int scannedApps=0;

            while(running && currentIndex<_packagesToScan.size())
            {
                Thread.sleep(100);

                if(!_isPaused)
                {
                    pi = _packagesToScan.get(currentIndex);

                    dtp.scannedFiles = currentIndex;
                    dtp.appName = pi.packageName;
                    dtp.icon = StaticTools.getIconFromPackage(dtp.appName, _activity);
                    dtp.totalFiles = _packagesToScan.size();

                    boolean b = isPackageInMenacesSet(dtp.appName);
                    if (b)
                        dtp.foundMenaces++;

                    publishProgress(dtp);

                    ++scannedApps;
                    ++currentIndex;
                }
            }
        }
        catch(InterruptedException ex)
        {
            Log.w("APP", "Scanning task was interrupted");
        }

        return null;
    }

    boolean isPackageInMenacesSet(String packageName)
    {
        for(AppProblem menace : _menaces)
        {
            if(menace.getPackageName().equals(packageName))
                return true;
        }

        return false;
    }

    @Override
    protected void onProgressUpdate(DataToPublish... params)
    {
        DataToPublish dtp=params[0];

        _progressPanelIconImageView.setImageDrawable(dtp.icon);
        _progressPanelTextView.setText(dtp.appName);
        _circleProgressBar.setValue(dtp.scannedFiles);
        _bottomMenacesCounterText.setText(""+dtp.foundMenaces);
        _bottomScannedAppsText.setText(""+dtp.scannedFiles);
    }

    @Override
    protected void onPostExecute(Void result)
    {

        _circleProgressBar.setValue(_circleProgressBar.getMaxValue());

        if(_asyncTaskCallBack!=null)
            _asyncTaskCallBack.onFinished();
    }
}
