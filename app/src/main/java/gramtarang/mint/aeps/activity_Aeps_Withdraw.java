package gramtarang.mint.aeps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.CaptureResponse;
import gramtarang.mint.utils.CheckNetwork;

import gramtarang.mint.utils.DialogActivity;

import gramtarang.mint.utils.LogOutTimer;

import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class activity_Aeps_Withdraw extends AppCompatActivity implements LogOutTimer.LogOutListener {
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume()");
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
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    private static final String TAG = "AepsWithdraw";
    String responseString,response_String;
    String selected_bank_name;
    String selected_bank_id;
    String latitude;
    String longitude,agentphn;
    String en_aad;
    String en_name;

    String en_am;
    String message;
    String status;
    String status_code,username,password;
    String pidDataXML;
    String pidOptions;
    String agentId;
   double balance;
    String androidId;
    String bank_RRN;
    String transaction_type;
    String merchant_transid;
    String timestamp;
    String amount,outletid;
    String transaction_status;
    String fpTransId;
    //bellow variable re for rd Service of fingerPrint device
    public static String ci;
    private static String dc;
    public static String errInfo;
    public static String fCount;
    public static String fType ;
    public static String hmac;
    public static String iCount ;
    public static String iType ;
    public static String mc ;
    public static String mi;
    public static String nmPoints;
    public static String qScore;
    public static String rdsID;
    public static String rdsVer ;
    public static String format;
    public static String errcode;
    public static String dpId;
    public static String SessionKey;
    public static String PidDatatype;
    public static String Piddata;
    public static String pCount;
    public static String pType;
    /////////////////////////////////////
    public static String pidData_json;
    public static Activity activity;
    ImageView backbtn;
    OkHttpClient client,httpClient;
    Button btn_submit;
    EditText et_adhaarNumber;
    EditText et_amount;

    EditText et_CustomerName;

    AutoCompleteTextView bank_autofill;

    ArrayList<String> arrayList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankIIN = new ArrayList<String>();

    //LoadingDialog loadingDialog = new LoadingDialog(activity_Aeps_Withdraw.this);
    //bellow boolean variables for validation
    boolean isValidAadhar,isValidBankName,isValidName,isValidAmount,isValidPhone;
    boolean doubleBackToExitPressedOnce = false;

    //double bank button press to exit/ logout
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
    //This method working as auto scaling of ui by density
    public void adjustFontScale(Configuration configuration) {

        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }
    //This method working as auto scaling of ui by density
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        newConfig.densityDpi= (int) (metrics.density * 160f);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = newConfig.densityDpi * metrics.density;
        getBaseContext().getResources().updateConfiguration(newConfig, metrics);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        onConfigurationChanged(getResources().getConfiguration());
        setContentView(R.layout.activity_aeps_withdraw);
//location permission check
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = new OkHttpClient();
        // //pidOptions is given by RD service of fingerPrint device which is used for Rd service API calling
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";
        et_adhaarNumber = findViewById(R.id.aadhaar);
        et_amount = findViewById(R.id.totamount);
        et_CustomerName = findViewById(R.id.entered_name);

        backbtn = findViewById(R.id.backimg);
        //Getting the required data from sharedPreferences
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        outletid=preferences.getString("OutletId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");
        //androidId=preferences.getString("AndroidId","No name defined");
        //latitude="11.111";
        //longitude="121.11";
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //AutoFIll the bank name
        bank_autofill = findViewById(R.id.bank_auto);
        Utils utils=new Utils();
       //
        //search for BANKIIN & BAnk name in database
        //arrayList_bankName =query.getBankNames();
       // arrayList_bankIIN =query.getBankIIN();
        new apiCall_getBanks().execute();
        btn_submit = findViewById(R.id.submitbtn);
        btn_submit.setEnabled(true);
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(activity_Aeps_Withdraw.this)) //returns true if internet available
                {
                    btn_submit.setEnabled(false);
                    //bankname autofilled
                    //selected_bank_id= utils.AutoCompleteTV_BankId(activity_Aeps_Withdraw.this, bank_autofill, arrayList_bankName, arrayList_bankIIN,TAG);
                    //selected_bank_name=utils.AutoCompleteTV_BankName(activity_Aeps_Withdraw.this, bank_autofill, arrayList_bankName, arrayList_bankIIN,TAG);
                    ////Collecting the user inputted data
                    en_aad = et_adhaarNumber.getText().toString().trim();
                    en_name = et_CustomerName.getText().toString().trim();

                    en_am = et_amount.getText().toString().trim();
                    //validations of user inputted data
                    isValidAadhar=utils.isValidAadhaar(en_aad);
                    isValidName=utils.isValidName(en_name);

                    //  isValidAmount=utils.isValidAmount(en_am);
                 //   isValidBankName= query.isValidBankName(selected_bank_name);

                    if (isValidAadhar  && isValidName ) {
                        try {
                            Log.d(TAG,"Selected try bank: "+selected_bank_name+" "+selected_bank_id);
                            //Rd service api calling method called
                            Matra_capture(pidOptions);
                           // parseData();
                            //fingerprintDataConvertedtoJSON();                       } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Fingerprint Device not connected.", Toast.LENGTH_LONG).show();
                            btn_submit.setEnabled(true);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //If validation got failed, setting the error messages
                    if(!isValidAadhar){
                        et_adhaarNumber.setError("Enter Valid Aadhaar Number");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidName){
                        et_CustomerName.setError("Enter Valid Name");
                        btn_submit.setEnabled(true);
                    }

                  /*  if(!isValidAmount){
                        et_amount.setError("Amount should be in range between INR100 to INR10000");
                        btn_submit.setEnabled(true);
                    }*/
                } else {
                    Toast.makeText(activity_Aeps_Withdraw.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    btn_submit.setEnabled(true);
                }

                //callApi();
            }
        });
    }

    class apiCall_getBanks extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            httpClient = utils.createAuthenticatedClient(username, password);
            okhttp3.Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/getBanks")
                    .addHeader("Accept", "*/*")
                    .get()
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();
                    if (response_String != null) {
                        Log.d("TAG","Response is+"+response_String.toString());
                        JSONArray jsonResponse = null;
                        try {
                            jsonResponse = new JSONArray(response_String);
                            for(int j = 0; j < jsonResponse.length(); j++){
                                JSONObject jresponse = jsonResponse.getJSONObject(j);
                                arrayList_bankName.add(jresponse.getString("bankname"));
                                arrayList_bankIIN.add(jresponse.getString("iinno"));
                            }

                            Log.d("TAG","BANK NAMES"+arrayList_bankName+arrayList_bankIIN);
                            setAutoCompleteTV();

                            //   setText(tv_dateofrelease,dateofrelease,tv_version,latest_app_version);

                            //Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease+androidId+latitude+longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e) {
                        }
                    }

                }
            });

            return null;
        }

    }
    public void setAutoCompleteTV(){
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d("TAG","BANK NAMES"+arrayList_bankName+arrayList_bankIIN);
                //utils.AutoCompleteTV_BankId(activity_Aeps_Withdraw.this,bank_autofill,arrayList_bankName,arrayList_bankIIN,TAG);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity_Aeps_Withdraw.this,android.R.layout.select_dialog_item, arrayList_bankName);
                bank_autofill.setThreshold(1);
                bank_autofill.setAdapter(adapter);
                Log.d(TAG,"array after async: "+arrayList_bankName);
                Log.d(TAG,"array after async id: "+arrayList_bankIIN);


                bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(c, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                        selected_bank_name = adapter.getItem(position).toString();
                        int selected_bank_index = arrayList_bankName.indexOf(adapter.getItem(position));
                        selected_bank_id = arrayList_bankIIN.get(selected_bank_index);
                        Log.d(TAG, "array Selected bank: " + selected_bank_name);
                        Log.d(TAG, "array Selected bank index: " + selected_bank_index);
                        Log.d(TAG, "array Selected bank ID: " + selected_bank_id);
                        /*al.add(banksID_arr.get(selected_bank_index));
                        al.add(selected_bank);
                        arr=al.toArray(arr);*/
                    }
                });
            }
        });

    }


    //mantra_capture method leads to the RD service api calling
    // it it taking the pid data after the user giving the fingerprint
    /*@params pidOptions
     * @return pidDataXML*/
    public String Matra_capture(String pidOptions) {
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        return pidDataXML;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            //data we are getting from rd service api
            pidDataXML = data.getStringExtra("PID_DATA");
            if(pidDataXML==null){
                DialogActivity.DialogCaller.showDialog(activity_Aeps_Withdraw.this, "Alert", "Fingerprint data not captured.\nPlease try again.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                btn_submit.setEnabled(true);
            }
            if (pidDataXML != null) {
                XmlPullParserFactory pullParserFactory;
                try {
                    pullParserFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = pullParserFactory.newPullParser();
                    InputStream is = new ByteArrayInputStream(pidDataXML.getBytes(StandardCharsets.UTF_8));
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(is, null);
                    processParsing(parser);

                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }}

    //data we are getting from rd service api in XML fromat so in
    //processParsing method i leads for parsing , we are trying to separate the whole xml data
    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        CaptureResponse captureResponse = null;
        while(eventType!= XmlPullParser.END_DOCUMENT){
            String eltName = null;
            switch (eventType){
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if ("PidData".equals(eltName)){
                        captureResponse = new CaptureResponse();
                    }else if (captureResponse != null){
                        if ("DeviceInfo".equals(eltName)){
                            captureResponse.dpID = parser.getAttributeValue(null,"dpId");
                            captureResponse.rdsID = parser.getAttributeValue(null,"rdsId");
                            captureResponse.rdsVer = parser.getAttributeValue(null,"rdsVer");
                            captureResponse.dc = parser.getAttributeValue(null,"dc");
                            captureResponse.mi=parser.getAttributeValue(null,"mi");
                            captureResponse.mc = parser.getAttributeValue(null,"mc");

                            dpId = captureResponse.dpID;
                            rdsID = captureResponse.rdsID;
                            rdsVer = captureResponse.rdsVer;
                            dc = captureResponse.dc;
                            mi = captureResponse.mi;
                            mc = captureResponse.mc;
                        } else if ("Resp".equals(eltName)) {
                            captureResponse.errCode = parser.getAttributeValue(null,"errCode");
                            captureResponse.errInfo = parser.getAttributeValue(null,"errInfo");
                            captureResponse.fType = parser.getAttributeValue(null,"fType");
                            captureResponse.fCount = parser.getAttributeValue(null,"fCount");
                            captureResponse.iCount = parser.getAttributeValue(null,"iCount");
                            captureResponse.iType = parser.getAttributeValue(null,"iType");
                            captureResponse.format = parser.getAttributeValue(null,"format");
                            captureResponse.nmPoints = parser.getAttributeValue(null,"nmPoints");
                            captureResponse.qScore = parser.getAttributeValue(null,"qScore");
                            captureResponse.pCount = parser.getAttributeValue(null,"qScore");
                            captureResponse.pType = parser.getAttributeValue(null,"qScore");

                            errcode = captureResponse.errCode;
                            errInfo = captureResponse.errInfo;
                            fType = captureResponse.fType;
                            fCount = captureResponse.fCount;
                            format = captureResponse.format;
                            nmPoints = captureResponse.nmPoints;
                            qScore = captureResponse.qScore;
                            iCount = captureResponse.iCount;
                            iType = captureResponse.iType;
                            pCount = captureResponse.pCount;
                            pType = captureResponse.pType;
                        } else if ("Skey".equals(eltName)) {

                            captureResponse.ci = parser.getAttributeValue(null, "ci");
                            captureResponse.sessionKey = parser.nextText();

                            ci = captureResponse.ci;
                            SessionKey = captureResponse.sessionKey;
                        } else if ("Hmac".equals(eltName)) {
                            captureResponse.hmac = parser.nextText();
                            hmac = captureResponse.hmac;
                        } else if ("Data".equals(eltName)) {
                            captureResponse.PidDatatype = parser.getAttributeValue(null, "type");
                            captureResponse.Piddata = parser.nextText();
                            PidDatatype = captureResponse.PidDatatype;
                            Piddata = captureResponse.Piddata;
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        fingerprintDataConvertedtoJSON();
    }
    //The data we are collecting after parsing the whole xml data which is coming from rd service api call(from fingerprint
    // ), then we are putting into json format as per bankAPi requirement
    private void fingerprintDataConvertedtoJSON() {
        String msgStr = "";
        JSONObject jsonObject = new JSONObject();

        try {
            /*jsonObject.put("errcode", "errcode1");
            jsonObject.put("errInfo", "errInfo1");
            jsonObject.put("fCount", "fCount1");
            jsonObject.put("fType", "fType1");
            jsonObject.put("iCount", "iCount1");
            jsonObject.put("iType", "iType1");
            jsonObject.put("pCount", "pCount1");
            jsonObject.put("pType", "pType1");
            jsonObject.put("nmPoints", "nmPoints1");
            jsonObject.put("qScore", "qScor1e");
            jsonObject.put("dpID", "dpI1d");
            jsonObject.put("rdsID", "rds1ID");
            jsonObject.put("rdsVer", "rdsVer");
            jsonObject.put("dc", "dc1");
            jsonObject.put("mi", "mi1");
            jsonObject.put("mc", "mc");
            jsonObject.put("ci", "c1i");
            jsonObject.put("sessionKey", "SessionK1ey");
            jsonObject.put("hmac", "hma1c");
            jsonObject.put("PidDatatype", "PidDatat1ype");
            jsonObject.put("Piddata", "Pidda1ta");*/
            jsonObject.put("errcode",errcode);
            jsonObject.put("errInfo",errInfo);
            jsonObject.put("fCount",fCount);
            jsonObject.put("fType",fType);
            jsonObject.put("iCount",iCount);
            jsonObject.put("iType",iType);
            jsonObject.put("pCount",pCount);
            jsonObject.put("pType",pType);
            jsonObject.put("nmPoints",nmPoints);
            jsonObject.put("qScore",qScore);
            jsonObject.put("dpID",dpId);
            jsonObject.put("rdsID",rdsID);
            jsonObject.put("rdsVer",rdsVer);
            jsonObject.put("dc",dc);
            jsonObject.put("mi",mi);
            jsonObject.put("mc",mc);
            jsonObject.put("ci",ci);
            jsonObject.put("sessionKey",SessionKey);
            jsonObject.put("hmac",hmac);
            jsonObject.put("PidDatatype",PidDatatype);
            jsonObject.put("Piddata",Piddata);

            pidData_json = jsonObject.toString();
            new apicallOfWithdrawalActivity().execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*apicallOfWithdrawalActivity class basically feeding the required data to the bank api
     * it is running asyncnously in the background so that out main thread will not hampered
     * @return responseString*/
    Utils utils=new Utils();

    class apicallOfWithdrawalActivity extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
//            loadingDialog.startLoading();
            httpClient = utils.createAuthenticatedClient(username, password);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, pidData_json);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/ipwithdrawal")
                    .addHeader("AdhaarNumber",en_aad)
                    .addHeader("Bankid",selected_bank_id)
                    .addHeader("phnumber",agentphn)
                    .addHeader("name",en_name)
                    .addHeader("amount",en_am)
                    .addHeader("imeiNumber",androidId)
                    .addHeader("latitude", latitude)
                    .addHeader("longitude", longitude)
                    .addHeader("outletid",outletid)
                  //  .addHeader("outletid","82923")
                    /*.addHeader("AdhaarNumber","7896541230")
                    .addHeader("Bankid","1022")
                    .addHeader("phnumber","7894652130")
                    .addHeader("name","en_name")
                    .addHeader("amount","1000")
                    .addHeader("imeiNumber","androidId")
                    .addHeader("latitude", "100.3")
                    .addHeader("longitude", "13.4")*/
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // loadingDialog.dismissDialog();
                    //Toast.makeText(activity_Aeps_Withdraw.this,"Your transaction Failed.Please Try Again", Toast.LENGTH_SHORT).show();
                    //btn_submit.setEnabled(true);
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                  if(response_String!= null)
                    {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            // message = jsonResponse.getString("message");
                            status = jsonResponse.getString("status");
                            status_code = jsonResponse.getString("statuscode");
                            merchant_transid=jsonResponse.getString("ipay_uuid");
                            fpTransId=jsonResponse.getString("orderid");
                            //bank_RRN = jsonResponse.getString("ipay_uuid");
                            JSONObject data=jsonResponse.getJSONObject("data");
                            //  JSONObject jsonData=new JSONObject(data);
                            balance=data.getDouble("balance");
                            amount=data.getString("transactionAmount");
                            transaction_status=data.getString("transactionStatus");
                            balance=data.getDouble("balance");
                            bank_RRN=data.getString("bankRRN");
                            transaction_type=data.getString("transactionType");
                            timestamp=data.getString("requestTransactionTime");
                            message=data.getString("message");
                            //status=jsonData.getString("status");
                            /* */

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                     Intent intent=new Intent(activity_Aeps_Withdraw.this, activity_Aeps_Withdrawal_Receipt.class);
                        intent.putExtra("trans_amount",en_am);
                        intent.putExtra("balance",balance);
                        intent.putExtra("merchant_transid",merchant_transid);
                        intent.putExtra("timestamp",timestamp);
                        intent.putExtra("aadhaar",en_aad);
                        intent.putExtra("bank_name",selected_bank_name);
                        intent.putExtra("agent_id",agentId);
                        intent.putExtra("rrn_no",bank_RRN);
                        intent.putExtra("custName",en_name);
                        intent.putExtra("message",message);
                        intent.putExtra("fpTransId",fpTransId);
                        intent.putExtra("status",status);
                        intent.putExtra("status_code",status_code);
                        intent.putExtra("transaction_type",transaction_type);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(activity_Aeps_Withdraw.this,"You are not getting any Response From Bank !! ",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return responseString ;
        }
    }
    public void parseData(){
        Log.d("TAG","IN PARSER");
       response_String="{\"statuscode\":\"TXN\"," +
               "\"status\":\"Transaction Successful\"," +
               "\"data\":{\"opening_bal\":\"554.39\"," +
               "\"ipay_id\":\"CIJ012035113112983\",\"amount\":\"2.00\"," +
               "\"amount_txn\":\"2.00\",\"account_no\":\"9160884610\"," +
               "\"txn_mode\":\"CR\",\"status\":\"SUCCESS\",\"opr_id\":\"035113772513\"," +
               "\"balance\":\"105398.53\",\"wallet_txn_id\":\"1201216131131DDGYX\"}," +
               "\"timestamp\":\"2020-12-16 13:11:31\",\"ipay_uuid\":\"C939E0F3E3AB8B47DD1A\"," +
               "\"orderid\":\"CIJ012035113112983\",\"environment\":\"PRODUCTION\"}";
      JSONObject jsonResponse = null;
        try {
            jsonResponse = new JSONObject(response_String);
            // message = jsonResponse.getString("message");
            status = jsonResponse.getString("status");
            status_code = jsonResponse.getString("statuscode");
            merchant_transid=jsonResponse.getString("ipay_uuid");
            fpTransId=jsonResponse.getString("orderid");
            //bank_RRN = jsonResponse.getString("ipay_uuid");
            JSONObject data=jsonResponse.getJSONObject("data");
          //  JSONObject jsonData=new JSONObject(data);
            balance=data.getDouble("balance");
            amount=data.getString("transactionAmount");
            transaction_status=data.getString("transactionStatus");
            balance=data.getDouble("balance");
            bank_RRN=data.getString("bankRRN");
            transaction_type=data.getString("transactionType");
            timestamp=data.getString("requestTransactionTime");
            message=data.getString("message");
            //status=jsonData.getString("status");
           /* */

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG","Parsed Data is"+status+status_code+merchant_transid+fpTransId
                +amount+transaction_status+balance+bank_RRN+timestamp+message);
    }
}






