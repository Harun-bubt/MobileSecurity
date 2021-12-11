package com.mobilesecurity.mobileapp.AntiVirus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.IBinder;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import android.view.WindowManager;


import com.mobilesecurity.mobileapp.Internetconnection;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import com.mobilesecurity.mobileapp.R;
import com.google.android.gms.ads.InterstitialAd;
import com.mobilesecurity.mobileapp.base.BaseSwipeBackActivity;

import org.joda.time.DateTime;
import org.joda.time.Days;

import static com.mobilesecurity.mobileapp.Constant.UpgradePro;

public class AntivirusActivity extends BaseSwipeBackActivity implements MonitorShieldService.IClientInterface
{
    public static final String kMainFragmentTag="MainFragmentTag";
    public static final String kResultFragmentTag="ResultFragmentTag";
    public static final String kInfoFragmnetTag="InfoFragmentTag";
    public static final String kIgnoredFragmentTag="IgnoredFragmentTag";
    InterstitialAd mInterstitial;
    Menu _menu=null;
    public Menu getMenu() {return _menu;}

    InterstitialAd _interstitialAd=null;
    public void setInterstitialListener(AdListener listener)
    {
        if(_interstitialAd!=null)
            _interstitialAd.setAdListener(listener);
    }

    public void showInterstitial()
    {
        _interstitialAd.show();
        _requestNewInterstitial();
    }
    private void _requestNewInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        _interstitialAd.loadAd(adRequest);
    }


    public MainFragment getMainFragment()
    {
        FragmentManager fm= getSupportFragmentManager();
        MainFragment f= (MainFragment) fm.findFragmentByTag(kMainFragmentTag);

        if(f==null)
            return new MainFragment();
        else
            return f;
    }

    public ResultsFragment getResultFragment()
    {
        FragmentManager fm= getSupportFragmentManager();
        ResultsFragment f= (ResultsFragment) fm.findFragmentByTag(kResultFragmentTag);

        if(f==null)
            return new ResultsFragment();
        else
            return f;
    }

    public InfoAppFragment getInfoFragment()
    {
        FragmentManager fm= getSupportFragmentManager();
        InfoAppFragment f= (InfoAppFragment) fm.findFragmentByTag(kInfoFragmnetTag);

        if(f==null)
            return new InfoAppFragment();
        else
            return f;
    }

    public IgnoredListFragment getIgnoredFragment()
    {
        FragmentManager fm= getSupportFragmentManager();
        IgnoredListFragment f= (IgnoredListFragment) fm.findFragmentByTag(kIgnoredFragmentTag);

        if(f==null)
            return new IgnoredListFragment();
        else
            return f;
    }


    public boolean canShowAd()
    {
        AppData appData=getAppData();

        DateTime today=new DateTime();
        int diffDays= Days.daysBetween(appData.getLastAdDate(),new DateTime()).getDays();

        if(diffDays>0)
        {
            appData.setLastAdDate(today);
            appData.serialize(this);
            return true;
        }
        else
            return false;
    }



    public UserWhiteList getUserWhiteList()
    {
        return _serviceInstance.getUserWhiteList();
    }
    public Set<IProblem> getProblemsFromMenaceSet() { return _serviceInstance.getMenacesCacheSet().getSet(); }
    public MenacesCacheSet getMenacesCacheSet() { return _serviceInstance.getMenacesCacheSet(); }
    public void updateMenacesAndWhiteUserList()
    {
        //Remove not existent problems
        ProblemsDataSetTools.removeNotExistingProblems(this, getUserWhiteList());
        ProblemsDataSetTools.removeNotExistingProblems(this, getMenacesCacheSet());

        //Add existent system problems
        Scanner.scanSystemProblems(this, getUserWhiteList(), getMenacesCacheSet().getSet());
    }


    String _logTag=AntivirusActivity.class.getSimpleName();

    MonitorShieldService _serviceInstance=null;

    boolean _bound=false;
    private ServiceConnection _serviceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            _bound=true;


            MonitorShieldService.MonitorShieldLocalBinder binder = (MonitorShieldService.MonitorShieldLocalBinder) service;
            _serviceInstance = binder.getServiceInstance(); //Get getInstance of your service!

            if(_serviceInstance!=null)
                new Exception("Service instance is null. At it can't be!!!!");

            _serviceInstance.registerClient(AntivirusActivity.this); //Activity register in the service as client for callabcks!

            //Now that service is active run fragment to init it
            FragmentManager fm=getSupportFragmentManager();
            if(fm.getBackStackEntryCount()<=0)
                slideInFragment(AntivirusActivity.kMainFragmentTag);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            _serviceInstance=null;
        }
    };


    //Registration for any activity inside our app to listen to service calls
    MonitorShieldService.IClientInterface _appMonitorServiceListener=null;
    public void setMonitorServiceListener(MonitorShieldService.IClientInterface listener) { _appMonitorServiceListener=listener;}

    //Called when a menace is found by the watchdog
    public void onMonitorFoundMenace(IProblem menace)
    {
        if(_appMonitorServiceListener!=null)
            _appMonitorServiceListener.onMonitorFoundMenace(menace);
    }
    public void onScanResult(List<PackageInfo> allPacakgesToScan,Set<IProblem> scanResult)
    {
        if(_appMonitorServiceListener!=null)
            _appMonitorServiceListener.onScanResult(allPacakgesToScan, scanResult);
    }

    public void startMonitorScan(MonitorShieldService.IClientInterface listener)
    {
        _appMonitorServiceListener=listener;
        if(_serviceInstance!=null)
            _serviceInstance.scanFileSystem();
    }

    public AppData getAppData()
    {
        return AppData.getInstance(this);
    }

    public void onCreate(Bundle paramBundle)
    {

        super.onCreate(/*paramBundle*/null);

        makeActionOverflowMenuShown();
        setContentView(R.layout.activity_appscan);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        procheck();

        //Configure Ads
        if(!StaticTools.isNetworkAvailable(this))
        {
            //showNoInetDialog();
            return;
        }


    }
    public void procheck()
    {
        boolean strPref = false;
        SharedPreferences shf = this.getSharedPreferences("config", MODE_PRIVATE);
        strPref = shf.getBoolean(UpgradePro, false);

        if(strPref)
        {


        }
        else {

            if (Internetconnection.checkConnection(this)) {
                _interstitialAd = new InterstitialAd(this);
                _interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit));
                //Cache first insterstitial
                _requestNewInterstitial();

                AdView adView = new AdView(AntivirusActivity.this);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId(getString(R.string.banner_ad_unit));

                ViewGroup vg = (ViewGroup) findViewById(R.id.topcontainer);
                vg.addView(adView);

                AdRequest.Builder builder = new AdRequest.Builder();
                AdRequest adRequest = builder.build();
                adView.loadAd(adRequest);
            }
          else {

            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                _handleBackButton();
                return true;
            case R.id.ignoredListButton:
                UserWhiteList userWhiteList=getUserWhiteList();
                showIgnoredFragment(userWhiteList);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showIgnoredFragment(UserWhiteList userWhiteList)
    {
        //Create list of apps that are whitelisted and installed
        List<IProblem> ignoredAppsInstalledOnSystem=IgnoredListFragment.getExistingProblems(this, new ArrayList<IProblem>(userWhiteList.getSet()));


        if(ignoredAppsInstalledOnSystem.size() > 0)
        {
            IgnoredListFragment newFragment= (IgnoredListFragment) this.slideInFragment(AntivirusActivity.kIgnoredFragmentTag);
            if(newFragment!=null)
                newFragment.setData(this, userWhiteList);
        }
        else
        {
            new AlertDialog.Builder(this)
                    .setTitle(this.getString(R.string.warning))
                    .setMessage(this.getString(R.string.igonred_message_dialog))
                    .setPositiveButton(this.getString(R.string.accept_eula), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    }).show();
        }

    }

    @Override
    public void onBackPressed()
    {
        _handleBackButton();
    }

    void _handleBackButton()
    {
        goBack();

    }


    public Fragment slideInFragment(String fragmentId)
    {
        Fragment fmt= getSupportFragmentManager().findFragmentByTag(fragmentId);
        if(fmt!=null && fmt.isVisible())
            return null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);


        Fragment f=null;
        switch(fragmentId)
        {
            case kMainFragmentTag:
                f=getMainFragment();
                break;
            case kInfoFragmnetTag:
                f=getInfoFragment();
                break;
            case kResultFragmentTag:
                f=getResultFragment();
                break;
            case kIgnoredFragmentTag:
                f=getIgnoredFragment();
                break;
            default:
        }


        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.container, f, fragmentId);
        transaction.addToBackStack(null);

        // Commit the transaction (Si no lo hago con commitAllowingStateLoss se peta la app
        // https://www.google.es/search?q=android+create+list+inplace&ie=utf-8&oe=utf-8&gws_rd=cr&ei=1J20VrXjFIScUYuXt6gI
        transaction.commitAllowingStateLoss();

        return f;
    }

    public void goBack()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1)
        {
            fm.popBackStack();

        }
        else
        {

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.dialog_message_exit))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();

                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                }
            }).show();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //Start services
        if(!StaticTools.isServiceRunning(this, MonitorShieldService.class))
        {
            Intent i = new Intent(this, MonitorShieldService.class);
            startService(i);

            //Bind to service
            bindService(i, _serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else
        {
            Intent i = new Intent(this, MonitorShieldService.class);

            //Bind to service
            bindService(i, _serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        // Unbind from the service
        if (_bound && _serviceConnection!=null)
        {
            unbindService(_serviceConnection);
            _bound = false;
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        _menu=menu;
        return true;
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.d("TAG", e.getLocalizedMessage());
        }
    }

}
