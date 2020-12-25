package gramtarang.instamoney.agent_login;
//IMPORTS
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import gramtarang.instamoney.R;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class activity_WelcomeScreen extends AppCompatActivity {
    //DECLARATIONS
    OkHttpClient client;
    ImageView img_mintLogo;
    TextView app_name;
    String response_String, dateofrelease, latest_app_version, androidId, latitude, longitude;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    boolean isLatestVersion,isRooted,doubleBackToExitPressedOnce;
    public static final int REQUEST_CODE_PERMISSIONS = 101;
    Utils util = new Utils();




    @Override
    protected void onCreate(Bundle savedInstanceState) {

       //LAYOUT ELEMENTS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img_mintLogo = (ImageView) findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);
        androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.logoanimation);
        img_mintLogo.startAnimation(myanim);
        app_name.startAnimation(myanim);
        client = new OkHttpClient();





       //CHECK ROOT STATUS
        isRooted = util.isDeviceRooted();
        if (isRooted) {
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this, "Alert", "App can't run on rooted devices.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
        }


        //CHECK LOCATION STATUS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           getLocation();
        }
        else{
            requestLocationPermission();
        }


        //CHECK INTERNET CONNECTIVITY
        if (!isConnected()) {
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this, "Alert", "No Internet Connection", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }


    }


    //REQUEST LOCATION IF NOT GRANTED
    private void requestLocationPermission() {

        boolean foreground = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (foreground) {
            boolean background = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

            if (background) {
                handleLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {

            boolean foreground = false, background = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //foreground permission allowed
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        Intent in = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        continue;
                    } else {
                        Toast.makeText(getApplicationContext(), "Location Permission denied.", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                        break;
                    }
                }

                if (permissions[i].equalsIgnoreCase(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] >= 0) {
                        foreground = true;
                        background = true;
                        getLocation();

                        // Toast.makeText(getApplicationContext(), "Background location location permission allowed", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getApplicationContext(), "Background location location permission denied", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            if (foreground) {
                if (background) {
                    handleLocationUpdates();
                } else {
                    handleForegroundLocationUpdates();
                }
            }
        }
    }


    //GET LOCATION IF GRANTED
    public void getLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
 longitude = String.valueOf(location.getLongitude());
  latitude = String.valueOf(location.getLatitude());
        new apiCall_getversion().execute();
}
    private void handleLocationUpdates() {
        //foreground and background
        //Toast.makeText(getApplicationContext(),"Start Foreground and Background Location Updates",Toast.LENGTH_SHORT).show();
    }

    private void handleForegroundLocationUpdates() {
        //handleForeground Location Updates
        //Toast.makeText(getApplicationContext(),"Start foreground location updates",Toast.LENGTH_SHORT).show();
    }



    //ON BACK PRESSED
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        DialogActivity.DialogCaller.showDialog(getApplicationContext(),"Alert","No Internet Connection",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                finish();
            }
        }, 4000);
    }



    //NETWORK CONNECTIVITY CHECK
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }



    //GET LATEST APP VERSION
    class apiCall_getversion extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            okhttp3.Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/im/version")
                    .addHeader("Accept", "*/*")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","Server Unreachable.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    if (response_String != null) {
                      //JSON PARSING
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            latest_app_version= jsonResponse.getString("version_number");
                            dateofrelease = jsonResponse.getString("date_of_release");
                             }
                        catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }


                        //CHECK APP VERSION OUTDATED OR LATEST
                        if(!getString(R.string.app_version).equals(latest_app_version)){
                            activity_WelcomeScreen.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","Outdated Version.\nPlease Contact Administrator.",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                }
                            });

                        }



                        //VALIDATE PARAMETERS
                            isLatestVersion=true;
                            if(!isRooted&&isConnected() &&isLatestVersion&& !latitude.equals("0.0") && !longitude.equals("0.0") ){
                                activity_WelcomeScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        move();
                                    }
                                });

                            }
                        //}



                    }

                }
            });

            return null;
        }

    }



    //IF ALL VALIDATIONS SUCCEEDED
    public void move(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //SEND DATA VIA PREFERENCES
                preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Latitude",String.valueOf(latitude));
                editor.putString("Longitude",String.valueOf(longitude));
                editor.commit();


                //START INTENT
                Intent i = new Intent(activity_WelcomeScreen.this, activity_Login.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

}

