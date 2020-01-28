package wallsplash.ankitray.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;



import com.facebook.stetho.Stetho;

import java.text.SimpleDateFormat;
import java.util.Date;




/**
 * Created by Sunil on 23-Oct-16.
 */
public class SharedObjects extends MultiDexApplication {

    private static final String USERJSON = "UserJSON";
    private static final String USERID = "UserID";
    private static final String USERPROFPIC = "UserProfPic";
    private static final String LoginCode = "LoginCode";
   // private static final int LoginCode =1;
    public static Context context;
    public PreferencesEditor preferencesEditor = new PreferencesEditor();
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    public SharedObjects() {
    }

    public static Context getContext() {
        return context;
    }

    public SharedObjects(Context context) {
        this.context = context;
        sharedPreference = context.getSharedPreferences("BML", Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        initializeStetho();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
      //  Fabric.with(this, new Crashlytics());
    }


    public static boolean isValidName(String name) {
        return name.matches("[A-Za-z\\s]*");
    }

    public static boolean isValidNumber(String number, int length) {
        return !((number.length() > length) || !number.matches("[0-9]*"));
    }

    public static boolean isValidURL(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    public static boolean isValidEmail(String emailid) {
        return Patterns.EMAIL_ADDRESS.matcher(emailid).matches();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setStrictMode(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public String convertOrderDate(String dt) throws Exception {
        String dateNoraml = "12-12-12";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date dateCal = new Date();
        dateCal = simpleDateFormat.parse(dt);

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        dateNoraml = outputDateFormat.format(dateCal);

        return dateNoraml;
    }

    public String convertOrderTime(String dt) throws Exception {
        String dateNoraml = "12-12-12";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date dateCal = new Date();
        dateCal = simpleDateFormat.parse(dt);

        SimpleDateFormat outputDateFormat = new SimpleDateFormat("HH:mm");
        dateNoraml = outputDateFormat.format(dateCal);

        return dateNoraml;
    }

    public void initializeStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(context);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(context));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void hideKeyboard(View view, Context c) {
        InputMethodManager inputMethodManager = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void SaveUserData(String userJson) {
        preferencesEditor.setPreference(SharedObjects.USERJSON,userJson);
    }



    public void setUserID(String userID) {
        preferencesEditor.setPreference(SharedObjects.USERID,userID);
    }
    public String getUserID() {
      return  preferencesEditor.getPreference(SharedObjects.USERID);
    }

    public void setProfilePic(Uri uri) {
        preferencesEditor.setPreference(SharedObjects.USERPROFPIC,uri.getPath());
    }

    public void setProfilePicFB(String piclink) {
        preferencesEditor.setPreference(SharedObjects.USERPROFPIC,piclink);
    }

    public String getProfilePic() {
        return  preferencesEditor.getPreference(SharedObjects.USERPROFPIC);
    }

    public void setCode(int code) {

        editor.putInt(SharedObjects.LoginCode,code);
        editor.commit();
        editor.apply();
    }

    public int getCode() {
        return   sharedPreference.getInt(SharedObjects.LoginCode,0);

    }
    public class PreferencesEditor {

        public void setPreference(String key, String value) {
            SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putString(key, value);
            editor.apply();
            editor.commit();
        }

        public String getPreference(String key) {
            try {
                SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
                return sharedPreference.getString(key, "");
            } catch (Exception exception) {
                return "";
            }
        }

        public void setBoolean(String key, boolean value) {
            SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putBoolean(key, value);
            editor.commit();
        }

        public Boolean getBoolean(String key) {
            try {
                SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
                return sharedPreference.getBoolean(key, true);
            } catch (Exception exception) {
                return false;
            }
        }

        public void removeSinglePreference(String pref) {

            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (app_preferences.contains(pref)) {
                SharedPreferences.Editor editor = app_preferences.edit();
                editor.remove(pref);
                editor.commit();
            }
        }

        public void clear() {
            SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.clear();
            editor.commit();
        }


    }
}
