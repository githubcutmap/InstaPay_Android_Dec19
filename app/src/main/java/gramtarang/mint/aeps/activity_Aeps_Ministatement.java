package gramtarang.mint.aeps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.api.MobileSMSAPI;
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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*activity_Aeps_Ministatement activity contains every functionaries about the AEPS ministement transaction
 * like it will take the user inputted data then verify those data , feed to the bank api & collection the response
 * then send the collected data to the receipt screen*/

public class activity_Aeps_Ministatement extends AppCompatActivity implements LogOutTimer.LogOutListener {
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
    Utils utils=new Utils();
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    private static final String TAG = "MINI STATEMENT";
    String username,password,transtype="MINI STATEMENT",agent_phone_number,agent_name,selected_bank_name,selected_bank_id,agentid,latitude,longitude,banks,selected_bank,en_aadhaar, en_name, en_phn,BankId,timeStamp2,avlBalance, responseString,androidId,mdate,mtxnType,mnarration,mamount,pidDataXML, message, status, status_code,custName,ipAddress,lati,longi,transaction_amount, pidOptions,data, timestamp,fpTransId,agentId;

    int i;
    //LoadingDialog loadingDialog = new LoadingDialog(activity_Aeps_Ministatement.this);
    //bellow variable re for rd Service of fingerPrint device
    public static String ci;
    private static String dc;
    public static String errInfo;
    public static String fCount;
    public static String fType;
    public static String hmac;
    public static String iCount;
    public static String iType;
    public static String mc;
    public static String mi;
    public static String nmPoints;
    public static String qScore;
    public static String rdsID;
    public static String rdsVer;
    public static String format;
    public static String errcode;
    public static String dpId;
    public static String SessionKey;
    public static String PidDatatype;
    public static String Piddata;
    public static String pCount;
    public static String pType;
    String bank_RRN,outletid,agentphn;
    ///////////////////////////////////////////////////////
    public static String pidData_json;
    OkHttpClient client,httpClient;
    ImageView backbtn;
    EditText et_AdhaarNumber, et_NameOFTheCustomer;
    Button btn_submit;
    boolean isValidAadhar,isValidBankName,isValidName;
    AutoCompleteTextView bank_autofill;
String response_String;
    ArrayList<String> arrList_BankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankIIN = new ArrayList<String>();

    boolean doubleBackToExitPressedOnce = false;

    //Double press BAckButton to exit(TO logout)
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

    List<HashMap<String,String>> list = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.ministatement_layout);
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        client = new OkHttpClient();
        //pidOptions is given by RD service of fingerPrint device which is used for Rd service API calling
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";

        et_AdhaarNumber = findViewById(R.id.aadhaar_no);
        et_NameOFTheCustomer = findViewById(R.id.entered_name);

        btn_submit = findViewById(R.id.submitbtn);
        btn_submit.setEnabled(true);
        backbtn = findViewById(R.id.backimg);
        //Getting the required data from sharedPreferences
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        agentId=preferences.getString("AgentId","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        outletid=preferences.getString("OutletId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");

        bank_autofill = findViewById(R.id.bank_auto);
        Utils utils=new Utils();
        new apiCall_getBanks().execute();

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
                if (CheckNetwork.isInternetAvailable(activity_Aeps_Ministatement.this)) //returns true if internet available
                {
                    btn_submit.setEnabled(false);
                    //Collecting the user inputted data
                    en_aadhaar = et_AdhaarNumber.getText().toString().trim();
                    en_name = et_NameOFTheCustomer.getText().toString().trim();
                  //  en_phn = et_PhoneNumber.getText().toString().trim();
                    //validating the user inputted Data
                    isValidAadhar=utils.isValidAadhaar(en_aadhaar);
                   // selected_bank_id= utils.AutoCompleteTV_BankId(activity_Aeps_Ministatement.this, bank_autofill, arrList_BankName, arrayList_bankIIN,TAG);
                    //selected_bank_name=utils.AutoCompleteTV_BankName(activity_Aeps_Ministatement.this, bank_autofill, arrList_BankName, arrayList_bankIIN,TAG);
                    isValidName=utils.isValidName(en_name);
                   // isValidPhone=utils.isValidPhone(en_phn);
                    //isValidBankName= query.isValidBankName(selected_bank_name);
                    if (isValidAadhar  && isValidName) {
                        try {
                            //Rd service api calling method called
                            Log.d(TAG,"Selected try bank: "+selected_bank_name+" "+selected_bank_id);
                            Matra_capture(pidOptions);
                            // fingerPrintDataConvertingJSON();
                        }
                        catch (Exception  e) {
                            Toast.makeText(activity_Aeps_Ministatement.this, "Fingerprint Device not connected.", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Fingerprint Device not connected.");
                            btn_submit.setEnabled(true);
                        }

//If validation got failed, setting the error messages
                    }
                    if(!isValidAadhar){
                        et_AdhaarNumber.setError("Enter Valid Aadhaar Number");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidName){
                        et_NameOFTheCustomer.setError("Enter Valid Name");
                        btn_submit.setEnabled(true);
                    }
                    /*if(!isValidBankName){
                        bank_autofill.setError("Enter Valid Bank Name");
                        btn_submit.setEnabled(true);
                    }*/
                  /*  if(!isValidPhone){
                        et_PhoneNumber.setError("Enter Valid Phone Number");
                        btn_submit.setEnabled(true);
                    }*/


                } else {
                    Toast.makeText(activity_Aeps_Ministatement.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "No Internet Connection");

                    btn_submit.setEnabled(true);

                }
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
                                arrList_BankName.add(jresponse.getString("bankname"));
                                arrayList_bankIIN.add(jresponse.getString("iinno"));
                            }

                            Log.d("TAG","BANK NAMES"+arrList_BankName+arrayList_bankIIN);
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
                Log.d(TAG, "setauto complete: " + arrList_BankName + arrayList_bankIIN);
                //utils.AutoCompleteTV_BankId(activity_Aeps_BalanceEnquiry .this,bank_autofill,arryList_bankName,arrayList_bankNumber,TAG);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity_Aeps_Ministatement.this,android.R.layout.select_dialog_item, arrList_BankName);
                bank_autofill.setThreshold(1);
                bank_autofill.setAdapter(adapter);
                Log.d(TAG,"array after async: "+arrList_BankName);
                Log.d(TAG,"array after async id: "+arrayList_bankIIN);


                bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(c, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                        selected_bank_name = adapter.getItem(position).toString();
                        int selected_bank_index = arrList_BankName.indexOf(adapter.getItem(position));
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
        Log.d(TAG, "capture: this is a log For capture :-" + pidOptions);
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
        }
    }

    //data we are getting from rd service api in XML fromat so in
    //processParsing method i leads for parsing , we are trying to separate the whole xml data
    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        int eventType = parser.getEventType();
        CaptureResponse captureResponse = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();Log.d(TAG, "processParsing: eltName " + eltName);

                    if ("PidData".equals(eltName)) {
                        captureResponse = new CaptureResponse();
                    } else if (captureResponse != null) {
                        if ("DeviceInfo".equals(eltName)) {
                            captureResponse.dpID = parser.getAttributeValue(null, "dpId");
                            captureResponse.rdsID = parser.getAttributeValue(null, "rdsId");
                            captureResponse.rdsVer = parser.getAttributeValue(null, "rdsVer");
                            captureResponse.dc = parser.getAttributeValue(null, "dc");
                            captureResponse.mi = parser.getAttributeValue(null, "mi");
                            captureResponse.mc = parser.getAttributeValue(null, "mc");

                            dpId = captureResponse.dpID;
                            rdsID = captureResponse.rdsID;
                            rdsVer = captureResponse.rdsVer;
                            dc = captureResponse.dc;
                            mi = captureResponse.mi;
                            mc = captureResponse.mc;

                        } else if ("Resp".equals(eltName)) {
                            captureResponse.errCode = parser.getAttributeValue(null, "errCode");
                            captureResponse.errInfo = parser.getAttributeValue(null, "errInfo");
                            captureResponse.fType = parser.getAttributeValue(null, "fType");
                            captureResponse.fCount = parser.getAttributeValue(null, "fCount");
                            captureResponse.iCount = parser.getAttributeValue(null, "iCount");
                            captureResponse.iType = parser.getAttributeValue(null, "iType");
                            captureResponse.format = parser.getAttributeValue(null, "format");
                            captureResponse.nmPoints = parser.getAttributeValue(null, "nmPoints");
                            captureResponse.qScore = parser.getAttributeValue(null, "qScore");
                            captureResponse.pCount = parser.getAttributeValue(null, "qScore");
                            captureResponse.pType = parser.getAttributeValue(null, "qScore");

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
        fingerPrintDataConvertingJSON();
    }

    //The data we are collecting after parsing the whole xml data which is coming from rd service api call(from fingerprint
    // ), then we are putting into json format as per bankAPi requirement
    private void fingerPrintDataConvertingJSON() {
        String msgStr = "";
        Log.d(TAG,"Inside CashWithdrawRequest");
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("errcode", errcode);
            jsonObject.put("errInfo", errInfo);
            jsonObject.put("fCount", fCount);
            jsonObject.put("fType", fType);
            jsonObject.put("iCount", iCount);
            jsonObject.put("iType", iType);
            jsonObject.put("pCount", pCount);
            jsonObject.put("pType", pType);
            jsonObject.put("nmPoints", nmPoints);
            jsonObject.put("qScore", qScore);
            jsonObject.put("dpID", dpId);
            jsonObject.put("rdsID", rdsID);
            jsonObject.put("rdsVer", rdsVer);
            jsonObject.put("dc", dc);
            jsonObject.put("mi", mi);
            jsonObject.put("mc", mc);
            jsonObject.put("ci", ci);
            jsonObject.put("sessionKey", SessionKey);
            jsonObject.put("hmac", hmac);
            jsonObject.put("PidDatatype", PidDatatype);
            jsonObject.put("Piddata", Piddata);

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
            jsonObject.put("Piddata", "Pidda1ta");
*/
            Log.d(TAG, "cashWithdrawRequest: Json is :" + jsonObject);

            pidData_json = jsonObject.toString();

            //tv.setText(is);

            Log.d(TAG, "cashWithdrawRequest: The is :" + pidData_json);
            // if(!is.equals(null)){
            //  Toast.makeText(AepsBal.this,"Fingerprint Captured Successfully",Toast.LENGTH_SHORT);
            // }
            Log.d(TAG,"Calling Network Class");
            new Ministatement_apiCalling().execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*Ministatement_apicalling class basically feeding the required data to the bank api
     * it is running asyncnously in the background so that out main thread will not hampered
     * @return responseString*/


    class Ministatement_apiCalling extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
//            utils.getprogressDialog(activity_Aeps_Ministatement.this,"Transaction in process","Please Wait...");
            httpClient = utils.createAuthenticatedClient(username, password);
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, pidData_json);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/ipministatement")
                    .addHeader("AdhaarNumber", en_aadhaar)
                    .addHeader("bankId", selected_bank_id)
                    .addHeader("phnumber", agentphn)
                    .addHeader("imeiNumber",androidId)//androidId
                    .addHeader("latitude", String.valueOf(latitude))
                    .addHeader("longitude", String.valueOf(longitude))
                     .addHeader("outletid",outletid)
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //loadingDialog.dismissDialog();
                    btn_submit.setEnabled(true);
                    Toast.makeText(activity_Aeps_Ministatement.this,"Your transaction Failed.Please Try Again", Toast.LENGTH_SHORT).show();

                    // Log.d(TAG, "Your transaction Failed.Please Try Again");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    responseString = response.body().string();
                    if(responseString !=null){
                        //   loadingDialog.dismissDialog();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(responseString);
                            //   message = jsonResponse.getString("message");
                            status = jsonResponse.getString("status");
                            status_code = jsonResponse.getString("statuscode");
                            data = jsonResponse.getString("data");
                            bank_RRN = jsonResponse.getString("ipay_uuid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {

                            JSONObject jsonData = new JSONObject(data);
                            // timeStamp2 = jsonData.getString("timestamp");
                            avlBalance = jsonData.getString("balance");
                            JSONArray ministatement_structure=jsonData.getJSONArray("mini_statement");
                            for(int i=0;i<ministatement_structure.length();i++)
                            {
                                JSONObject curr = ministatement_structure.getJSONObject(i);
                                mdate=curr.getString("date");
                                mtxnType=curr.getString("txnType");
                                mamount=curr.getString("amount");
                                mnarration=curr.getString("narration");
                                transaction_amount="0.00";
                                HashMap<String,String> hashMap_transaction=new HashMap<>();
                                hashMap_transaction.put("date",mdate);
                                hashMap_transaction.put("amount",mamount);
                                hashMap_transaction.put("txnType",mtxnType);
                                hashMap_transaction.put("narration",mnarration);
                                list.add(hashMap_transaction);
                            }
                            // insertlog();

                        } catch (JSONException  e) {

                      Log.d(TAG, "Transaction failed"+message+"\n"+"TimeStamp:"+timeStamp2);

                            e.printStackTrace();
                        }
                        catch (NullPointerException  e) {

                            e.printStackTrace();
                        }
                        new SendTransDetailsSMS().execute();
                        Intent intent = new Intent(activity_Aeps_Ministatement.this, activity_Aeps_Ministatement_Report.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", (Serializable) list);
                        intent.putExtras(bundle);
                        intent.putExtra("Adaar", en_aadhaar);
                        intent.putExtra("timeStamp", timestamp);
                        intent.putExtra("avlBalance", avlBalance);
                        intent.putExtra("custname",en_name);
                        intent.putExtra("bankname",selected_bank_name);

                        startActivity(intent);
                    }else {
                        //Toast.makeText(activity_Aeps_Ministatement.this,"You are not getting any Response From Bank !! ",Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "You are not getting any Response From Bank !! ");

                    }
                }
            });
            return responseString;
        }
    }
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
    public  class SendTransDetailsSMS extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String greet=gethour();
            String flagurl= null;
            try {
                String message= URLEncoder.encode(greet+","+" "+agent_name+"\n"+"Thank you for banking with us"+"\n"+"Your transaction details are:"+"\n"+"Transaction Type:"+"\n"+transtype+"Message:"+status+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
                flagurl =  "http://smslogin.mobi/spanelv2/api.php?username=gramtarang&password=Ind123456&to="+agent_phone_number+"&from=GTIDSP&message="+message;
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