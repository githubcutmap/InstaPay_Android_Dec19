package gramtarang.instamoney.aeps;
//IMPORTS
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.CaptureResponse;
import gramtarang.instamoney.utils.CheckNetwork;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class activity_Aeps_BalanceEnquiry extends AppCompatActivity implements LogOutTimer.LogOutListener {

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
    private static final String TAG = "AepsBalanceEnquiry";
    EditText aadhaar, name;
    Button submit;
    ImageView backbtn;
    boolean isValidAadhar;
    AutoCompleteTextView bank_autofill;
    boolean isValidName;
    String agentphn,username,password,selected_bank_id, selected_bank_name, en_aadhaar, en_name,  latitude, longitude, response_String, androidId, banks, selected_bank, banks_no, pidDataXML, message, status, status_code, pidOptions, data, transaction_status, balance, bank_RRN, transaction_type, merchant_transid, timestamp, fpTransId, agentId;
    OkHttpClient httpClient;
    ArrayList<String> arryList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankNumber = new ArrayList<String>();
    boolean doubleBackToExitPressedOnce = false;
    Utils util = new Utils();
    //RD SERVICE FINGERPRINT DECLARATIONS
    private String dc;
    public String ci, errInfo, fCount, fType, hmac, iCount, iType, mc, mi, nmPoints, qScore, rdsID, rdsVer, format, errcode, dpId, SessionKey, PidDatatype, Piddata, pCount, pType, pidData_json,outletid;


    //BACK PRESSED HANDLING
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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_bal);
        //PID OPTIONS DATA GIVEN BY RD SERVICE FINGERPRINT
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //LAYOUT ELEMENTS DECLARATION
        bank_autofill = findViewById(R.id.bank_auto);
        aadhaar = findViewById(R.id.aadhar_no);
        name = findViewById(R.id.name);
        submit = findViewById(R.id.submit);
        backbtn = findViewById(R.id.backimg);

        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude = preferences.getString("Latitude", "No name defined");
        longitude = preferences.getString("Longitude", "No name defined");
        agentId=preferences.getString("AgentId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        outletid=preferences.getString("OutletId","No name defined");

        new apiCall_getBanks().execute();  //GET BANKS API

        //BACK BUTTON ON CLICK EVENT
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });

        //SUBMIT BUTTON ON CLICK EVENT
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckNetwork.isInternetAvailable(activity_Aeps_BalanceEnquiry.this)) //returns true if internet available
                {

                    en_aadhaar = aadhaar.getText().toString().trim();
                    en_name = name.getText().toString().trim();
                    isValidAadhar = util.isValidAadhaar(en_aadhaar);
                    isValidName = util.isValidName(en_name);
                     if (isValidAadhar && isValidName) {
                        try {
                           Matra_capture(pidOptions);
                           // fingerprintDataConvertedtoJSON();
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    }
                    if (!isValidAadhar) {
                        aadhaar.setError("Enter Valid Aadhaar Number");
                    }
                    if (!isValidName) {
                        name.setError("Enter Valid Name");
                    }

                } else {
                    Toast.makeText(activity_Aeps_BalanceEnquiry.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                } }
        });

    }

    //API CALL FOR GETTING BANKS LIST
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
                                arryList_bankName.add(jresponse.getString("bankname"));
                                arrayList_bankNumber.add(jresponse.getString("iinno"));
                            }

                            Log.d("TAG","BANK NAMES"+arryList_bankName+arrayList_bankNumber);
                            setAutoCompleteTV();

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

    //AUTO COMPLETE TEXTVIEW
    public void setAutoCompleteTV(){
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "setauto complete: " + arrayList_bankNumber + arryList_bankName);
               final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (activity_Aeps_BalanceEnquiry.this,android.R.layout.select_dialog_item, arryList_bankName);
                bank_autofill.setThreshold(1);
                bank_autofill.setAdapter(adapter);
                bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(c, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                        selected_bank_name = adapter.getItem(position).toString();
                        int selected_bank_index = arryList_bankName.indexOf(adapter.getItem(position));
                        selected_bank_id = arrayList_bankNumber.get(selected_bank_index);
                        Log.d(TAG, "array Selected bank: " + selected_bank_name);
                        Log.d(TAG, "array Selected bank index: " + selected_bank_index);
                        Log.d(TAG, "array Selected bank ID: " + selected_bank_id);
                    }
                });
            }
        });
     }

    //RD service api calling of mantra Fingerprint device
    public String Matra_capture(String pidOptions) {
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        return pidDataXML;
    }

    //ON ACTIVITY RESULT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            //data we are getting from rd service api
            pidDataXML = data.getStringExtra("PID_DATA");
            if(pidDataXML==null){
                DialogActivity.DialogCaller.showDialog(activity_Aeps_BalanceEnquiry.this, "Alert", "Fingerprint data not captured.\nPlease try again.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                submit.setEnabled(true);
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

//PROCESS PARSING
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

    //FP DATA TO JSON
    private void fingerprintDataConvertedtoJSON() {
        String msgStr = "";
        JSONObject jsonObject = new JSONObject();

        try {
           /* jsonObject.put("errcode", "errcode1");
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
            new apiCall_BalanceEnquiry().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Utils utils=new Utils();

    //API CALL TO SERVER
    class apiCall_BalanceEnquiry extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
           // httpClient = utils.createAuthenticatedClient(username, password);
            httpClient = utils.createAuthenticatedClient("1011","Test@123");
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, pidData_json);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/ipbalanceenquiry")
                   .addHeader("AdhaarNumber", en_aadhaar)
                    .addHeader("Bankid", selected_bank_id)
                    .addHeader("phnumber", agentphn)
                    .addHeader("name", en_name)
                    .addHeader("imeiNumber", androidId)
                    .addHeader("latitude", latitude)
                    .addHeader("longitude", longitude)
                    .addHeader("outletid",outletid)
                    .addHeader("Accept", "*/*")
                /*   .addHeader("AdhaarNumber", "123456781190")
                     .addHeader("Bankid", "1234")
                     .addHeader("phnumber", "7896541230")
                     .addHeader("name", "Testinggg")
                     .addHeader("imeiNumber", "1234567890")
                     .addHeader("latitude", "17.7509436")
                     .addHeader("longitude", "83.2457357")
                     .addHeader("outletid","12345")*/

                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG","IN API CALL FAILURE"+e);
                    //loadingDialog.dismissDialog();
                    //  Toast.makeText(activity_Aeps_BalanceEnquiry.this, "Server not Connected.", Toast.LENGTH_SHORT).show();
                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();

                    //  Toast.makeText(activity_Aeps_BalanceEnquiry.this, response_String, Toast.LENGTH_SHORT).show();


                    //JSON PARSING
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            status = jsonResponse.getString("status");
                            status_code = jsonResponse.getString("statuscode");
                            bank_RRN = jsonResponse.getString("ipay_uuid");
                            data = jsonResponse.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {

                            JSONObject jsonData = new JSONObject(data);
                            transaction_status = jsonData.getString("status");
                            balance = jsonData.getString("balance");
                            fpTransId = jsonData.getString("opr_id");
                            merchant_transid = jsonData.getString("ipay_id");
                            timestamp = jsonData.getString("timestamp");
                        } catch (JSONException e) {

                        } catch (NullPointerException e) {
                        }


                        //PASSING INTENT DATA
                        Intent intent = new Intent(activity_Aeps_BalanceEnquiry.this, activity_Aeps_BalanceEnq_Receipt.class);
                        intent.putExtra("balance", balance);
                        intent.putExtra("merchant_transid", merchant_transid);
                        intent.putExtra("timestamp", timestamp);
                        intent.putExtra("aadhaar", en_aadhaar);
                        intent.putExtra("bank_name", selected_bank_name);
                        intent.putExtra("agent_id", agentId);
                        intent.putExtra("rrn_no", bank_RRN);
                        intent.putExtra("custName", en_name);
                        intent.putExtra("message", message);
                        intent.putExtra("fpTransId", fpTransId);
                        intent.putExtra("message", message);
                        intent.putExtra("status", status);
                        intent.putExtra("status_code", status_code);
                        intent.putExtra("transaction_type", transaction_type);
                        startActivity(intent);



                    } else {
                        Toast.makeText(activity_Aeps_BalanceEnquiry.this, "You are not getting any Response From Bank !! ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            return null;
        }
    }
    public void sendDatatoServer(String op1) throws IOException {

        HttpUrl.Builder httpBuilder = HttpUrl.parse("https://aepsapi.gramtarang.org:8008/mint/aeps/printPXML").newBuilder();
        httpBuilder.addQueryParameter("pxml",op1);
        Request request = new Request.Builder().url(httpBuilder.build()).build();
        Response response = httpClient.newCall(request).execute();

    }

}


