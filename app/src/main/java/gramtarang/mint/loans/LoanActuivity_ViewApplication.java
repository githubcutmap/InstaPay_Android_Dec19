package gramtarang.mint.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gramtarang.mint.R;
import gramtarang.mint.adapters.Adapter_loansAgent;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.LogOutTimer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoanActuivity_ViewApplication extends AppCompatActivity implements LogOutTimer.LogOutListener{
    private String TAG="LOAN_viewapplication";
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

    @Override
    public void doLogout() {
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }


    ArrayList<String> LoansListArr = new ArrayList<String>();
    ArrayList<String> LoansDetailsArr = new ArrayList<String>();
    String SelectedAgentId,SelectedLoanId,managerId;
    ListView lv_agents,lv_loans,lv_loansdetails;
    Button confirm,reject,get;
    private RecyclerView rv_agents;
    private LinearLayoutManager layoutManager;
    private Adapter_loansAgent adapter_loansAgent;
    private ArrayList<String> agents;
    LinearLayout ll_buttons;
    Spinner sp_loanStatus;
    OkHttpClient client;
    String jsonString,response_String,catagory;
    SharedPreferences preferences;
    public final String mypreference = "mypref";
    //String[] AgentArray;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_view_application);

        sp_loanStatus = findViewById(R.id.id_sp_loanStatus);

        lv_agents = findViewById(R.id.listview_agents);
       // lv_loans = findViewById(R.id.listview_loan);
        //lv_loansdetails = findViewById(R.id.listview_loandetails);
        ll_buttons = findViewById(R.id.ll_buttons);
        confirm = findViewById(R.id.confirm);
        reject = findViewById(R.id.reject);
        get = findViewById(R.id.b_get);

        client=new OkHttpClient();
        rv_agents = findViewById(R.id.id_rv_agents);
        layoutManager = new LinearLayoutManager(this);
        //rv_agents.setLayoutManager(layoutManager);
        agents = new ArrayList<String>();
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        managerId=preferences.getString("AreaManagerId","Null");
        managerId = "9999";


        //sp_loanStatus.setOnItemSelectedListener(this);

        /*// Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        sp_loanStatus.setAdapter(dataAdapter);*/
        //
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catagory = sp_loanStatus.getSelectedItem().toString();
                Log.d("TAG","cat"+catagory);
                switch (catagory){
                    case "Select Category": Toast.makeText(LoanActuivity_ViewApplication.this,"select category",Toast.LENGTH_SHORT);
                    case "Pending": agentslist("Pending");break;
                    case "Completed": agentslist("Completed");break;
                    case "Search By ID": ;break;
                    default : Toast.makeText(LoanActuivity_ViewApplication.this,"select category",Toast.LENGTH_SHORT);
                }
            }
        });



    }



    private void agentslist(String cat){
        ArrayList<String> ManagerAgents = new ArrayList<String>();
        api_getAgent();
        //SQLQueries managerAgents = new SQLQueries();
        //ManagerAgents = managerAgents.getUnderAreaMAgents(managerId);
        Log.d("ViewApplication"," After API calling: "+ManagerAgents);
        lv_agents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedAgentId = ManagerAgents.get(position);
                Toast.makeText(getBaseContext(),"selected id:"+SelectedAgentId,Toast.LENGTH_SHORT).show();
                Log.d("ViewApplication"," ids of agents under manager Selected : "+SelectedAgentId);
                //lv_agents.setVisibility(View.GONE);
                if(cat.matches("Pending")){
                    loansListPending(SelectedAgentId);
                }
                if(cat.matches("Completed")){
                    loansListCompleted(SelectedAgentId);
                }
            }


        });
    }

    private void loansListPending(String selectedAgentId) {
        api_loanaAppId(selectedAgentId,1);
        Log.d("ViewApplication"," ids of agents under manager: "+LoansListArr);
        lv_agents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedLoanId = LoansListArr.get(position);
                Toast.makeText(getBaseContext(),"selected id:"+SelectedLoanId,Toast.LENGTH_SHORT).show();
                Log.d("ViewApplication"," ids of agents under manager Selected : "+SelectedLoanId);
                loansDetails(SelectedLoanId);
            }
        });
    }
    private void loansListCompleted(String selectedAgentId) {
        api_loanaAppId(selectedAgentId,1);
        Log.d("ViewApplication"," ids of agents under manager: "+LoansListArr);
        lv_agents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectedLoanId = LoansListArr.get(position);
                Toast.makeText(getBaseContext(),"selected id:"+SelectedLoanId,Toast.LENGTH_SHORT).show();
                Log.d("ViewApplication"," ids of agents under manager Selected : "+SelectedLoanId);
                loansDetails(SelectedLoanId);
            }
        });
    }

    private void loansDetails(String selectedLoanId) {
       /* SQLQueries loansDetails = new SQLQueries();
        LoansDetailsArr = loansDetails.getLoanDetails(selectedLoanId);*/

        Log.d("ViewApplication"," ids of agents under manager: "+LoansDetailsArr);
        String[] LoansDetailArray = LoansDetailsArr.toArray(new String[0]);
        Log.d("ViewApplication"," ids of agents under manager: "+LoansDetailArray);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.loan_viewapplication_lable, LoansDetailArray);
        //lv_loans.setVisibility(View.GONE);
        ll_buttons.setVisibility(View.VISIBLE);
        //lv_loansdetails.setVisibility(View.VISIBLE);
        lv_agents.setAdapter(adapter);

    }

    private void api_loanaAppId(String agentid,int status){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bankmitra_id", agentid);
            jsonObject.put("Status", status);
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        okhttp3.Request request = new Request.Builder()
                .url("http://mintserver.gramtarang.org:8080/mint/im/getloansbyagentid")
                .addHeader("Accept", "*/*")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
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
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response_String);
                        JSONArray llist1 = jsonResponse.getJSONArray("getloansbyagentid");
                        for(int i = 0; i < llist1.length(); i++){
                            String temp = llist1.getJSONObject(i).getString("uniqueid");
                            LoansListArr.add(temp);
                        }


                        runOnUiThread(new Runnable() {
                            public void run() {
                                String[] LoanArray = LoansListArr.toArray(new String[0]);
                                Log.d("ViewApplication"," convert array to string: "+LoanArray);
                                ArrayAdapter adapter = new ArrayAdapter(LoanActuivity_ViewApplication.this, R.layout.loan_viewapplication_lable, LoanArray);
                                //lv_loans.setVisibility(View.VISIBLE);
                                lv_agents.setAdapter(adapter);
                            }
                        });
                        Log.d("TAG","SAME CLAdddSS"+LoansListArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e) {
                    }
                } else {
                    //Toast.makeText(activity_Aeps_BalanceEnquiry.this, "You are not getting any Response From Bank !! ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

        private void api_getAgent(){
            ArrayList<String> ManagerAgents = new ArrayList<String>();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("area_manager_id", managerId);
                //jsonObject.put("Status", status);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            okhttp3.Request request = new Request.Builder()
                    .url("http://mintserver.gramtarang.org:8080/mint/im/getagentidbyam")
                    .addHeader("Accept", "*/*")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
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
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            JSONArray llist1 = jsonResponse.getJSONArray("getagentidbyam");
                            for(int i = 0; i < llist1.length(); i++){
                                String temp = llist1.getJSONObject(i).getString("id");
                                ManagerAgents.add(temp);
                            }


                            runOnUiThread(new Runnable() {
                                              public void run() {
                                                  String[] AgentArray = ManagerAgents.toArray(new String[0]);
                                                  Log.d("ViewApplication"," convert array to string: "+AgentArray);
                                                  ArrayAdapter adapter = new ArrayAdapter(LoanActuivity_ViewApplication.this, R.layout.loan_viewapplication_lable, AgentArray);
                                                  lv_agents.setAdapter(adapter);
                                              }
                                          });
                            Log.d("TAG","SAME CLAdddSS"+ManagerAgents);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e) {
                        }
                    } else {
                        //Toast.makeText(activity_Aeps_BalanceEnquiry.this, "You are not getting any Response From Bank !! ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
}


