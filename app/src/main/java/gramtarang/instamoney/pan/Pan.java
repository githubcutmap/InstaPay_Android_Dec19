package gramtarang.instamoney.pan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pan extends AppCompatActivity {
    public static String encodeURL,jsonString,username,agentPassword,response_String;
    OkHttpClient httpClient;
    Utils utils = new Utils();
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//LAYOUT DECLARATIONS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pan_card);
        webView = findViewById(R.id.activity_main_webview);



        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username = preferences.getString("Username", "No name defined");
        agentPassword = preferences.getString("Password", "No name defined");


        String keyFull= "6e27eaa73f1c22aae7b2346621eecdd01b1169fc5e0977b3377c4773dd78a72d";
        String key = "6e27eaa73f1c22aa";
        String CnameRecordURL = "http://gtidspan.gramtarang.org";

        // Order of Keys is important. Do not change it.
        Map<String, String> map = new TreeMap<String, String>();
        map.put("ALLOWED_SERVICES", "PAN");
        map.put("APP_ID", "342");
        map.put("BLOCKED_SERVICES", "");
        map.put("LANDING_SERVICE", ""); // optional, service to show first after login
        map.put("PAN", "AUXPK5128N");

        String checkSumString  = "";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if(checkSumString.isEmpty() == true){
                checkSumString += entry.getKey()+ ":" + entry.getValue();
            }else{
                checkSumString += "|" + entry.getKey() + ":" + entry.getValue();
            }
        }
        String plainText = checkSumString;

        String IV = "1234567812345678";

        // Encrypting the parameters
        byte[] cipherText = null;
        try {
            cipherText = encrypt(plainText.getBytes(),key, IV.getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Generating HMAC
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SecretKeySpec secret_key = null;
        try {
            secret_key = new SecretKeySpec(keyFull.getBytes("UTF-8"), "HmacSHA256");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            sha256_HMAC.init(secret_key);
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Combine IV, HMAC and Ciphertext
        byte[] combined = combine(IV, sha256_HMAC, cipherText);

        // Put CHECKSUMHASH in map
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            map.put("CHECKSUMHASH", Base64.getEncoder().encodeToString(combined));
        }


        // Convert Map to JSON
        String jsonMap = buildJson(map);

        // URL Encode the JSON
        encodeURL = null;
        try {
            encodeURL = URLEncoder.encode( jsonMap, "UTF-8" );
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        api();

    }

    private void api(){
        JSONObject jsonObject = new JSONObject();
        //  Log.d("TAG","EN_FLAG"+en_flag);
        try {
            httpClient = utils.createAuthenticatedClient(username,agentPassword);
            jsonObject.put("params",encodeURL);
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .url("http://gtidspan.gramtarang.org")
                .addHeader("Accept", "*/*")
                // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                .post(body)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //JSON PARSING
                assert response.body() != null;
                response_String = response.body().string();

                // set the html content on a TextView
                Pan.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadDataWithBaseURL(null, response_String, "text/html", "utf-8", null);

                    }
                });



                Log.d("TAG","pan card "+response_String);
                /*if (response_String != null) {
                    JSONObject jsonResponse = null;
                    JSONObject jsonResponse1=null;
                    try {
                        jsonResponse = new JSONObject(response_String);




                    } catch (JSONException e) {
                        Log.d("TAG", "Exception Caught "+e );

                    }


                }*/

            }

        });

    }




    private static byte[] encrypt (byte[] plaintext,String key,byte[] IV ) throws Exception {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV);

        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        return cipherText;
    }

    private static byte[] combine(String IV, Mac sha256_HMAC, byte[] cipherText) {
        byte[] c1 = IV.getBytes();
        byte[] c2 = sha256_HMAC.doFinal(cipherText);
        byte[] c3 = cipherText;
        byte[] combined = new byte[c1.length + c2.length + c3.length];

        int comLength = 0;
        for (int i = 0; i < c1.length; ++i)
        {
            combined[comLength] = c1[i];
            comLength++;
        }

        for (int i = 0; i < c2.length; ++i)
        {
            combined[comLength] = c2[i];
            comLength++;
        }

        for (int i = 0; i < c3.length; ++i)
        {
            combined[comLength] = c3[i];
            comLength++;
        }

        return combined;
    }

    private static String buildJson(Map<String, String> map) {
        String jsonMap = "{";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonMap += ",";
            jsonMap += "\""+entry.getKey()+"\":\""+entry.getValue()+"\"";
        }
        jsonMap += "}";
        jsonMap = jsonMap.substring(0, 1) + jsonMap.substring(2);

        return jsonMap;
    }



    /*private static File getHtmlFile(String transactionURL, String encodeURL) {
        StringBuilder outputHtml = new StringBuilder();

        outputHtml.append("<!DOCTYPE html>");
        outputHtml.append("<html>");
        outputHtml.append("<head>");
        outputHtml.append("<title>Merchant Checkout Page</title>");
        outputHtml.append("</head>");
        outputHtml.append("<body>");
        outputHtml.append("<center><h1>Please do not refresh this page...</h1></center>");

        outputHtml.append("<form method='post' action='"+transactionURL+"' name='f1'>");
        outputHtml.append("<input type='hidden' name='params' value='"+encodeURL+"'>");

        outputHtml.append("</form>");
        outputHtml.append("<script type='text/javascript'>");
        outputHtml.append("document.f1.submit();");
        outputHtml.append("</script>");
        outputHtml.append("</body>");
        outputHtml.append("</html>");

        try {
            FileWriter fw=new FileWriter("redirect.html");
            fw.write(outputHtml.toString());
            fw.close();
        } catch(IOException e) {
            System.out.println("Cannot save html file");
        }

        File file = new File("redirect.html");

        return file;
    }*/

}
