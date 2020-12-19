package gramtarang.mint.api;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class DBApi extends AppCompatActivity {
    String data_json,response_String,agentemail,appversion,dateofrelease;
    //String agentdetails=new String;
    List<String> al = new ArrayList<String>();
    OkHttpClient client;
    public String getappversion(){
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://mintserver.gramtarang.org:8080/mint/im/version")
                .addHeader("Accept", "*/*")
                .get()
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
                    JSONArray jsonResponse = null;
                    try {
                        jsonResponse = new JSONArray(response_String);
                        for(int i = 0; i < jsonResponse.length(); i++){
                            JSONObject jresponse = jsonResponse.getJSONObject(i);
                            appversion= jresponse.getString("version_number");
                            dateofrelease = jresponse.getString("date_of_release");
                        }
                        Log.d("TAG","Response:"+appversion+dateofrelease);
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
        return agentemail;
    }

    public ArrayList<String> getagentdetails(String username,String androidId){
        client = new OkHttpClient();
        ArrayList<String> agentDetailsArr = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", username);
            jsonObject.put("androidid",androidId);
            data_json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, data_json);
        Request request = new Request.Builder()
                .url("http://mintserver.gramtarang.org:8080/mint/im/getagentdetails")
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
                        JSONArray llist1 = jsonResponse.getJSONArray("llist1");
                        agentemail=llist1.getJSONObject(0).getString("email");
                        agentDetailsArr.add(agentemail);
                        Log.d("TAG","Response Email:"+agentDetailsArr);

                        SharedPreferences settings = getSharedPreferences(
                                "pref", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("jsonString", response_String);
                        editor.commit();
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
    return agentDetailsArr;
    }
}

