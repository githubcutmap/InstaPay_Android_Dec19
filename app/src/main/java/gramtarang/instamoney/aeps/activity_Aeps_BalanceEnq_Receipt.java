package gramtarang.instamoney.aeps;
//IMPORTS

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.LogOutTimer;

public class activity_Aeps_BalanceEnq_Receipt extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG="AEPS Balenq";
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
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    TextView tv_timestamp,tv_bal,tv_bankname,tv_aadhaarnumber,tv_transid,tv_agentid,tv_rrnno, tv_CustomerName, tv_descriptionMessage;
    String agent_name,agent_phone_number,transtype="AEPS BALANCE ENQUIRY",latitude,longitude,status,status_code,transaction_type,transaction_amount="0.00",timestamp,fpTransId, available_balance, bankName, aadhaar_number, trans_id, agentid, rrn_no, custName, message, ipAddress, androidId;
    Button btn_back;
    ImageView trans_status;


    //BACK PRESSED HANDLING
    boolean doubleBackToExitPressedOnce = false;
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
        super.onCreate(savedInstanceState);

        //LAYOUT ELEMENTS DECLARATION
        setContentView(R.layout.activity_aeps_balance_receipt);
        tv_aadhaarnumber=findViewById(R.id.aadhaar_number);
        tv_bal=findViewById(R.id.available_balance);
        tv_bankname=findViewById(R.id.bank_name);
        tv_timestamp=findViewById(R.id.timestamp);
        tv_transid=findViewById(R.id.merchant_transid);
        tv_agentid=findViewById(R.id.imid);
        tv_descriptionMessage =findViewById(R.id.tex_message);
        tv_CustomerName =findViewById(R.id.customer_name);
        btn_back =findViewById(R.id.back);
trans_status=findViewById(R.id.trans_status);


        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        androidId=preferences.getString("AndroidId","No name defined");
        agent_phone_number=preferences.getString("AgentPhone","No name defined");
        agent_name=preferences.getString("AgentName","No name defined");
        agentid=preferences.getString("Username","No name defined");
        status_code=preferences.getString("status_code","No name defined");


         //CURRENT DATE
         SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
         timestamp = s.format(new Date());


         // INTENT DATA
        Intent intent = getIntent();
        available_balance= intent.getStringExtra("balance");
        bankName = intent.getStringExtra("bank_name");
        aadhaar_number = intent.getStringExtra("aadhaar");
        trans_id = intent.getStringExtra("merchant_transid");
        rrn_no=intent.getStringExtra("rrn_no");
        custName=intent.getStringExtra("custName");
        fpTransId=intent.getStringExtra("fpTransId");
        status=intent.getStringExtra("status");
        status_code=intent.getStringExtra("status_code");
        transaction_type=intent.getStringExtra("transaction_type");
        status_code=intent.getStringExtra("status_code");
        try{
            if(status_code.equals("TXN")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    trans_status.setImageDrawable(getResources().getDrawable(R.drawable.success, getApplicationContext().getTheme()));
                } else {
                    trans_status.setImageDrawable(getResources().getDrawable(R.drawable.success));
                }

            }
            else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    trans_status.setImageDrawable(getResources().getDrawable(R.drawable.fail, getApplicationContext().getTheme()));
                } else {
                    trans_status.setImageDrawable(getResources().getDrawable(R.drawable.fail));
                }

            }
        }catch (Exception e){e.printStackTrace();}
        Log.d("BER","Calling SEND SMS");
        new SendTransDetailsSMS().execute();


        //SET DATA TO TEXTVIEWS
        try {
            tv_bankname.setText(bankName);

            if(trans_id==null){
                tv_transid.setText("Not Applicable");
            }
            else{
                tv_transid.setText(trans_id);
            }
            if(available_balance==null){
                tv_bal.setText("Not Applicable");
            }
            else{
                tv_bal.setText(available_balance);
            }
            tv_descriptionMessage.setText(status);
            tv_CustomerName.setText(custName);
            tv_transid.setText(trans_id);
            tv_agentid.setText(agentid);
            tv_aadhaarnumber.setText("XXXX" + " " + "XXXX" + " " + aadhaar_number.charAt(8) + aadhaar_number.charAt(9) + aadhaar_number.charAt(10) + aadhaar_number.charAt(11));
            tv_timestamp.setText(timestamp);
        }catch (Exception e){
            e.printStackTrace();
        }


        //BACK BUTTON ON CLICK EVENT
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity_Aeps_BalanceEnq_Receipt.this, activity_Aeps_HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }



    //GET CURRENT TIME GREETINGS
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


    //SEND SMS API
    public  class SendTransDetailsSMS extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("BER","IN SEND SMS");
            HttpURLConnection urlConnection = null;
            String greet=gethour();
            String flagurl= null;
            try {
                String message= URLEncoder.encode(greet+","+" "+agent_name+"\n"+"Thank you for banking with us"+"\n"+"Your transaction details are:"+"\n"+"Transaction Type:"+"\n"+transtype+"\n"+"\n"+"Message:"+status+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
                flagurl =  "http://smslogin.mobi/spanelv2/api.php?username=gramtarang&password=Ind123456&to="+agent_phone_number+"&from=GTIDSP&message="+message;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("BER","URL IS"+flagurl);
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
