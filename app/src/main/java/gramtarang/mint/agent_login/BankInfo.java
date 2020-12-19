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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
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

public class BankInfo extends Fragment {


    String jsonString,response_String,username,password;
    String agentId;
    String ipAccountNo ;
    String bankName ;
    String bankAccountNo;
    String ifsccode;
    String branch;
    String status;
    String updatedon;
    SharedPreferences preferences;
    public final String mypreference = "mypref";
    private Handler mHandler = new Handler(Looper.getMainLooper());

    TextView tv_agentId;
    TextView tv_ipAccountNo ;
    TextView tv_bankName ;
    TextView tv_bankAccountNo;
    TextView tv_ifsccode;
    TextView tv_branch;
    TextView tv_status;
    TextView tv_updatedon;



    public BankInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank_info, container, false);


        api_getBankInfo(v);

        tv_agentId = v.findViewById(R.id.agent_id);
        tv_ipAccountNo  = v.findViewById(R.id.agent_phone);
        tv_bankName  = v.findViewById(R.id.agent_bankname);
        tv_bankAccountNo = v.findViewById(R.id.agent_bankaccountno);
        tv_ifsccode = v.findViewById(R.id.agent_ifsccode);
        tv_branch = v.findViewById(R.id.agent_branch);
        tv_status = v.findViewById(R.id.agent_status);
        tv_updatedon = v.findViewById(R.id.agent_updatedon);

        return v;
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
            //jsonObject.put("Status", status);
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
                            bankAccountNo = jsonResponse.getString("ipaccountno");
                            ifsccode = jsonResponse.getString("ifsccode");
                            branch = jsonResponse.getString("branch");
                            status = jsonResponse.getString("status");
                            updatedon = jsonResponse.getString("updatedon");

                        }catch (JSONException e) {
                        e.printStackTrace();
                    }
                        mHandler.post(new Runnable() {
                            public void run() {

                                setvalues(agentId,
                                ipAccountNo ,
                                bankName ,
                                bankAccountNo,
                                ifsccode,
                                branch,
                                status,
                                updatedon);
                            }
                        });


                } else {
                    Snackbar.make(v, "You are not getting any Response From Server !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setvalues(String aId,String aPhoneNo, String aBankName,
                           String aBankAccountNo, String aIfscCode,
                           String aBranch, String aStatus, String aUpdatedon) {
       try{
        tv_agentId.setText(aId);
        tv_ipAccountNo.setText(aPhoneNo);
        tv_bankName.setText(aBankName);
        tv_bankAccountNo.setText(aBankAccountNo);
        tv_ifsccode.setText(aIfscCode);
        tv_branch.setText(aBranch);
        if(status.equals("1")){
            tv_status.setText("ACTIVE");
        }
        else{
            tv_status.setText("INACTIVE");
        }

        tv_updatedon.setText(aUpdatedon);}catch (Exception e){e.printStackTrace();}
    }

}