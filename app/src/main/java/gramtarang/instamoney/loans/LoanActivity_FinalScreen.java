package gramtarang.instamoney.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.LogOutTimer;

public class LoanActivity_FinalScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG = "LOAN_finalscreen";
    SharedPreferences preferences, preferences2;
    public static final String mypreference2 = "mypref";
    public static final String mypreference = "Loanpreferences";
    private Button print, back;
    private TextView tv_scheme, tv_status, tv_timestamp, tv_agentName, bId, bloanAmount, bName, bBank, bLoanType, bIdDetails;
    private String schemetype, beneficiaryUniqueId, agentName, beneficiaryName, beneficiaryBank, beneficiaryloanType, timestamp, beneficiaryLoanAmount, beneficiaryiddetails;
    boolean doubleBackToExitPressedOnce = false;

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

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__final_screen);

        init();

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        preferences2 = getSharedPreferences(mypreference2, Context.MODE_PRIVATE);

        schemetype = preferences.getString("LoanScheme", "No name defined");
        agentName = preferences2.getString("AgentName", "Null");
        beneficiaryUniqueId = preferences.getString("BeneficiaryUniqueId2", "Null");
        beneficiaryName = preferences.getString("BeneficiaryName", "Null");
        beneficiaryBank = preferences.getString("BeneficiaryBank", "Null");
        beneficiaryloanType = preferences.getString("LoanType", "Null");
        beneficiaryLoanAmount = preferences.getString("LoanAmount", "Null");
        beneficiaryiddetails = preferences.getString("BeneficiaryIdDetails", "Null");

        tv_scheme.setText(schemetype);
        bId.setText(beneficiaryUniqueId);
        bName.setText(beneficiaryName);
        tv_status.setText("Pending");
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        tv_timestamp.setText(timestamp);
        bBank.setText(beneficiaryBank);
        tv_agentName.setText(agentName);
        bloanAmount.setText(beneficiaryLoanAmount);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoanActivity_FinalScreen.this, LoanActivity_MainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

    private void init() {
        tv_scheme = findViewById(R.id.textView023);
        print = findViewById(R.id.print);
        back = findViewById(R.id.back);
        tv_timestamp = findViewById(R.id.timestamp);
        bId = findViewById(R.id.applicationId);
        bBank = findViewById(R.id.bank_location);
        bName = findViewById(R.id.customer_name);
        tv_agentName = findViewById(R.id.agent_name);
        tv_status = findViewById(R.id.status);
        bloanAmount = findViewById(R.id.loanamount);
    }
}