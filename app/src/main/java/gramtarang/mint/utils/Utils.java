package gramtarang.mint.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.api.JavaMailAPI;
import gramtarang.mint.loans.areamgr.MainActivity;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class Utils extends AppCompatActivity {
    ProgressDialog progressDialog;

    //This is your from email
    public static final String EMAIL = "noreply@gramtarang.org";
    //noreply@gramtarang.org
    //Aucc@312020@!
    //This is your from email password
    public static final String PASSWORD = "Aucc@312020@!";

    String selected_bank;
    String selected_bank_id;
    int selected_bank_index;
    boolean isValidAadhaar,isValidName,isValidAmount,isValidDOB,isValidPhone,isMorning,isAfternoon,isEvening,isValidEmail;
    String pidDataXML,agentid;
    List<String> al = new ArrayList<String>();
    String[] arr=new String[100];

    public static OkHttpClient createAuthenticatedClient(final String api_username,
                                                         final String api_password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(api_username, api_password);
                Log.d("CREDENTIALS","USR"+api_username+"PASS:"+api_password);
                Log.d("CREDENTIALS","PRINT"+credential);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }


    public void getprogressDialog(Context context, String title, String message){
        progressDialog=new ProgressDialog(context,R.style.Theme_RJProgressDialog);
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

    }
    public boolean isValidDOB(String dob){
        if(dob.matches("^(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d$")){
            isValidDOB=true;
        }
        else{
            isValidDOB=false;
        }
        return isValidDOB;
    }
    public void dismissProgressDialog(){
        progressDialog.dismiss();
    }
    public String getOTPString() {
        Random rand = new Random();
        String rand_int1= String.valueOf(rand.nextInt(900000) + 100000);
        return rand_int1;


    }
  public String gethour(){
      Date date = new Date();
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      String greeting;
      if(hour<12){
          greeting="Good Morning";
      }
      else if(hour<17){
          greeting="Good Afternoon";
      }
      else{
          greeting="Good Evening";
      }
      return greeting;
  }

    public void sendOTPMail(String generated_otp, String agemail, String username) {
        //greeting+","+" "+username+"\n"+"Your OTP for login is:"+otp+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team"

        String greeting = gethour();
        String mail = agemail;
        String message = greeting+","+" "+username+"\n"+"Your OTP for login is:"+generated_otp+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team";
        String subject = "OTP Authentication";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(this,mail,subject,message);
        javaMailAPI.execute();

    }

    public void dialog(Context context, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_RJDialog);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setNegativeButton("Ok", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.setIcon(R.drawable.erroricon);
        alertDialog.show();
    }
    public void loansdialog(Context context, String title, String message,int stat) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_RJDialog);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setCancelable(false);

        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                }
        });
        AlertDialog alertDialog = builder.create();
        if (stat==1){
            alertDialog.setIcon(R.drawable.success);
        }
        else{
            alertDialog.setIcon(R.drawable.erroricon);
        }
        alertDialog.show();
    }


    public boolean isValidAmount(String amount){
        if(amount.matches("[1-8][0-9]{2}|9[0-8][0-9]|99[0-9]|[1-8][0-9]{3}|9[0-8][0-9]{2}|99[0-8][0-9]|999[0-9]|10000")){
            isValidAmount=true;
        }
        else{
            isValidAmount=false;
        }
        return isValidAmount;
    }
    public boolean isValidName(String name){
        if(name.length()!=0){
            isValidName=true;
        }
        else{
            isValidName=false;
        }
        return isValidName;
    }
    public boolean isValidEmail(String email){
        if(email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
            isValidEmail=true;
        }
        else{
            isValidEmail=false;
        }
        return isValidEmail;
    }
    public boolean isValidPhone(String Phn){
        if (Phn.matches("^[6-9][0-9]{9}$")){
           isValidPhone=true;
        }
        else{
            isValidPhone=false;
        }
        return isValidPhone;
        
    }
    public boolean isValidAadhaar(String aadhaar) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        isValidAadhaar = aadharPattern.matcher(aadhaar).matches();
        if(isValidAadhaar ){
            isValidAadhaar  = VerhoeffAlgorithm.validateVerhoeff(aadhaar);
        }
        else{
            isValidAadhaar=false;
        }
        return isValidAadhaar;
    }
    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");

    }

    public static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists())

                return true;
        }
        return false;
    }

    public static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null){
                return true;}
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

       /* public String capture(String pidOptions) {
        try{
            Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
            intentCapture.putExtra("PID_OPTIONS", pidOptions);
            startActivityForResult(intentCapture, 1);
           }catch(Exception e){
            Toast.makeText(this, "Fingerprint device not connected.", Toast.LENGTH_SHORT).show();
        }
            return pidDataXML;
        }*/

public String generateUniqueIdLoan(){
    int leftLimit = 65;
    int rightLimit = 90;
    int targetStringLength = 4;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
        int randomLimitedInt = leftLimit + (int)
                (random.nextFloat() * (rightLimit - leftLimit + 1));
        buffer.append((char) randomLimitedInt);
    }
    String rand_int1= String.valueOf(random.nextInt(900000) + 100000);
    String generatedString = buffer.toString()+rand_int1;
    return generatedString;
}
int selectedBankIndex;

    public int AutoCompleteTV_ApgvbBranch(Context c, AutoCompleteTextView bank_autofill, ArrayList<String> apgvbbranch_arr,
                                       String TAG){

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (c,android.R.layout.select_dialog_item, apgvbbranch_arr);
        bank_autofill.setThreshold(1);
        bank_autofill.setAdapter(adapter);
        Log.d(TAG," "+apgvbbranch_arr);
        bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(c, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                //selected_bank = adapter.getItem(position).toString();
                //selectedBankIndex = (int) adapter.getItemId(position);
                selectedBankIndex = apgvbbranch_arr.indexOf(adapter.getItem(position));


            }
        });
        return selectedBankIndex;
    }

    public String AutoCompleteTV_BankId(Context c, AutoCompleteTextView bank_autofill, ArrayList<String> banks_arr,
                                         ArrayList<String> banksID_arr,String TAG){

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (c,android.R.layout.select_dialog_item, banks_arr);
        bank_autofill.setThreshold(1);
        bank_autofill.setAdapter(adapter);
        Log.d(TAG,"array after async: "+banks_arr);
        Log.d(TAG,"array after async id: "+banksID_arr);


        bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(c, adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
                selected_bank = adapter.getItem(position).toString();
                selected_bank_index = banks_arr.indexOf(adapter.getItem(position));
                selected_bank_id = banksID_arr.get(selected_bank_index);
                Log.d(TAG, "array Selected bank: " + selected_bank);
                Log.d(TAG, "array Selected bank index: " + selected_bank_index);
                Log.d(TAG, "array Selected bank ID: " + selected_bank_id);
                al.add(banksID_arr.get(selected_bank_index));
                al.add(selected_bank);
                arr=al.toArray(arr);
            }
        });
        return banksID_arr.get(selected_bank_index);
    }

    public String AutoCompleteTV_BankName(Context c, AutoCompleteTextView bank_autofill, ArrayList<String> banks_arr,
                                        ArrayList<String> banksID_arr,String TAG){

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (c,android.R.layout.select_dialog_item, banks_arr);
        bank_autofill.setThreshold(1);
        bank_autofill.setAdapter(adapter);
        Log.d(TAG,"array after async: "+banks_arr);
        Log.d(TAG,"array after async id: "+banksID_arr);


        bank_autofill.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Toast.makeText(c,
                        adapter.getItem(position).toString(),
                        Toast.LENGTH_SHORT).show();*/

                selected_bank = adapter.getItem(position).toString();
                selected_bank_index = banks_arr.indexOf(adapter.getItem(position));
                selected_bank_id = banksID_arr.get(selected_bank_index);
                Log.d(TAG, "array Selected bank: " + selected_bank);
                Log.d(TAG, "array Selected bank index: " + selected_bank_index);
                Log.d(TAG, "array Selected bank ID: " + selected_bank_id);
                selected_bank=banks_arr.get(selected_bank_index);

            }
        });

        return banks_arr.get(selected_bank_index);
    }
    public String getSelected_bank(){
        return selected_bank;

    }
    public void sendMail(){

    }
    }




