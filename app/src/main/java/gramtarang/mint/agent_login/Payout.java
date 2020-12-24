package gramtarang.mint.agent_login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Payout extends Fragment {

     private String jsonString,response_String,username,password;
     private String agentId;
     private String ipAccountNo ;
     private String bankName ;
     private String bankAccountNo;
     private String ifsccode;
     private String branch;
     private String status;
     private String updatedon;
     private String method,spkey;
     private String bank;
     private String amount;
     private String remarks;

    SharedPreferences preferences;
    public final String mypreference = "mypref";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Spinner sp_key,sp_bank;
    private AutoCompleteTextView actv_bank;
    private EditText et_amount,et_remarks;
    private ArrayList<String> banks_arr = new ArrayList<String>();
    private Button b_submit;


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

        return v;

    }
    private void init(View view){

        sp_key = view.findViewById(R.id.id_sp_spkey);
        sp_bank = view.findViewById(R.id.id_sp_bank);
        //actv_bank = view.findViewById(R.id.id_actv_bank);
        et_amount = view.findViewById(R.id.id_et_amount);
        et_remarks = view.findViewById(R.id.id_et_remarks);
        b_submit = view.findViewById(R.id.id_button_submit);

    }

    private String checkmethod(String m){
        /*DPN- For IMPS
          BPN- For NEFT
          CPN- For RTGS*/
        String t = "Select Type";
        if(m.matches("IMPS")){
            t ="DPN";
        }
        if(m.matches("NEFT")){
            t ="BPN";
        }
        if(m.matches("RTGS")){
            t ="CPN";
        }
        return t;
    }

    private void onClickActivities(View v) {
        b_submit.setOnClickListener(view -> {
            method = sp_key.getSelectedItem().toString();
            bank = sp_bank.getSelectedItem().toString().trim();
            spkey = checkmethod(method);
            amount = et_amount.getText().toString().trim();
            remarks = et_remarks.getText().toString().trim();

            Log.d("onclick",method+" " +bank+" "+ spkey+" "+amount+" "+remarks);

            if(!spkey.matches("Select Type") && !bank.matches("Select Bank") && !amount.isEmpty() && !remarks.isEmpty()){
                api_payoutdirect(v,spkey,amount,remarks);
            }
            if (spkey.matches("Select Type")){
                ((TextView)sp_key.getSelectedView()).setError("Select Type");
            }
            if (bank.matches("Select Bank") || bank.matches("--No Bank--")){
                ((TextView)sp_bank.getSelectedView()).setError("Select Bank");
            }
            if (amount.isEmpty()){
                et_amount.setError("Enter Amount");
            }
            if (remarks.isEmpty()){
                et_remarks.setError("Enter Amount");
            }
        });

    }

    private void api_payoutdirect(View v,String type,String am,String rm){

        /*"sp_key":"DPN",X
                "external_ref":"82923",X
                "credit_account":"055801605365",
                "credit_rmn":"",X
                "ifs_code":"ICIC0000558",X
                "bene_name":"SUNITA",X
                "credit_amount":"11",X
                "upi_mode" : "",X
                "vpa" : "",X
                "latitude":"14.86767",X
                "longitude":"95.3577",
                "endpoint_ip":"192.168.0.6",
                "remarks":"prodfirst",
                "otp_auth":"0",
                "otp":""*/

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        String outletId=preferences.getString("OutletId","No name defined");
        String agentName=preferences.getString("AgentName","No name defined");
        String latitude = preferences.getString("Latitude", "No name defined");
        String longitude = preferences.getString("Longitude", "No name defined");

        Utils utils = new Utils();
        String ipaddress = utils.getMobileIPAddress();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        Log.d("username","usr getagent pending"+username+password);

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sp_key", type);
            jsonObject.put("external_ref", outletId);
            jsonObject.put("credit_account", bankAccountNo); // verify
            jsonObject.put("credit_rmn", "");
            jsonObject.put("ifs_code", ifsccode);
            jsonObject.put("bene_name", agentName);
            jsonObject.put("credit_amount", am);
            jsonObject.put("upi_mode", "");
            jsonObject.put("vpa", "");
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("endpoint_ip", ipaddress); //ip
            jsonObject.put("remarks", rm);
            jsonObject.put("otp_auth", "0");
            jsonObject.put("otp", "");

            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("JSON STRING IS"+jsonString);
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
                    Log.d("TAG","Response is+"+response_String.toString());
                    if(response_String.equals("Insufficient Funds to transfer pls contact customer support")){
                        Snackbar.make(v, "Insufficient Funds to transfer pls contact customer support", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(response_String);

                            String payoutstatus = jsonResponse.getString("status");
                            String ipay_uuid = jsonResponse.getString("ipay_uuid");
                            String orderid = jsonResponse.getString("orderid");
                            Snackbar.make(v, payoutstatus, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                } else {
                    Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void api_getBankInfo(View v){

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");

        Utils utils = new Utils();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        Log.d("username","usr getagent pending"+username+password);

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
                    Log.d("TAG","Response is+"+response_String.toString());
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

                    }catch (JSONException e) {
                        e.printStackTrace();
                        bankName="--No Bank--";
                        Snackbar.make(v, "Banks Unavailable", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    mHandler.post(new Runnable() {
                        public void run() {
                            setbanks(v,bankName);
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
        banks_arr.add("Select Bank");
        try {
            //set spinner text view banks
            banks_arr.add(bankName);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (getActivity(),android.R.layout.select_dialog_item, banks_arr);
            sp_bank.setAdapter(adapter);
        }
        catch (Exception exception){
            exception.printStackTrace();
            Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

}