package gramtarang.instamoney.utils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.Dashboard;
import gramtarang.instamoney.agent_login.activity_Login;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginVerification extends AppCompatActivity implements LogOutTimer.LogOutListener{
    //LOG OUT TIMER
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
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }


    //DECLARATIONS
    OkHttpClient httpClient;
    EditText edit_otp;
    TextView timer,otp_type;
    Button submit,resend_btn;
    public String username,password,bankmitra_id,jsonString,entered_otp,androidId,login_status,latitude,longitude,agentId,timestamp,agentphn,agentemail,agentname,generated_pin;
    SharedPreferences preferences;boolean isValidOtp;String verification_type;
    public static final String mypreference = "mypref";
    String TAG = "LoginVerification";
    Utils utils=new Utils();
    int counter = 0;
    boolean doubleBackToExitPressedOnce = false;


    //ON BACK PRESSED
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                Intent intent=new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LAYOUT ELEMENTS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verification);
        edit_otp=findViewById(R.id.user_entered_otp);
        submit=findViewById(R.id.verify);
        timer = findViewById(R.id.timer);
        resend_btn = findViewById(R.id.resend_btn);
        otp_type = findViewById(R.id.type);


        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        latitude=preferences.getString("Latitude","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        bankmitra_id=preferences.getString("BankMitraId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");;
        agentemail=preferences.getString("AgentEmail","No name defined");
        agentname=preferences.getString("AgentName","No name defined");
        generated_pin=preferences.getString("LoginOTP","No name defined");
        Log.d("TAG","OTP for Login:"+generated_pin);
        otp_type.setText(agentphn);


        //GET CURREMT DATE
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        timer();
        edit_otp.addTextChangedListener(otpTextWatcher);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if(counter<=3){
                    resend_btn.setEnabled(false);
                    generated_pin=utils.getOTPString();
                   new SendOTP().execute();
                    timer();
                }
                else{
                    timer.setText("Please Login After Some time");
                    resend_btn.setEnabled(false);

                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                entered_otp=edit_otp.getText().toString().trim();
                //utils.getprogressDialog(LoginVerification.this,"Please Wait","Verification of OTP in Progress..");
if(entered_otp.equals(generated_pin)){
    isValidOtp=true;
}
else{
    isValidOtp=false;
}

                if(isValidOtp){
                    login_status="Success";
                    new apiCall_insertloginlog().execute() ;
                    Intent intent=new Intent(LoginVerification.this, Dashboard.class);
                    startActivity(intent);
                }
                else{
                    login_status="Failed";
                    new apiCall_insertloginlog().execute() ;
                    Toast.makeText(LoginVerification.this,"Incorrect OTP",Toast.LENGTH_SHORT);
                    edit_otp.setError("Enter Valid OTP");
//                    utils.dismissProgressDialog();
                    submit.setEnabled(true);
                }
            }
        });


    }


    public TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            entered_otp=edit_otp.getText().toString().trim();
            if(entered_otp.length() == 6){
                submit.setEnabled(true);


            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };




    public void timer(){
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Resend OTP in: " + millisUntilFinished / 1000+" Seconds");
            }


            @SuppressLint("NewApi")
            public void onFinish() {
                timer.setText("");
                resend_btn.setEnabled(true);

            }
        }.start();
    }


//INSERT LOGIN LOG
    class apiCall_insertloginlog extends AsyncTask<Request, Void, String> {
        @SuppressLint("HardwareIds")
        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            httpClient = utils.createAuthenticatedClient(username, password);
            try {


                Log.d("RAF","Message"+username+androidId+latitude+longitude+login_status);
                jsonObject.put("s_id", null);
                jsonObject.put("agentid",username);
                jsonObject.put("androidid", androidId);
                jsonObject.put("latitude",latitude);
                jsonObject.put("longitude",longitude);
                jsonObject.put("timestamp",null);
                jsonObject.put("loginstatus",login_status);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Log.d("RAF","Message 2"+jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/im/loginlogs")
                    .addHeader("Accept", "*/*")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    String flag=response.body().toString();
                    Log.d("RAF","Message 3"+flag);
                }

            });


            return null;
        }

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
           String flagurl="http://smslogin.mobi/spanelv2/api.php?username=gramtarang&password=Ind123456&to="+agentphn+"&from=GTIDSP&message="+greet+", "+agentname+"Your OTP for login is:"+generated_pin+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team";
           Log.d("OTP","Params are"+agentname+agentphn+generated_pin+"URL IS"+flagurl);
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