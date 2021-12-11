package com.mobilesecurity.mobileapp.AntiVirus;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.mobilesecurity.mobileapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MonitorShieldService extends Service
{
    final String _logTag=MonitorShieldService.class.getSimpleName();

    private final IBinder _binder=new MonitorShieldLocalBinder();

    PackageBroadcastReceiver _packageBroadcastReceiver;

    Set<PackageData> _whiteListPackages;
    Set<PackageData> _blackListPackages;
    Set<PackageData> _blackListActivities;
    Set<PermissionData> _suspiciousPermissions;
    UserWhiteList _userWhiteList=null;
    public UserWhiteList getUserWhiteList() { return _userWhiteList;}
    MenacesCacheSet _menacesCacheSet =null;
    public MenacesCacheSet getMenacesCacheSet() { return _menacesCacheSet; }
    private int _appIcon = R.mipmap.ic_launcher;
    IClientInterface _clientInterface=null;
    public void registerClient(IClientInterface clientInterface) { _clientInterface=clientInterface;}

    static int _currentNotificationId=0;
    @Override
    public void onCreate()
    {
        super.onCreate();

      _packageBroadcastReceiver = new PackageBroadcastReceiver();
        _packageBroadcastReceiver.setPackageBroadcastListener(new IPackageChangesListener()
        {

            public void OnPackageAdded(Intent i)
            {
                String packageName = i.getData().getSchemeSpecificPart();
                scanApp(packageName);
            }

            public void OnPackageRemoved(Intent intent)
            {

            }
        });

        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);

        packageFilter.addDataScheme("package");
        this.registerReceiver(_packageBroadcastReceiver, packageFilter);

        _loadDataFiles();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(_packageBroadcastReceiver);
        _packageBroadcastReceiver = null;

    }

    @Override
    public IBinder onBind(Intent i)
    {
        return _binder;
    }

    public class MonitorShieldLocalBinder extends Binder
    {
        public MonitorShieldService getServiceInstance()
        {
            return MonitorShieldService.this;
        }
    }

    public interface IClientInterface
    {
        public void onMonitorFoundMenace(IProblem menace);
        public void onScanResult(List<PackageInfo> allPackages, Set<IProblem> menacesFound);
    }

    private void _loadDataFiles()
    {

        _whiteListPackages=new HashSet<PackageData>();
        _blackListPackages=new HashSet<PackageData>();
        _blackListActivities=new HashSet<PackageData>();
        _suspiciousPermissions= new HashSet<PermissionData>();

        _userWhiteList=new UserWhiteList(this);
        _menacesCacheSet = new MenacesCacheSet(this);

        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "whiteList.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _whiteListPackages.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "blackListPackages.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _blackListPackages.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "blackListActivities.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _blackListActivities.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "permissions.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PermissionData pd=new PermissionData(temp.getString("permissionName"),temp.getInt("dangerous"));
                _suspiciousPermissions.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }


    public void scanFileSystem()
    {
        List<PackageInfo> allPackages= StaticTools.getApps(this, PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
        List<PackageInfo> nonSystemApps=StaticTools.getNonSystemApps(this,allPackages);

        //Packages with problems will be stored here
        Set<IProblem> tempBadResults=new HashSet<IProblem>();

        //Filter white listed apps
        List<PackageInfo> potentialBadApps=_removeWhiteListPackagesFromPackageList(nonSystemApps, _whiteListPackages);
        potentialBadApps=_removeWhiteListPackagesFromPackageList(potentialBadApps, ProblemsDataSetTools.getAppProblemsAsPackageDataList(_userWhiteList));

        Scanner.scanForBlackListedActivityApps(potentialBadApps, _blackListActivities, tempBadResults);
        Scanner.scanForSuspiciousPermissionsApps(potentialBadApps, _suspiciousPermissions, tempBadResults);
        Scanner.scanInstalledAppsFromGooglePlay(this, potentialBadApps, tempBadResults);
        Scanner.scanSystemProblems(this, _userWhiteList, tempBadResults);

        Log.e(_logTag, "----------------------> Numero de aplicciones escaneadas: " + allPackages.size());

        _menacesCacheSet.addItems(tempBadResults);
        _menacesCacheSet.writeToJSON();

        if(_clientInterface!=null)
            _clientInterface.onScanResult(allPackages,tempBadResults);

    }

    public void scanApp(String packageName)
    {
        AppData appData=AppData.getInstance(this);

        Intent toExecuteIntent = new Intent(MonitorShieldService.this, AntivirusActivity.class);

        Intent openAppIntent = getPackageManager().getLaunchIntentForPackage(packageName);

        String appName = StaticTools.getAppNameFromPackage(MonitorShieldService.this, packageName);

        boolean whiteListed=Scanner.isAppWhiteListed(packageName, _whiteListPackages);

        if(whiteListed)
        {
            StaticTools.notificatePush(MonitorShieldService.this,_currentNotificationId++, _appIcon,
                    appName + " " + getString(R.string.trusted_message), appName, "App " + appName + " " + getString(R.string.trusted_by_app), openAppIntent);
        }
        else
        { PackageInfo pi=null;
                try
                {
                    pi=StaticTools.getPackageInfo(this,packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
                }
                catch(PackageManager.NameNotFoundException ex)
                {
                    pi=null;
                }

                if(pi!=null)
                {
                    AppProblem bpbr=new AppProblem(pi.packageName);
                    List<ActivityInfo> recycleList=new ArrayList<ActivityInfo>();
                    Scanner.scanForBlackListedActivityApp(pi, bpbr, _blackListActivities, recycleList);
                    Scanner.scanForSuspiciousPermissionsApp(pi, bpbr, _suspiciousPermissions);
                    Scanner.scanInstalledAppFromGooglePlay(this, bpbr);

                    if(bpbr.isMenace())
                    {
                        //Do not scan if we haven't done any
                        if(appData.getFirstScanDone())
                        {
                            _menacesCacheSet.addItem(bpbr);
                            _menacesCacheSet.writeToJSON();
                        }

                        if(_clientInterface!=null)
                        {
                            _clientInterface.onMonitorFoundMenace(bpbr);
                        }

                        StaticTools.notificatePush(MonitorShieldService.this, _currentNotificationId++, _appIcon,
                                appName + " " + getString(R.string.has_been_scanned), appName, getString(R.string.enter_to_solve_problems), toExecuteIntent);

                    }
                    else
                        StaticTools.notificatePush(MonitorShieldService.this, _currentNotificationId++, _appIcon,
                            appName + " " + getString(R.string.is_secure), appName, getString(R.string.has_no_threats), toExecuteIntent);
                }
            //}
        }
    }

    protected List<PackageInfo> _removeWhiteListPackagesFromPackageList(List<PackageInfo> packagesToSearch, Set<? extends PackageData> whiteListPackages)
    {
        boolean found=false;

        List<PackageInfo> trimmedPackageList=new ArrayList<PackageInfo>(packagesToSearch);

        //Check against whitelist
        for(PackageData pd : whiteListPackages)
        {
            PackageInfo p = null;
            int index = 0;
            String mask = pd.getPackageName();
            found = false;

            while (found == false && index < trimmedPackageList.size())
            {
                p = trimmedPackageList.get(index);

                if (StaticTools.stringMatchesMask(p.packageName, mask))
                    trimmedPackageList.remove(index);
                else
                    ++index;
            }
        }

        return trimmedPackageList;
    }
}
