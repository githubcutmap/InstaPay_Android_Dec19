package gramtarang.instamoney.aeps;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gramtarang.instamoney.R;
import gramtarang.instamoney.adapters.Adapter_Ministatement;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.LogOutTimer;

public class activity_Aeps_Ministatement_Report extends AppCompatActivity implements LogOutTimer.LogOutListener {
   //LOGOUT TIMER
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
         //Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

         //Log.e(TAG, "onResume()");
    }

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }

    private static final String TAG ="MainActivity" ;
    String timeStamp,avlBalance,adhaar,custName,bankName ;
    List<HashMap<String,String>> myList = new ArrayList<>();
    ListView listView ;
    TextView tv_aadhaar,tv_timestamp,tv_availablebalance,tv_custname,tv_bankname;
    Button btn_back;
    String[] date;
    String[] type;
    String[] amount;
    boolean doubleBackToExitPressedOnce = false;

    //BACK PRESSED HANDLING
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
                Intent intent=new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //LAYOUT ELEMENTS DECLARATION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ministatement_report);
        listView = findViewById(R.id.listview);
        tv_aadhaar=findViewById(R.id.text_aadhaar);
        tv_availablebalance=findViewById(R.id.text_balance);
        tv_timestamp=findViewById(R.id.text_timestamp);
        tv_custname = findViewById(R.id.text_custname);
        tv_bankname = findViewById(R.id.text_bankname);
        btn_back = findViewById(R.id.back);

        //BACK BUTTON HANDLING
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_Aeps_Ministatement_Report.this,activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });

        //INTENT DATA
        timeStamp = getIntent().getStringExtra("timeStamp");
        avlBalance = getIntent().getStringExtra("avlBalance");
        adhaar = getIntent().getStringExtra("Adaar");
        custName = getIntent().getStringExtra("custname");
        bankName = getIntent().getStringExtra("bankname");

//List data getting from previous activity
        myList =(List<HashMap<String,String>>) getIntent().getSerializableExtra("data");

        try {
            tv_aadhaar.setText("XXXX XXXX "+adhaar.charAt(8)+adhaar.charAt(9)+adhaar.charAt(10)+adhaar.charAt(11));
            tv_timestamp.setText(timeStamp);
            tv_custname.setText(custName);
            tv_bankname.setText(bankName);
            tv_availablebalance.setText(avlBalance);
            date = new String[myList.size()];
            type = new String[myList.size()];
            amount = new String[myList.size()];

            for (int i = 0; i<myList.size();i++){
                date[i] = myList.get(i).get("date");
                type[i] = myList.get(i).get("txnType");
                amount[i] = myList.get(i).get("amount");
            }
            Adapter_Ministatement adapter_ministatement = new Adapter_Ministatement(this,date,type,amount);
            listView.setAdapter(adapter_ministatement);
        }catch (NullPointerException e){
            listView.setAdapter(null);
            e.printStackTrace();
        }
    }


}
