package com.mobilesecurity.mobileapp.AntiVirus;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class StaticTools
{
    //Notifications
    public static final String TAG = "NotificationUtils";

    public static void notificatePush(Context context, int notificationId,int iconDrawableId,
                                      String tickerText, String contentTitle, String contentText, Intent intent)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconDrawableId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(tickerText);

        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, mBuilder.build());
    }


    public static String getInternalDataPath(Context c)
    {
        return new ContextWrapper(c).getFilesDir().getPath();
    }

    public static boolean existsFile(String filePath)
    {
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory())
            return true;
        else
            return false;
    }

    public static boolean existsFolder(String folderPath)
    {
        File f = new File(folderPath);
        if(f.exists() && f.isDirectory())
            return true;
        else
            return false;
    }



    public static String loadJSONFromAsset(Context context,String file)
    {
        String json = null;
        try
        {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String loadJSONFromFile(Context context,String filePath)
    {
        StringBuilder text=new StringBuilder();
        try
        {
            BufferedReader br=new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }

        return text.toString();
    }


    static public void writeTextFile(String filePath, String text) throws IOException
    {
        BufferedWriter bw=null;
        try
        {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(text);
        }
        finally
        {
            bw.close();
        }
    }


    static public boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Activitiets
    public static String getPackageName(final Context context)
    {
        return context.getPackageName();
    }


    public static boolean isPackageInstalled(Context context,String targetPackage)
    {
        PackageManager pm=context.getPackageManager();
        try
        {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
        return true;
    }


    public static List<PackageInfo> getNonSystemApps(final Context context, List<PackageInfo> appsToFilter)
    {

        List<PackageInfo> filteredPackgeInfo = new ArrayList<PackageInfo>();

        PackageInfo packInfo = null;

        int mask=ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;

        for (int i = 0; i < appsToFilter.size(); i++)
        {
            packInfo = appsToFilter.get(i);
            if ((packInfo.applicationInfo.flags & mask) == 0)
            {
                filteredPackgeInfo.add(packInfo);
            }
        }

        return filteredPackgeInfo;
    }


    public static List<PackageInfo> getApps(final Context context, int packageManagerPermissions)
    {
        return context.getPackageManager().getInstalledPackages(packageManagerPermissions);
    }

    public static String getAppNameFromPackage(final Context context, final String packageName)
    {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        String appName = "";
        try
        {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        }
        catch (android.content.pm.PackageManager.NameNotFoundException ex)
        {
            appName = "Unkown app";
        }

        return appName;
    }


    public static Drawable getIconFromPackage(String packageName, Context context)
    {

        final PackageManager pm = context.getPackageManager();
        Drawable icon = null;

        try
        {
            icon = pm.getApplicationIcon(packageName);

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return icon;

    }


    public static boolean checkIfAppWasInstalledThroughGooglePlay(Context context, String packageName)
    {

        final PackageManager packageManager = context.getPackageManager();

        try
        {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if ("com.android.vending".equals(packageManager.getInstallerPackageName(applicationInfo.packageName)))
            {
                return true;
            }
        } catch (final PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        return false;

    }


    public static boolean checkIfUSBDebugIsEnabled(Context context)
    {

        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1)
        { return true;
        } else
        {
             return false;
        }
    }


    public static PackageInfo getPackageInfo(Context context, String packageName, int packageManagerPermissions) throws PackageManager.NameNotFoundException
    {
        return context.getPackageManager().getPackageInfo(packageName, packageManagerPermissions);
    }

    public static boolean packageInfoHasPermission(PackageInfo packageInfo, String permissionName)
    {
        if(packageInfo.requestedPermissions==null)
            return false;

        for(String permInfo :  packageInfo.requestedPermissions)
        {
            if(permInfo.equals(permissionName))
                return true;
        }

        return false;
    }

    public static boolean checkIfUnknownAppIsEnabled(Context context)
    {

        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS,0) == 1)
        {

            return true;
        } else
        {

            return false;
        }

    }

    public static void openSecuritySettings(Context context)
    {

        context.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));


    }

    public static void openDeveloperSettings(Context context)
    {

        context.startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));


    }
    public static String fillParams(String data, String paramStr, String ... args)
    {

        for (int i = 0; i < args.length; i++)
            data=data.replace(paramStr+(i+1),args[i]);

        return data;
    }


    //Encode
    public static String mixUp(String str, int interleaveRange)
    {

        StringBuffer sb = new StringBuffer(str);

        for (int i = 0; i < str.length() - interleaveRange; ++i) {
            char c = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i + interleaveRange));
            sb.setCharAt(i + interleaveRange, c);
        }

        return sb.toString();
    }


    static public boolean stringMatchesMask(String packageName, String mask)
    {
        boolean wildcard=false;

        if(mask.charAt(mask.length()-1)=='*')
        {
            wildcard=true;
            mask=mask.substring(0,mask.length()-2);
        }
        else
            wildcard=false;

        if(wildcard==true)
        {
            if (packageName.startsWith(mask))
                return true;
            else
                return false;
        }
        else
        {
            if(packageName.equals(mask))
                return true;
            else
                return false;
        }

    }

    //Views
    @SuppressLint("NewApi")
    static public void setViewBackgroundDrawable(View view,Drawable drawable)
    {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackgroundDrawable(drawable);
        }
        else
        {
            view.setBackground(drawable);
        }
    }

    //Service
    public static boolean isServiceRunning(Context context,Class<?> serviceClass)
    {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    static public <T> T deserializeFromFile(String fileName) throws FileNotFoundException, IOException
    {
        T data = null;
        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (T) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public static <T> T deserializeFromDataFolder(Context ctx, String rootRelativePath)
    {
        T obj = null;

        try
        {

            //Internal
            String path = StaticTools.getInternalDataPath(ctx) + File.separatorChar + rootRelativePath;
            if (StaticTools.existsFile(path))
            {
                obj = StaticTools.deserializeFromFile(path);
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return obj;
    }

    static public void serializeToFile(String fileName,Serializable obj) throws IOException
    {
        File file= new File(fileName);
        String fileParentFolder=file.getParent();
        File parentPath=new File(fileParentFolder);

        if(fileParentFolder!=null)
        {
            if(!StaticTools.existsFolder(fileParentFolder))
                parentPath.mkdirs();
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        fos = new FileOutputStream(fileName);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
        fos.close();
    }

    public static void serializeToDataFolder(Context ctx, Serializable obj, String rootRelativePath) throws IOException
    {

        String internalPath= StaticTools.getInternalDataPath(ctx);
        String finalPath=internalPath+File.separatorChar+rootRelativePath;
        serializeToFile(finalPath, obj);
    }
}