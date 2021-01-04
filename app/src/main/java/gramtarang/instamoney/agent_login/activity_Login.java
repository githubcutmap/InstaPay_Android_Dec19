package gramtarang.instamoney.agent_login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import gramtarang.instamoney.R;
import gramtarang.instamoney.api.MobileSMSAPI;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.LoginVerification;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class activity_Login extends AppCompatActivity implements LogOutTimer.LogOutListener {




    //LOGOUT TIMER
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
         //Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

         //Log.e(TAG, "onResume()");
    }

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(activity_Login.this,activity_WelcomeScreen.class);
        startActivity(intent);
    }




    //DECLARATIONS
    Utils utils = new Utils();
    public final String mypreference = "mypref";
    private final String TAG = "Login_Activity";
    int role,aeps,loan,bbps,pan,card;
    SharedPreferences preferences;
    String verification_type, agentname,bankmitraid,agentPassword, generated_pin, agentphn, agentemail, latitude, longitude, username,outletid, androidId, appversion, dateofrelease, response_String, agentAadhaar, jsonString, timestamp,areamanager_id,areamanager_name,panNo;
    EditText et_userName,et_pass;
    boolean isValidUsername,isphnregistered,isemailregistered;
    TextView tv_version, tv_dateofrelease;
    Button btn_login;
    OkHttpClient client,httpClient;
    boolean doubleBackToExitPressedOnce = false;
    Utils util = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //LAYOUT ELEMENTS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        et_userName = findViewById(R.id.username);
        et_pass = findViewById(R.id.password);
        btn_login = findViewById(R.id.login_button);
        btn_login.setEnabled(true);
        tv_version = findViewById(R.id.version);
        tv_dateofrelease = findViewById(R.id.dateofr);
        tv_version.setText(R.string.app_version);
        tv_dateofrelease.setText(R.string.dateofrelease);

        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude = preferences.getString("Latitude", "No name defined");
        longitude = preferences.getString("Longitude", "No name defined");
        appversion = preferences.getString("AppVersion", "No name defined");
        dateofrelease = preferences.getString("DateofRelease", "No name defined");







        //CURRENT DATE
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());

        //LOGIN BUTTON HANDLING
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    username = et_userName.getText().toString(); } catch (NullPointerException e) {
                    et_userName.setError("Enter Username");

                }
                try{
                    agentPassword = et_pass.getText().toString();}catch (NullPointerException e){
                    et_pass.setError("Enter Password");
                }
                isValidUsername = util.isValidName(username);

                if (!isValidUsername) {
                    et_userName.setError("Enter Valid Username");
                }
                else{
                    new apiCall_getagentdetails().execute();//GET AGENT DETAILS API
                }



            }
        });
    }




    //BACK PRESSED HANDLING
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to Close the app.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                finish();
            }
        }, 2000);
    }


    //API CALL FOR AGENT DETAILS
    class apiCall_getagentdetails extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            //  Log.d("TAG","EN_FLAG"+en_flag);
            try {
                httpClient = utils.createAuthenticatedClient(username,agentPassword);
                jsonObject.put("id",username);
                jsonObject.put("password",agentPassword);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/loans/getagentdetails")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //JSON PARSING
                    assert response.body() != null;
                    response_String = response.body().string();
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        JSONObject jsonResponse1=null;
                  try {
                            jsonResponse = new JSONObject(response_String);
                            jsonResponse1=jsonResponse.getJSONObject("llist1");
                            agentemail = jsonResponse1.getString("email");
                            agentphn = jsonResponse1.getString("contact_no");
                            agentname = jsonResponse1.getString("name");
                            bankmitraid=jsonResponse1.getString("id");
                            areamanager_id=jsonResponse1.getString("area_manager_id");
                            areamanager_name=jsonResponse1.getString("area_manager");
                            agentAadhaar =jsonResponse1.getString("aadhaar_number");
                            role=jsonResponse1.getInt("role");
                            outletid=jsonResponse1.getString("aepsim");
                            aeps=jsonResponse1.getInt("aeps");
                            pan=jsonResponse1.getInt("pan");
                            bbps=jsonResponse1.getInt("bbps");
                            card=jsonResponse1.getInt("card");
                            loan=jsonResponse1.getInt("loan");
                            panNo=jsonResponse1.getString("panno");
                           activity_Login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    senddata(outletid,agentAadhaar,areamanager_name,agentemail,agentname,agentphn,bankmitraid,areamanager_id,aeps,pan,bbps,loan,card,isphnregistered,isemailregistered,panNo);
                                }
                            });

                        } catch (JSONException e) {
                            Log.d("TAG", "Exception Caught "+e );
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    DialogActivity.DialogCaller.showDialog(activity_Login.this,"Login Failed","Agent not registered.\nPlease contact administrator."+androidId,new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });


                        }


                    }

                }

            });


            return null;
        }

    }
    public void senddata(String outletid1, String aadhaar, String areamanager_name, String email, String name, String phn, String bankmitraid, String areamanagerid, int aeps, int pan, int bbps, int loan, int card, boolean isphnregistered, boolean isemailregistered,String panNo){
        verification_type = "OTP";
        generated_pin = utils.getOTPString();
        new SendOTP().execute();//SEND OTP FOR VERIFICATION
        try {
            username = et_userName.getText().toString();
            Log.d("TAG","Entered"+username);
        } catch (NullPointerException e) {
            et_userName.setError("Enter Username");

        }

        //SEND DATA VIA PREFERENCES
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AndroidId",androidId);
        editor.putString("Username",username);
        editor.putString("Password",agentPassword);
        editor.putString("AgentName", name);
        editor.putString("AgentEmail", email);
        editor.putString("AgentPhone", phn);
        editor.putString("AreaManagerName", areamanager_name);
        editor.putString("BankMitraId", bankmitraid);
        editor.putString("AgentAadhaar", aadhaar);
        editor.putString("VerificationMethod", verification_type);
        editor.putString("LoginOTP", generated_pin);
        editor.putString("AreaManagerId", areamanagerid);
        editor.putString("OutletId",outletid1);
        editor.putInt("Role", role);
        editor.putInt("aeps",aeps);
        editor.putInt("pan",pan);
        editor.putInt("loan",loan);
        editor.putInt("bbps",bbps);
        editor.putInt("card",card);
        editor.putString("panno",panNo);
        editor.commit();

        //START INTENT
        Intent i = new Intent(activity_Login.this, LoginVerification.class);
        startActivity(i);

    }

    //GET CURRENT TIME GREETING
    public String gethour(){
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if(hour<12){
            greeting="Good Morning";
        }
        else if(hour<17){
            greeting="Good Afternoon";
        }
        else{
            greeting="Good Evening";
        }
        return greeting;
    }

    //SEND OTP
    public  class SendOTP extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String greet=gethour();
            String flagurl= null;
             try {
                 String message=URLEncoder.encode(greet+", "+agentname+"\n"+"\n"+"Your OTP for login is:"+generated_pin+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team","UTF-8");
                 flagurl =  "http://smslogin.mobi/spanelv2/api.php?username=gramtarang&password=Ind123456&to="+agentphn+"&from=GTIDSP&message="+message;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                URL url = new URL(
                        flagurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }
}

