package com.mobilesecurity.mobileapp;


public final class Constant {
    
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public static final String TAG = "AbcMobileSecurity";
    public static final String PRODUCT_ID = "android.test.purchased"; // PUT YOUR PRODUCT ID HERE
    public static final String MERCHANT_ID = null; // PUT YOUR MERCHANT ID HERE
    public static final String LICENSE_KEY = null; // PUT YOUR LICENSE KEY HERE
    public static final String PrivacyPolicyUrl = "https://pharid.com/privacy-policy/"; // PUT YOUR Privacy Policy Url Here
    public static final String UpgradePro = "AbcMobileSecurity"; //Please put something different for example you can put your app name here without spaces

    public static String URL_LOGIN = "https://pharid.com/abcmobile/api/login.php";
    public static String URL_REGISTER = "https://pharid.com/abcmobile/api/register.php";

    public static final String KEY_SAFE_PHONE = "safey_phone";
    public static final String KEY_SIM_INFO = "sim_info";
    public static final String KEY_CONTACT_PHONE = "contact_phone";
    public static final String KEY_CB_PHONE_SAFE = "cb_pref_phone_safe";
    public static final String KEY_CB_BIND_SIM = "cb_pref_bind_sim";
    public static final String KEY_CB_GPS_TRACE = "cb_pref_gps_trace";
    public static final String KEY_CB_DEVICE_ADMIN = "cb_pref_device_admin";
    public static final String KEY_CB_REMOTE_LOCK_SCREEN = "cb_pref_remote_lock_screen";
    public static final String KEY_CB_REMOTE_WIPE_DATA = "cb_pref_remote_wipe_data";
    public static final String KEY_CB_ALARM = "cb_pref_alarm";

    public static final String SMS_GPS_TRACE = "#*gps*#";
    public static final String SMS_REMOTE_LOCK_SCREEN = "#*lock screen*#";
    public static final String SMS_REMOTE_WIPE_DATA = "#*wipe data*#";
    public static final String SMS_ALARM = "#*alarm*#";

    public static final String KEY_CB_SHOW_INCOMING_LOCATION = "cb_pref_show_incoming_location";

    
    public static final String KEY_FLOAT_TOAST_X = "float_toast_x";
    public static final String KEY_FLOAT_TOAST_Y = "float_toast_y";


    // notification topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "notificationnn";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


}
