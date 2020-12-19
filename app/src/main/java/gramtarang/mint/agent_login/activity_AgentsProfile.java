package gramtarang.mint.agent_login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gramtarang.mint.R;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class activity_AgentsProfile extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String mypreference = "mypref";

    private static final String TAG = "AgentProfile";
    Utils utils = new Utils();
    ImageView backbtn;
    TextView agent_id,agent_name,agent_aadhar,agent_phone,agent_mail,agent_aepsim,agent_wallet;
    String ag_details[],androidId,agentName,agentEmail,agentPhone,agentId,aepsim,walletamount, agentAadhaar,username,password,jsonString,responseString;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);
        backbtn = findViewById(R.id.backimg);
        agent_id = findViewById(R.id.agent_id);
        agent_name = findViewById(R.id.agent_name);
        agent_aadhar = findViewById(R.id.agent_aadhar);
        agent_phone = findViewById(R.id.agent_phone);
        agent_mail = findViewById(R.id.agent_mail);
        agent_aepsim=findViewById(R.id.aepsim);
        agent_wallet=findViewById(R.id.walletamount);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId=preferences.getString("AndroidId","No name defined");
        aepsim=preferences.getString("OutletId","No name defined");
        agentName=preferences.getString("AgentName","No name defined");
        agentEmail=preferences.getString("AgentEmail","No name defined");
        agentPhone=preferences.getString("AgentPhone","No name defined");
        agentId=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");

        //areamanager=preferences.getString("AreaManagerName","No name defined");
       // areamanagerId=preferences.getString("AreaManagerId","No name defined");

        agentAadhaar=preferences.getString("AgentAadhaar","No name defined");
        new apiCall_totalWithdrawal().execute();
try{
    agent_id.setText(agentId);
    agent_name.setText(agentName);
    agent_mail.setText(agentEmail);
    agent_phone.setText(agentPhone);
    agent_aadhar.setText(agentAadhaar);
    //agent_areamgr_id.setText(areamanagerId);
    agent_aepsim.setText(aepsim);
}catch (Exception e){
    e.printStackTrace();
}
//Log.d("TAG","Responses are:"+androidId+agentPhone+agentEmail+agentName+agentId+aepsim);
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        });
    }
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
                Intent intent=new Intent(getApplicationContext(),activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    class apiCall_totalWithdrawal extends AsyncTask<Request, Void, String> {
        Utils utils = new Utils();
        @SuppressLint("HardwareIds")
        @Override
        protected String doInBackground(Request... requests) {
            OkHttpClient httpClient;

            JSONObject jsonObject = new JSONObject();
            httpClient = utils.createAuthenticatedClient(agentId, password);
            Log.d("TAG","API Message 1:"+agentPhone+agentId+password);
            try {
                jsonObject.put("accountno",agentPhone);
                jsonObject.put("status", "SUCCESS");
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/SumTotWithdrawalByAccountNoandStatus")
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
                    responseString = response.body().string();
                    Log.d("TAG","API Message 2:"+responseString);
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(responseString);
                        walletamount=jsonResponse.getString("sum");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG","API Message 3:"+walletamount);
                   activity_AgentsProfile.this.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           setAgent_walletAmount(walletamount);
                       }
                   });
                }

            });


            return null;
        }


    }
    public void setAgent_walletAmount(String walletamount){
        activity_AgentsProfile.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    agent_wallet.setText(walletamount);
                }
                catch (NullPointerException e)
                {

                }
            }
        });

    }
}
