package gramtarang.instamoney.agent_login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import gramtarang.instamoney.R;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Payout extends Fragment {

    private String jsonString, response_String, username, password;
    private String agentId;
    private String ipAccountNo;
    private String bankName;
    private String bankAccountNo;
    private String ifsccode;
    private String branch;
    private String status;
    private String updatedon,status_code;
    private String method, spkey;
    private String bank;
    private String amount;
    private String remarks;
    private String ipay_uuid;
    private String payoutstatus;
    private String orderid,timestamp;
    private static int TIME_OUT = 1500;
    SharedPreferences preferences;
    public final String mypreference = "mypref";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Spinner sp_key, sp_bank;
    private AutoCompleteTextView actv_bank;
    private EditText et_amount, et_remarks;
    //private ArrayList<String> banks_arr = new ArrayList<String>();
    private Button b_submit;
    private TextView tv_amount,tv_ipayid,tv_message,tv_orderid,tv_timestamp;
    LinearLayout linearLayout;
    ImageView imgback,imgstatus;


    public Payout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_payout, container, false);

        init(v);

        api_getBankInfo(v);

        onClickActivities(v);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestamp = dateFormat.format(new Date()); // Find todays date
        return v;

    }

    private void init(View view) {

        sp_key = view.findViewById(R.id.id_sp_spkey);
        sp_bank = view.findViewById(R.id.id_sp_bank);
        //actv_bank = view.findViewById(R.id.id_actv_bank);
        et_amount = view.findViewById(R.id.id_et_amount);
        et_remarks = view.findViewById(R.id.id_et_remarks);
        b_submit = view.findViewById(R.id.id_button_submit);

        tv_amount=view.findViewById(R.id.amount);
        tv_ipayid=view.findViewById(R.id.ipay_id);
        tv_message=view.findViewById(R.id.msg);
        tv_orderid=view.findViewById(R.id.order_id);
        tv_timestamp=view.findViewById(R.id.timestamp);
        imgback=view.findViewById(R.id.backimg);;
        imgstatus=view.findViewById(R.id.trans_success);
        linearLayout=view.findViewById(R.id.report);
        linearLayout.setVisibility(View.INVISIBLE);

    }

    private String checkmethod(String m) {
        /*DPN- For IMPS
          BPN- For NEFT
          CPN- For RTGS*/
        String tempType = "Select Type";
        if (m.matches("IMPS")) {
            tempType = "DPN";
        }
        if (m.matches("NEFT")) {
            tempType = "BPN";
        }
        if (m.matches("RTGS")) {
            tempType = "CPN";
        }
        return tempType;
    }

    private void onClickActivities(View v) {
        b_submit.setOnClickListener(view -> {
            method = sp_key.getSelectedItem().toString();
            bank = sp_bank.getSelectedItem().toString().trim();
            spkey = checkmethod(method);
            amount = et_amount.getText().toString().trim();
            remarks = et_remarks.getText().toString().trim();

            Log.d("onclick", method + " " + bank + " " + spkey + " " + amount + " " + remarks);

            if (!spkey.matches("Select Type") && !bank.matches("Select Bank") && !amount.isEmpty() && !remarks.isEmpty()) {
                //if(spkey.matches(("DPN")) || spkey.matches(("BPN")) ||(spkey.matches("CPN") && Integer.parseInt(amount)>200000)){
                    api_payoutdirect(v, spkey, amount, remarks);
                //}

            }
            if (spkey.matches("Select Type")) {
                ((TextView) sp_key.getSelectedView()).setError("Select Type");
            }
            if (bank.matches("Select Bank") || bank.matches("--No Bank--")) {
                ((TextView) sp_bank.getSelectedView()).setError("Select Bank");
            }
            if (amount.isEmpty()) {
                et_amount.setError("Enter Amount");
            }
            if (remarks.isEmpty()) {
                et_remarks.setError("Enter Remarks");
            }
            if (spkey.matches("CPN") && Integer.parseInt(amount)<200000){
                et_amount.setError("Amount should be more than 2,00,000");
            }
        });

    }

    private void api_payoutdirect(View v, String type, String am, String rm) {

        /*"sp_key":"DPN",
                "external_ref":"82923",
                "credit_account":"055801605365",
                "credit_rmn":"",
                "ifs_code":"ICIC0000558",
                "bene_name":"SUNITA",
                "credit_amount":"11",
                "upi_mode" : "",
                "vpa" : "",
                "latitude":"14.86767",
                "longitude":"95.3577",
                "endpoint_ip":"192.168.0.6",
                "remarks":"prodfirst",
                "otp_auth":"0",
                "otp":""*/

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username = preferences.getString("Username", "No name defined");
        password = preferences.getString("Password", "No name defined");
        String outletId = preferences.getString("OutletId", "No name defined");
        String agentName = preferences.getString("AgentName", "No name defined");
        String latitude = preferences.getString("Latitude", "No name defined");
        String longitude = preferences.getString("Longitude", "No name defined");

        Utils utils = new Utils();
        String ipaddress = utils.getLocalIpAddress();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        Log.d("username", "usr getagent pending" + username + password);

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sp_key", type);
            //jsonObject.put("agentid", username);
            jsonObject.put("external_ref", outletId);
            jsonObject.put("credit_account", bankAccountNo);
            jsonObject.put("credit_rmn", "");
            jsonObject.put("ifs_code", ifsccode);
            jsonObject.put("bene_name", agentName);
            jsonObject.put("credit_amount", am);
            jsonObject.put("upi_mode", "");
            jsonObject.put("vpa", "");
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude.replace("-", ""));
            jsonObject.put("endpoint_ip", ipaddress); //ip
            jsonObject.put("remarks", rm);
            jsonObject.put("otp_auth", "0");
            jsonObject.put("otp", "");
            /*test.setText("spkey "+type+"\nexternal ref "+outletId+"\naccount "+bankAccountNo+"\nifsc "+ifsccode+
                    "\nlat "+latitude+"\nlon "+longitude+"\nipaddress "+ipaddress);*/

            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("JSON STRING IS" + jsonString);
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        okhttp3.Request request = new Request.Builder()
                .url("https://aepsapi.gramtarang.org:8008/mint/im/user/payoutDirect")
                .addHeader("Accept", "*/*")
                .post(body)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(v,"Server Not Connected",Snackbar.LENGTH_LONG);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                //the response we are getting from api
               response_String = response.body().string();
//response_String="{\"statuscode\":\"TXN\",\"status\":\"Transaction Successful\",\"data\":{\"external_ref\":\"88059\",\"ipay_id\":\"1201229112432XWNEK\",\"transfer_value\":\"11.00\",\"type_pricing\":\"CHARGE\",\"commercial_value\":\"1.1800\",\"value_tds\":\"0.0000\",\"ccf\":\"0.00\",\"vendor_ccf\":\"0.00\",\"charged_amt\":\"12.18\",\"payout\":{\"credit_refid\":\"023317188421\",\"account\":\"917010019014052\",\"ifsc\":\"UTIB0002943\",\"name\":\"KALYAN\"}},\"timestamp\":\"2020-12-29 11:24:35\",\"ipay_uuid\":\"B52A602876C7F9BFF375\",\"orderid\":\"1201229112432XWNEK\",\"environment\":\"PRODUCTION\"}";
                if (response_String != null) {
                    Log.d("TAG", "Response is+" + response_String.toString());
                    //test.setText("ifCase: "+response_String);
                    JSONObject jsonResponse = null;

                    try {
                        jsonResponse = new JSONObject(response_String);
                       // test.setText("tryBlock : "+response_String);

                        payoutstatus = jsonResponse.getString("status");
                        ipay_uuid = jsonResponse.getString("ipay_uuid");
                        orderid = jsonResponse.getString("orderid");
                        status_code=jsonResponse.getString("statuscode");
                        //timestamp=jsonResponse.getString("timestamp");

                       setText(et_amount,"",et_remarks,"");
                       setText2(tv_amount,amount,tv_ipayid,ipay_uuid,tv_message,payoutstatus,tv_orderid,orderid,tv_timestamp,timestamp);
                    }

                    catch (JSONException e) {
                        e.printStackTrace();
                    }



                }  else {
                    Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } /*Snackbar.make(v, payoutstatus + "  \nipayUUID: " + ipay_uuid + "  \nOrderID: " + orderid, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
*/

               // move();




            }
        });
    }
    private void setText(final TextView text,final String value,final TextView text2,final String value2){
       getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
                text2.setText(value2);
            }
        });
    }
    private void setText2(final TextView text,final String value,final TextView text2,final String value2,final TextView text3,final String value3,final TextView text4,final String value4,final TextView text5,final String value5){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayout.setVisibility(View.VISIBLE);
                try{
                text.setText(value);
                text2.setText(value2);
                text3.setText(value3);
                text4.setText(value4);
                text5.setText(value5);
                }catch (Exception e){e.printStackTrace();}
                try{
                    if(status_code.equals("TXS")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imgstatus.setImageDrawable(getResources().getDrawable(R.drawable.success, getActivity().getTheme()));
                        } else {
                            imgstatus.setImageDrawable(getResources().getDrawable(R.drawable.success));
                        }

                    }
                    else{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            imgstatus.setImageDrawable(getResources().getDrawable(R.drawable.fail,getActivity().getTheme()));
                        } else {
                            imgstatus.setImageDrawable(getResources().getDrawable(R.drawable.fail));
                        }

                    }
                } catch (Exception e){e.printStackTrace();}
            }
        });

    }


    private void move(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (payoutstatus.matches("Transaction Successful")){
                            Intent intent = new Intent(getActivity(),activity_AgentProfile.class);
                            startActivity(intent);
                        }

                    }
                }, TIME_OUT);
            }
        });

    }

    private void api_getBankInfo(View v) {

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username = preferences.getString("Username", "No name defined");
        password = preferences.getString("Password", "No name defined");

        Utils utils = new Utils();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        Log.d("username", "usr getagent pending" + username + password);

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("agentid", username);

            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        okhttp3.Request request = new Request.Builder()
                .url("https://aepsapi.gramtarang.org:8008/mint/aeps/bankinfo")
                .addHeader("Accept", "*/*")
                .post(body)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            /* "agentid": "1011",
                     "ipaccountno": "9160884610",
                     "bankaccountno": "1234561235",
                     "bankname": "SBI",
                     "ifsccode": "IDCF1234B",
                     "branch": "VSKP",
                     "status": "1",
                     "updatedon": "2020-12-03 11:00:00"*/
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                //the response we are getting from api
                response_String = response.body().string();

                if (response_String != null) {
                    Log.d("TAG", "Response is+" + response_String.toString());
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response_String);

                        agentId = jsonResponse.getString("agentid");
                        ipAccountNo = jsonResponse.getString("ipaccountno");
                        bankName = jsonResponse.getString("bankname");
                        bankAccountNo = jsonResponse.getString("bankaccountno");
                        ifsccode = jsonResponse.getString("ifsccode");
                        branch = jsonResponse.getString("branch");
                        status = jsonResponse.getString("status");
                        updatedon = jsonResponse.getString("updatedon");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        bankName = "--No Bank--";
                        Snackbar.make(v, "Banks Unavailable", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    mHandler.post(new Runnable() {
                        public void run() {
                            setbanks(v, bankName);
                        }
                    });


                } else {
                    Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setbanks(View v, String bankName) {
        ArrayList<String> banks_arr = new ArrayList<>();
        banks_arr.add("Select Bank");
        try {
            //set spinner text view banks
            banks_arr.add(bankName);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (getActivity(), android.R.layout.select_dialog_item, banks_arr);
            sp_bank.setAdapter(adapter);
        } catch (Exception exception) {
            exception.printStackTrace();
            Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}