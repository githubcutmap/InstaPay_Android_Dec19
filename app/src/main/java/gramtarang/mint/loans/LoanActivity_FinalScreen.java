package gramtarang.mint.loans;

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_BalanceEnq_Receipt;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.LogOutTimer;

public class LoanActivity_FinalScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG="LOAN_finalscreen";
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
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }
    SharedPreferences preferences,preferences2;
    public static final String mypreference2 = "mypref";
    public static final String mypreference = "Loanpreferences";
    Button print,back;
    TextView tv_scheme,tv_status,tv_timestamp,tv_agentName,bId, bloanAmount,bName, bBank, bLoanType,bIdDetails;
    String schemetype,beneficiaryUniqueId, agentName, beneficiaryName, beneficiaryBank, beneficiaryloanType, timestamp,beneficiaryLoanAmount,beneficiaryiddetails;
    boolean doubleBackToExitPressedOnce = false;
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
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        preferences2= getSharedPreferences(mypreference2, Context.MODE_PRIVATE);
        tv_scheme=findViewById(R.id.textView023);
        schemetype= preferences.getString("LoanScheme","No name defined");
        agentName=preferences2.getString("AgentName","Null");
        beneficiaryUniqueId = preferences.getString("BeneficiaryUniqueId2","Null");
        beneficiaryName = preferences.getString("BeneficiaryName","Null");
        beneficiaryBank = preferences.getString("BeneficiaryBank","Null");
        beneficiaryloanType = preferences.getString("LoanType","Null");
        beneficiaryLoanAmount= preferences.getString("LoanAmount","Null");
        beneficiaryiddetails= preferences.getString("BeneficiaryIdDetails","Null");
        print=findViewById(R.id.print);
        back=findViewById(R.id.back);

        tv_timestamp=findViewById(R.id.timestamp);
        bId=findViewById(R.id.applicationId);
        bBank=findViewById(R.id.bank_location);
        bName=findViewById(R.id.customer_name);
        tv_agentName=findViewById(R.id.agent_name);
        /*bLoanType=findViewById(R.id.loantype);*/
        tv_status=findViewById(R.id.status);
        bloanAmount=findViewById(R.id.loanamount);
        tv_scheme.setText(schemetype);
        bId.setText(beneficiaryUniqueId);
        bName.setText(beneficiaryName);
        tv_status.setText("Pending");
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        tv_timestamp.setText(timestamp);
        /*
        bLoanType.setText(beneficiaryloanType);*/
        bBank.setText(beneficiaryBank);
        tv_agentName.setText(agentName);
        bloanAmount.setText(beneficiaryLoanAmount);
       // bIdDetails.setText(beneficiaryiddetails);
      /*  tv_timestamp.setText("timestamp");
        bLoanType.setText("beneficiaryloanType");
        bBank.setText("beneficiaryBank");

        tv_agentName.setText("agentName");*/
       /* bAadhaar=findViewById(R.id.aadhaar_number);

        tv_add1=findViewById(R.id.address1);
        tv_add2=findViewById(R.id.address2);
        tv_add3=findViewById(R.id.address3);
        bOccupation=findViewById(R.id.occupation);
        bRepaymentPeriod=findViewById(R.id.repaymentperiod);

  bLoanAmount.setText("beneficiaryLoanAmount");
        bphone.setText(beneficiaryPhone);
        bAadhaar.setText(beneficiaryAadhaar);

        bGender.setText(beneficiaryGender);

        tv_add1.setText(beneficiaryAddressLane);
        tv_add2.setText(beneficiaryAddressLane2);
        tv_pincode.setText(beneficiaryPincode);

        bOccupation.setText(beneficiaryOccupation);
        bLoanPurpose.setText(beneficiaryLoanPurpose);
        bTenure.setText(beneficiaryTenure);
        bRepaymentPeriod.setText(beneficiaryRepaymentPeriod);*/



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoanActivity_FinalScreen.this, LoanActivity_MainScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }
}