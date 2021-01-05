package gramtarang.instamoney.aeps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gramtarang.instamoney.R;
import gramtarang.instamoney.adapters.Adapter_AEPSReport;
import gramtarang.instamoney.agent_login.Dashboard;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AepsActivity_Report extends AppCompatActivity {

    SharedPreferences preferences;
    public static final String mypreference = "mypref";

    private RecyclerView rv_aepsreport;
    private Adapter_AEPSReport rvAepsAdapter;
    private ArrayList<AepsReport> AepsReportList = new ArrayList<>();
    private String response_String,username,password,jsonString;
    Utils utils = new Utils();
    OkHttpClient httpClient;
    private String orderId;
    private String timeStamp;
    private String agentId;
    private String status;
    private String message;
    private String customerName;
    private String transType;
    private String TransId;
    private String commision;
    private String amount;
    private String retCommTds;
    private String retComm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps__report);

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username = preferences.getString("Username", "No name defined");
        password = preferences.getString("Password", "No name defined");
        username = "9999";
        password = "Test@123";
        api();

        //new apiCall_getReport().execute();

        rv_aepsreport = findViewById(R.id.id_rv_reportList);
        rvAepsAdapter = new Adapter_AEPSReport(AepsReportList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_aepsreport.setLayoutManager(layoutManager);
        rv_aepsreport.setAdapter(rvAepsAdapter);



    }

    private void prepare() {
        String lOrderId=orderId;
        String lTimestamp=timeStamp;
        String lStatus=status;
        String lMessage = message;
        String ltransType=transType;
        String lAmount = amount;
        AepsReport aepsReport = new AepsReport(lOrderId, lTimestamp,"1010", lStatus ,lMessage,
                "customer", ltransType," ","commision",lAmount);
        AepsReportList.add(aepsReport);
        Log.d("report","aepsReport "+aepsReport);
        Log.d("report","AepsReportList "+AepsReportList);
    }


    private void prepareReport(String lOrderId, String lTimestamp, String lStatus, String lMessage,
                               String ltransType, String lRetComm,String lRetCommTds,String lAmount){
        /*Log.d("report","lRetComm "+lRetComm);
        Log.d("report","lRetCommTds "+lRetCommTds);

        double comm = (Double.parseDouble(lRetComm)-Double.parseDouble(lRetCommTds));
        commision = String.valueOf(comm);

        Log.d("report","commision "+commision);

        AepsReport aepsReport = new AepsReport(lOrderId, lTimestamp,"1010", lStatus ,lMessage,
                "customer", ltransType," ",commision,lAmount);
        AepsReportList.add(aepsReport);
        Log.d("report","aepsReport "+aepsReport);
        Log.d("report","AepsReportList "+AepsReportList);*/
    }
    private void api(){
        httpClient = utils.createAuthenticatedClient(username, password);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("agentid", "9999");
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .url("https://aepsapi.gramtarang.org:8008/mint/aeps/getAgentWithdrawalByAgent")
                .addHeader("Accept", "*/*")
                .post(body)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override

            //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
            public void onFailure(Call call, IOException e) {

            }

            //if API call got success then it will go for this onResponse also where we are collection
            //the response as well
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //JSON PARSING
                assert response.body() != null;
                response_String = response.body().string();
                Log.d("Report","Response is: "+response_String);
                if (response_String != null) {
                    JSONArray jsonResponse = null;
                    try {

                        jsonResponse = new JSONArray(response_String);
                        for(int j = 0; j < jsonResponse.length(); j++){
                            JSONObject jresponse = jsonResponse.getJSONObject(j);
                            orderId = jresponse.getString("orderid");
                            timeStamp= jresponse.getString("timestamp");
                            //agentId= jresponse.getString("orderid");
                            status= jresponse.getString("statuscode");
                            message= jresponse.getString("bodystatus");
                            //customerName= jresponse.getString("orderid");
                            transType= jresponse.getString("txn_mode");
                            //TransId= jresponse.getString("orderid");
                            retCommTds  = jresponse.getString("retcomm");
                            retComm= jresponse.getString("retcomm");
                            amount= jresponse.getString("amount");
                            /*AepsActivity_Report.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    prepare();
                                    //prepareReport(orderId,timeStamp,status,message,transType,retComm,retCommTds,amount);
                                }
                            });*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    prepare();


                }
            }

        });

    }

    class apiCall_getReport extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {



            return null;
        }

    }
}