package gramtarang.mint.loans.areamgr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

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

public class CompletedFragment extends Fragment {

    ListView lv_agents;
    //String managerId="9999";
    String jsonString,response_String,username,password;
    OkHttpClient client;
    Spinner sp_agents;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    SharedPreferences preferences;
    public final String mypreference = "mypref";



    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_completed, container, false);
        lv_agents =(ListView) v.findViewById(R.id.id_lv_pen_agents);
        sp_agents = (Spinner)v.findViewById(R.id.id_sp_agents);
        client=new OkHttpClient();
        api_getAgentsList(v);

        return v;
    }

    private void api_getAgentsList(View v){

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");

        Log.d("comple","usr getagent completed"+username+password);

        Utils utils = new Utils();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);


        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String managerId=preferences.getString("AreaManagerId","Null");
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
                .url("https://aepsapi.gramtarang.org:8008/mint/loans/getagentidbyam")
                .addHeader("Accept", "*/*")
                .post(body)
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
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response_String);
                        JSONArray llist1 = jsonResponse.getJSONArray("getagentidbyam");
                        for(int i = 0; i < llist1.length(); i++){
                            String temp = llist1.getJSONObject(i).getString("id");
                            String temp1 = llist1.getJSONObject(i).getString("name");
                            ManagerAgents.add(temp1+"-"+ temp);
                        }
                        mHandler.post(new Runnable() {
                            public void run() {
                                setspinner(ManagerAgents,v);
                            }
                        });

                        Log.d("TAG","SAME CLAdddSS"+ManagerAgents);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e) {
                    }
                } else {
                    Snackbar.make(v, "You are not getting any Response From Bank !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void setspinner(ArrayList<String> managerAgents, View v) {
        String[] AgentArray = managerAgents.toArray(new String[0]);
        //String[] AgentArray1 = ManagerAgents1.toArray(new String[0]);
        Log.d("ViewApplication"," convert array to string: "+managerAgents);
        Log.d("ViewApplication"," convert array to string: "+AgentArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, AgentArray);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp_agents.setAdapter(adapter);

        sp_agents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAgent = managerAgents.get(i);
                String[] selectedAgentId = selectedAgent.split("-");
                Snackbar.make(view, "Agent ID "+ selectedAgentId[1], Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                api_getApplist(v,selectedAgentId[1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void api_getApplist(View v,String agentid){
        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");

        Utils utils = new Utils();
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        ArrayList<String> AppId = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bankmitra_id", agentid);
            jsonObject.put("armgrstatus", 1);
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        okhttp3.Request request = new Request.Builder()
                .url("https://aepsapi.gramtarang.org:8008/mint/loans/getloansbyagentid")
                .addHeader("Accept", "*/*")
                .post(body)
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
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response_String);
                        JSONArray llist1 = jsonResponse.getJSONArray("getloansbyagentid");
                        for(int i = 0; i < llist1.length(); i++){
                            String temp = llist1.getJSONObject(i).getString("uniqueid");
                            String temp1 = llist1.getJSONObject(i).getString("beneficiary_name");
                            AppId.add(temp1+"-"+ temp);
                        }
                        mHandler.post(new Runnable() {
                            public void run() {
                                setlist(AppId,v);
                            }
                        });

                        Log.d("TAG","SAME CLAdddSS"+AppId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar.make(v, "No data found!! ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    catch (NullPointerException e) {
                        Snackbar.make(v, "No data found !! ", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Snackbar.make(v, "You are not getting any Response From Bank !! ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }




    private void setlist(ArrayList<String> appId, View v) {
        String[] AgentArray = appId.toArray(new String[0]);
        Log.d("ViewApplication"," convert array to string: "+appId);
        Log.d("ViewApplication"," convert array to string: "+AgentArray);
        lv_agents.setAdapter(new ArrayAdapter<String>(v.getContext(), R.layout.loan_viewapplication_lable,AgentArray));
        lv_agents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAppId = appId.get(i);
                String[] selectedApplicationId = selectedAppId.split("-");
                Snackbar.make(view, "Application ID "+ selectedApplicationId[1], Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(v.getContext(),loan_viewapp.class);
                intent.putExtra("applicationId",selectedApplicationId[1]);
                startActivity(intent);
            }
        });
    }
}