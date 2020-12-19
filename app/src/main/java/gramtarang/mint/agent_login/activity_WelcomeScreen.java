package gramtarang.mint.agent_login;

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
import android.os.Build;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gramtarang.mint.R;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/*acitivity_WelcomeScreen activity is containing the popup screen when the application
 * is opened*/
public class activity_WelcomeScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private static final String TAG = "MainActivity";
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationManager locationManager;
    OkHttpClient client;
    ImageView img_mintLogo;
    TextView app_name;
    String agent_id,response_String,jsonString,areamanagerid;

    String dateofrelease;
    String latest_app_version;
    String androidId;
    String latitude;
    String longitude;
    String agentId;String otpurl;
    boolean isLatestVersion, isLocationgranted;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    boolean isRooted;
    public static final int REQUEST_CODE_PERMISSIONS = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img_mintLogo = (ImageView) findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.logoanimation);
        img_mintLogo.startAnimation(myanim);
        app_name.startAnimation(myanim);
        client = new OkHttpClient();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());


        if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.N){
            Log.d("VERSION","Android Version is:Nougat");
            requestLocationPermission();
           /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            checkLocation();*/
        }   else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.N_MR1){
            Log.d("VERSION","Android Version is:Nougat_2");
            requestLocationPermission();
            /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
            checkLocation();*/
        }
        else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.Q){
            Log.d("VERSION","Android Version is:Q");
            requestLocationPermission();
        }
        else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.R){
            Log.d("VERSION","Android Version is:R");
           requestLocationPermission();
        }
        else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.P){
            Log.d("VERSION","Android Version is:P");
            requestLocationPermission();
        }
        else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.O){
            Log.d("VERSION","Android Version is:O");
            requestLocationPermission();
        }
        else if (android.os.Build.VERSION.SDK_INT ==Build.VERSION_CODES.O_MR1){
            Log.d("VERSION","Android Version is:O_2");
            requestLocationPermission();
        }

        else{

            requestLocationPermission();

           ///check whether location service is enable or not in your  phone
            // do something for phones running an SDK before lollipop
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        checkLocation();
        Utils util=new Utils();
        isRooted=util.isDeviceRooted();
        if(isRooted){
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","App can't run on rooted devices.",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            isLocationgranted=true;
        }
        if (!isConnected()) {
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","No Internet Connection",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }


    }
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
                        Intent in= getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
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

    private void handleLocationUpdates() {
        //foreground and background
        //Toast.makeText(getApplicationContext(),"Start Foreground and Background Location Updates",Toast.LENGTH_SHORT).show();
    }

    private void handleForegroundLocationUpdates() {
        //handleForeground Location Updates
        //Toast.makeText(getApplicationContext(),"Start foreground location updates",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            // Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d("reque", "--->>>>");
    }
    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        latitude=String.valueOf(location.getLatitude());
        longitude= String.valueOf(location.getLongitude() );
        //  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        System.out.println("SOMETHING"+latitude+longitude);
        new apiCall_getversion().execute();
//sendOtp("7093460377","Hello this is final message");

        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }
    private boolean checkLocation() {

        if(!isLocationEnabled()){

            showAlert();
            //Log.d("ERRORRR","NOOO");
        }

        return isLocationEnabled();
    }
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        System.exit(0);
                    }
                });
        dialog.show();
    }
    private boolean isLocationEnabled() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    boolean doubleBackToExitPressedOnce = false;
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
    /*Utils utils=new Utils();
        OkHttpClient httpClient = utils.createAuthenticatedClient("1010", "Test@123");*/
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
                    //the response we are getting from api
                    response_String = response.body().string();
                    if (response_String != null) {
                        Log.d("TAG","Response is+"+response_String.toString());
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            latest_app_version= jsonResponse.getString("version_number");
                            dateofrelease = jsonResponse.getString("date_of_release");
                            Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease);
                            //   setText(tv_dateofrelease,dateofrelease,tv_version,latest_app_version);

                            //Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease+androidId+latitude+longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e) {
                        }
                        if(!getString(R.string.app_version).equals(latest_app_version)){
                            Log.d("TAG","Errorrrrr"+getString(R.string.app_version)+latest_app_version);
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
                        //else{
                            isLatestVersion=true;
                            //
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
 /*public void sendOtp(String number,String message){
     String otpurl="http://smslogin.mobi/spanelv2/api.php?username=gramtarang&password=Ind123456&to="+number+"&from=LEOSMS&message="+message;

     RequestBody reqbody = RequestBody.create(null, new byte[0]);
     Request.Builder formBody = new Request.Builder().url(otpurl).method("POST",reqbody).header("Content-Length", "0");
     client.newCall(formBody.build()).enqueue(new Callback() {
         @Override
         public void onFailure(Call call, IOException e) {

         }

         @Override
         public void onResponse(Call call, Response response) throws IOException {

         }
     });
 }*/
    public void move(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("Latitude",String.valueOf(latitude));
                editor.putString("Longitude",String.valueOf(longitude));
                editor.commit();
                Intent i = new Intent(activity_WelcomeScreen.this, activity_Login.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

}

