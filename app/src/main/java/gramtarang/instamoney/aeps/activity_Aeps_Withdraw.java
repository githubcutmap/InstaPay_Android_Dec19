package gramtarang.instamoney.aeps;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class activity_Aeps_Withdraw extends AppCompatActivity implements LogOutTimer.LogOutListener {

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
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }

    //DECLARATIONS
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    private static final String TAG = "AepsWithdraw";
    String latitude,selected_bank_id, responseString,response_String,selected_bank_name, longitude,agentphn, en_aad, en_name, en_am, message, status, status_code,username,password, pidDataXML, pidOptions, agentId, balance, androidId, bank_RRN, transaction_type, merchant_transid, timestamp, amount,outletid, transaction_status, fpTransId;
    public static Activity activity;
    ImageView backbtn;
    OkHttpClient httpClient;
    Button btn_submit;
    EditText et_adhaarNumber,et_amount, et_CustomerName;
    AutoCompleteTextView bank_autofill;
    ArrayList<String> arrayList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankIIN = new ArrayList<String>();
    boolean isValidAadhar,isValidName;
    boolean doubleBackToExitPressedOnce = false;
    Utils utils=new Utils();


    //RD SERVICE FINGERPRINT DECLARATIONS
    private String dc;
    public String ci, errInfo, fCount, fType, hmac, iCount, iType, mc, mi, nmPoints, qScore, rdsID, rdsVer, format, errcode, dpId, SessionKey, PidDatatype, Piddata, pCount, pType, pidData_json;


    //BACK PRESSED
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
        //LAYOUT DECLARATIONS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps_withdraw);
        et_adhaarNumber = findViewById(R.id.aadhaar);
        et_amount = findViewById(R.id.totamount);
        et_CustomerName = findViewById(R.id.entered_name);
        backbtn = findViewById(R.id.backimg);
        bank_autofill = findViewById(R.id.bank_auto);
        btn_submit = findViewById(R.id.submitbtn);
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        //PID OPTIONS DATA GIVEN BY RD SERVICE FINGERPRINT
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";


        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        outletid=preferences.getString("OutletId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");



        new apiCall_getBanks().execute();//GET BANKS LIST
        btn_submit.setEnabled(true);



        //BACK BUTTON HANDLING
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });

        //SUBMIT BUTTON HANDLING
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(activity_Aeps_Withdraw.this)) //returns true if internet available
                {
                    btn_submit.setEnabled(false);
                    en_aad = et_adhaarNumber.getText().toString().trim();
                    en_name = et_CustomerName.getText().toString().trim();
                    en_am = et_amount.getText().toString().trim();
                    isValidAadhar=utils.isValidAadhaar(en_aad);
                    isValidName=utils.isValidName(en_name);

                    if (isValidAadhar  && isValidName ) {
                        try {
                            Matra_capture(pidOptions);
                           // parseData();
                            btn_submit.setEnabled(true);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    if(!isValidAadhar){
                        et_adhaarNumber.setError("Enter Valid Aadhaar Number");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidName){
                        et_CustomerName.setError("Enter Valid Name");
                        btn_submit.setEnabled(true);
                    }

                } else {
                    Toast.makeText(activity_Aeps_Withdraw.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    btn_submit.setEnabled(true);
                }


            }
        });
    }


//GET BANKS API
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

                    }
                });
            }
        });

    }

//FINGERPRINT DEVICE CAPTURE
    public String Matra_capture(String pidOptions) {
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        return pidDataXML;
    }

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


    //FP DATA CONVERTING TO JSON
    private void fingerprintDataConvertedtoJSON() {
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


//API CALL TO SERVER
    class apicallOfWithdrawalActivity extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
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
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //JSON PARSING
                    assert response.body() != null;
                    response_String = response.body().string();
                    String jsonData = null;
                  if(response_String!= null)
                    {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            status = jsonResponse.getString("status");
                            status_code = jsonResponse.getString("statuscode");
                            merchant_transid=jsonResponse.getString("ipay_uuid");
                            fpTransId=jsonResponse.getString("orderid");
                            jsonData = jsonResponse.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try{
                            JSONObject data = new JSONObject(jsonData);
                            balance=data.getString("balance");
                            amount=data.getString("transactionAmount");
                            transaction_status=data.getString("transactionStatus");
                            bank_RRN=data.getString("bankRRN");
                            transaction_type=data.getString("transactionType");
                            timestamp=data.getString("requestTransactionTime");
                            message=data.getString("message");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        //INTENT DATA PASSING
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


    //HARDCODED DATA PARSING
    public void parseData(){
        Log.d("TAG","IN PARSER");
       response_String="{\"statuscode\":\"TXS\"," +
               "\"status\":\"Transaction Successful\"," +
               "\"data\":{\"opening_bal\":\"554.39\"," +
               "\"ipay_id\":\"CIJ012035113112983\",\"amount\":\"2.00\"," +
               "\"amount_txn\":\"2.00\",\"account_no\":\"9160884610\"," +
               "\"txn_mode\":\"CR\",\"status\":\"SUCCESS\",\"opr_id\":\"035113772513\"," +
               "\"balance\":\"105398.53\",\"wallet_txn_id\":\"1201216131131DDGYX\"}," +
               "\"timestamp\":\"2020-12-16 13:11:31\",\"ipay_uuid\":\"C939E0F3E3AB8B47DD1A\"," +
               "\"orderid\":\"CIJ012035113112983\",\"environment\":\"PRODUCTION\"}";
       responseString="{\"statuscode\":\"TXN\",\"status\":\"Transaction Successful\",\"data\":{\"opening_bal\":\"855.79\",\"ipay_id\":\"CIJ012036015255951\",\"amount\":\"101.00\",\"amount_txn\":\"101.37\",\"account_no\":\"9963022226\",\"txn_mode\":\"CR\",\"status\":\"SUCCESS\",\"opr_id\":\"036015190501\",\"balance\":1931.4,\"wallet_txn_id\":\"1201225152601WFSRE\"},\"timestamp\":\"2020-12-25 15:26:01\",\"ipay_uuid\":\"CA73EE56985D9F4CEAE3\",\"orderid\":\"CIJ012036015255951\",\"environment\":\"PRODUCTION\"}";
        String jsonData = null;
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
                jsonData = jsonResponse.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try{
                //bank_RRN = jsonResponse.getString("ipay_uuid");
                JSONObject data = new JSONObject(jsonData);
                //  JSONObject jsonData=new JSONObject(data);
                balance=data.getString("balance");
                amount=data.getString("transactionAmount");
                transaction_status=data.getString("transactionStatus");
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
        Log.d("TAG","Parsed Balance is"+
                balance);
    }
}






