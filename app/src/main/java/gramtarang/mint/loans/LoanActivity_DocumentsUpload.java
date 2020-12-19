package gramtarang.mint.loans;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoanActivity_DocumentsUpload extends AppCompatActivity {
    TextView tv_uniqueId,tv_loanamount,delete;
    int flag=0;boolean canProceed;int imageflag=0;
    SharedPreferences preferences,preferences2;String extflag,extension,beneficiaryUniqueId,beneficiaryUniqueId2,loanAmount,idDetails,file_ext_type;
    public static final String mypreference = "Loanpreferences";
    public final String preference = "mypref";
    Button businessPhoto,idProof,propertyProof;
    Button upload,cancel;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;
    private long downloadId;
    TextView file_name;double filesize;
    Spinner sp_idDetails;
    String file_path=null,upload_selection,appId,aadhaar,username,password;
    ImageView photoSuccess,idSuccess,propertySuccess;
    boolean isPhotoUploaded,isIdUploaded,isPropertyUploaded;
    boolean doubleBackToExitPressedOnce = false;

    OkHttpClient httpClient;
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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }
    Utils utils = new Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__documents_upload);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        preferences2= getSharedPreferences(preference, Context.MODE_PRIVATE);
        beneficiaryUniqueId=preferences.getString("BeneficiaryUniqueId","No name defined");
        beneficiaryUniqueId2=preferences.getString("BeneficiaryUniqueId2","No name defined");
        loanAmount=preferences.getString("LoanAmount","No name defined");
        aadhaar=preferences.getString("BeneficiaryAadhaar","No name defined");
        appId=preferences.getString("BeneficiaryUniqueId","NO name defined");
        username=preferences2.getString("Username","NO name defined");
        password=preferences2.getString("Password","NO name defined");
        Log.d("LOAN","USRPAS"+username+password);
        businessPhoto =findViewById(R.id.customer_img);

        tv_uniqueId=findViewById(R.id.uniqueId);
        tv_loanamount=findViewById(R.id.loan_payment);
        tv_uniqueId.setText(beneficiaryUniqueId2);
        tv_loanamount.setText(loanAmount);
        sp_idDetails=findViewById(R.id.iddetails);
        idProof=findViewById(R.id.customer_idproof);



        photoSuccess = findViewById(R.id.customer_success);
        propertyProof=findViewById(R.id.property_idproof);
        propertySuccess=findViewById(R.id.propertyproof_success);
        idSuccess = findViewById(R.id.customer_idproof_success);


        upload=findViewById(R.id.upload);
        cancel=findViewById(R.id.cancel);




        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isIdUploaded&&isPhotoUploaded && isPropertyUploaded){
                    idDetails=sp_idDetails.getSelectedItem().toString().trim();
                    SharedPreferences.Editor editor =preferences.edit();
                    editor.putString("BeneficiaryIdDetails",idDetails);


                    editor.commit();
                    Intent intent=new Intent(LoanActivity_DocumentsUpload.this,LoanActivity_ReviewScreen.class);
                    startActivity(intent);}
                else{
                    DialogActivity.DialogCaller.showDialog(LoanActivity_DocumentsUpload.this,"Alert","Please upload all the required documents.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoanActivity_DocumentsUpload.this, activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });
        businessPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check permission greater than equal to marshmeellow we used run time permission
                upload_selection="Business_Photo";
                imageflag=1;
                if(Build.VERSION.SDK_INT>=23){
                    if(checkPermission()){

                        filePicker();
                    }
                    else{
                        requestPermission();
                    }
                }
                else{
                    filePicker();
                }


            }
        });
        idProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload_selection="ID_Proof";
                imageflag=2;
                if(Build.VERSION.SDK_INT>=23){
                    if(checkPermission()){
                        filePicker();
                    }
                    else{
                        requestPermission();
                    }
                }
                else{
                    filePicker();
                }


            }
        });

        propertyProof.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //check permission greater than equal to marshmeellow we used run time permission
                upload_selection="Property_Proof";
                imageflag=3;
                if(Build.VERSION.SDK_INT>=23){
                    if(checkPermission()){

                        filePicker();
                    }
                    else{
                        requestPermission();
                    }
                }
                else{
                    filePicker();
                }


            }
        });
    }




    public void UploadAddressProof() {
        UploadTask uploadTask=new UploadTask();
        uploadTask.execute(new String[]{file_path});
    }

    private void filePicker(){

        //.Now Permission Working
        //  Toast.makeText(LoanActivity_DocumentsUpload.this, "File Picker Call", Toast.LENGTH_SHORT).show();
        //Let's Pick File

        Intent opengallery=new Intent(Intent.ACTION_PICK);
        opengallery.setType("image/*");
        startActivityForResult(opengallery,REQUEST_GALLERY);
    }


    //now checking if download complete

   /* private BroadcastReceiver onDownloadComplete=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(downloadId==id){
                Toast.makeText(LoanActivity_DocumentsUpload.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };*/

    /* @Override
     protected void onDestroy() {
         super.onDestroy();
         unregisterReceiver(onDownloadComplete);
     }
 */
    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(LoanActivity_DocumentsUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(LoanActivity_DocumentsUpload.this, "Please Give Permission to Upload File", Toast.LENGTH_SHORT).show();
        }
        else{
            ActivityCompat.requestPermissions(LoanActivity_DocumentsUpload.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission(){
        int result= ContextCompat.checkSelfPermission(LoanActivity_DocumentsUpload.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(LoanActivity_DocumentsUpload.this, "Permission Successfull,Click on Upload", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoanActivity_DocumentsUpload.this, "Permission Failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Result Code","Res Code is:"+resultCode);
        Log.d("Request Code","Req Code is:"+requestCode);
        if(requestCode==REQUEST_GALLERY && resultCode== Activity.RESULT_OK){
            String filePath=getRealPathFromUri(data.getData(),LoanActivity_DocumentsUpload.this);
            Log.d("File Path : "," "+filePath);

            //now we will upload the file
            //lets import okhttp first

            this.file_path=filePath;
            UploadAddressProof();
            /*File file=new File(filePath);
            delete.setText(file.getName());*/

        }
    }

    public String getRealPathFromUri(Uri uri,Activity activity){
        String[] proj = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor=activity.getContentResolver().query(uri,proj,null,null,null);
        if(cursor==null){
            return uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int id=cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

            return cursor.getString(id);

        }
    }


    public class UploadTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(filesize<=1000 ) {

                if(extension.equals("jpg")) {
                    if(upload_selection.equals("Business_Photo")){

                        businessPhoto.setEnabled(false);
                        businessPhoto.setText("Uploaded");
                        photoSuccess.setVisibility(View.VISIBLE);
                        isPhotoUploaded=true;
                    }
                    if(upload_selection.equals("ID_Proof")){
                        idProof.setEnabled(false);
                        idProof.setText("Uploaded");
                        idSuccess.setVisibility(View.VISIBLE);
                        isIdUploaded=true;
                    }
                    if(upload_selection.equals("Property_Proof")){
                        propertyProof.setEnabled(false);
                        propertyProof.setText("Uploaded");
                        propertySuccess.setVisibility(View.VISIBLE);
                        isPropertyUploaded=true;
                    }
                    Toast.makeText(LoanActivity_DocumentsUpload.this, "File uploaded", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                DialogActivity.DialogCaller.showDialog(LoanActivity_DocumentsUpload.this,"Alert","File size should be lesser than 1MB.",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            if(imageflag==1)
            {
                if(uploadFile(strings[0])){

                    return "true";
                }
                else{
                    return "failed";
                }
            }
            else if(imageflag==2)
            {
                if(uploadFile2(strings[0])){

                    return "true";
                }
                else{
                    return "failed";
                }
            }
            else
            {
                if(uploadFile3(strings[0])){
                    return "true";
                }
                else{
                    return "failed";
                }
            }

        }
        public void run(){

        }
        private boolean uploadFile(String path) {
            Log.d("Path is", "Path" + path);
            File file = new File(path);
            double bytes = file.length();
            filesize = (bytes / 1024);

            extflag = path.substring(path.lastIndexOf("."));
            extension = extflag.replace(".", "");
            Log.d("TAG", "FIle Extension is:" + extension);
            Log.d("LOAN","ONE"+username+password);
            httpClient = utils.createAuthenticatedClient(username,password);

            try {
                if(extension.equals("jpg")){

                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))

                            .build();
                    Request request = new Request.Builder()
                            .url("http://bankmgr.gramtarang.org:8081/mint/doc/upload2")
                           /* .addHeader("aadhaarnumber","309979918328")
                            .addHeader("applicationId","APGVB/1169/870825".replace("/","_"))*/
                            .addHeader("aadhaarnumber", aadhaar)
                            .addHeader("applicationId",appId.replace("/", "_") )
                            .addHeader("ext", extension.trim())
                            .post(requestBody)
                            .build();
                    Log.d("TAG", "Request is:" + request);
                    OkHttpClient client = new OkHttpClient();

                    run();



//
                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("Doc: ","fail");
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("Doc: ","response: "+ response);

                        }
                    });
                    LoanActivity_DocumentsUpload.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });



                }else{
                    LoanActivity_DocumentsUpload.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(LoanActivity_DocumentsUpload.this,"Alert","Only JPG files are accepted.",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }



        }
        private boolean uploadFile2(String path) {
            Log.d("Path is","Path"+path);
            File file = new File(path);
            double bytes = file.length();
            filesize = (bytes / 1024);
            extflag = path.substring(path.lastIndexOf("."));
            extension = extflag.replace(".", "");
            Log.d("LOAN","TWO"+username+password);
            httpClient = utils.createAuthenticatedClient(username,password);
            try {
                if(extension.equals("jpg")){
                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://bankmgr.gramtarang.org:8081/mint/doc/upload1")
                           /* .addHeader("aadhaarnumber","309979918328")
                            .addHeader("applicationId","APGVB/1169/870825".replace("/","_"))*/
                            .addHeader("aadhaarnumber", aadhaar)
                            .addHeader("applicationId",appId.replace("/", "_") )
                            .addHeader("ext",extension.trim())
                            .post(requestBody)
                            .build();

                    /*SharedPreferences pref = getSharedPreferences(preference, Context.MODE_PRIVATE);
                    String username=pref.getString("Username","No name defined");
                    String password=pref.getString("Password","No name defined");*/



                    //OkHttpClient client = new OkHttpClient();
                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("TAG","response: "+ response);

                        }
                    });}else{
                    LoanActivity_DocumentsUpload.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(LoanActivity_DocumentsUpload.this,"Alert","Only JPG files are accepted.",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }


        }
        private boolean uploadFile3(String path) {
            Log.d("Path is","Path"+path);
            File file = new File(path);
            double bytes = file.length();
            filesize = (bytes / 1024);
            extflag = path.substring(path.lastIndexOf("."));
            extension = extflag.replace(".", "");
            Log.d("LOAN","THREE"+username+password);
            httpClient = utils.createAuthenticatedClient(username,password);
            try {
                if(extension.equals("jpg")){
                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://bankmgr.gramtarang.org:8081/mint/doc/upload3")
                            .addHeader("aadhaarnumber",aadhaar)
                            .addHeader("applicationId",appId.replace("/","_"))
                           /* .addHeader("aadhaarnumber","309979918328")
                            .addHeader("applicationId","APGVB/1169/870825".replace("/","_"))*/
                            .addHeader("ext",extension.trim())
                            .post(requestBody)
                            .build();

                   /* SharedPreferences pref = getSharedPreferences(preference, Context.MODE_PRIVATE);
                    String username=pref.getString("Username","No name defined");
                    String password=pref.getString("Password","No name defined");*/


                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d("Tag","response: "+ response);

                        }
                    });}else{
                    LoanActivity_DocumentsUpload.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(LoanActivity_DocumentsUpload.this,"Alert","Only JPG files are accepted.",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }


        }
    }



}