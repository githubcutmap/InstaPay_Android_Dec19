package gramtarang.instamoney.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.Dashboard;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.loans.areamgr.MainActivity;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.Utils;


public class LoanActivity_MainScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {

    private final String TAG = "LOAN_mainscreen";
    private Button applyLoan, viewRegistration;
    private String title, message;
    private ImageView backbtn;
    boolean doubleBackToExitPressedOnce = false;
    Utils utils = new Utils();
    SharedPreferences preferences, preferences2;
    public final String mypreference = "mypref";
    public final String loanpreference = "Loanpreferences";
    private TextView bankName;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__main);
        init();

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        int role = preferences.getInt("Role", 0);

        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        });

        applyLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // role = 0 => agentlogin
                // role = 1 => areamanager

                if (role == 0) {
                    Intent intent = new Intent(LoanActivity_MainScreen.this, LoanActivity_Category.class);
                    startActivity(intent);
                } else {
                    title = "Apply Loan Application";
                    message = "This function is not available in your login please try after some time or contact your admin";
                    utils.dialog(LoanActivity_MainScreen.this, title, message);
                }

            }
        });

        viewRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (role == 1) {
                    Intent intent = new Intent(LoanActivity_MainScreen.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoanActivity_MainScreen.this, LoanActivity_SearchViewApplication.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void init() {
        backbtn = findViewById(R.id.backimg);
        applyLoan = findViewById(R.id.applyloan);
        viewRegistration = findViewById(R.id.view_reg);
        bankName = findViewById(R.id.tv_bankname);
    }
}
