package gramtarang.instamoney.aeps;
//IMPORTS
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.Dashboard;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.Utils;
public class activity_Aeps_HomeScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {

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
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }
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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }



    //DECLARATIONS
    private static final String TAG ="MenuScreen" ;
    String androidId,lastlogin_time,response_String, bankmitra_id,jsonString,agentname,title,message;
    TextView menu_timestamp,textMessage,agent_name;
    ImageView aadhaarPay,aepsBalance,aepsWithdraw,aepsDeposit,ministatement,eodreport,accOpen,loan,rrnStatus,billPayments,card,logout,backimg;
    private static final int REQUEST_CODE = 101;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    Utils utils = new Utils();
    boolean doubleBackToExitPressedOnce = false;

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Options in the menuOption this onOptionMenueItemSelected method enable to click the items are
    //present inside option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_dropdown_myacc:
                Intent intent = new Intent(getApplicationContext(), activity_AgentsProfile.class);
                startActivity(intent);
                return true;
            case R.id.action_dropdown_logout:
                Intent intent2 = new Intent(getApplicationContext(), activity_Login.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                return true;
            case R.id.action_dropdown_eodreport:
                title = "End Of Day Report";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                return true;
            case R.id.action_dropdown_aboutmint:
                title = "About MINT";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("HardwareIds")*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //LAYOUT ELEMENTS DECLARATION
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);
        menu_timestamp=findViewById(R.id.menu_timestamp);
        textMessage = findViewById(R.id.textMessage);
        agent_name = findViewById(R.id.agent_name);
        aepsBalance=findViewById(R.id.aeps_balenquiry);
        aepsWithdraw=findViewById(R.id.aeps_withdraw);
        aepsDeposit = findViewById(R.id.deposit);
        ministatement=findViewById(R.id.image_ministatement);
        aadhaarPay=findViewById(R.id.aadhaar_pay);
        rrnStatus = findViewById(R.id.rrnstatus);
        logout=findViewById(R.id.logout);
        backimg=findViewById(R.id.backimg);

        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId=preferences.getString("AndroidId","No name defined");
        bankmitra_id =preferences.getString("AgentId","No name defined");
        agentname=preferences.getString("AgentName","No name defined");
        lastlogin_time=preferences.getString("LastLogin","No name defined");


        menu_timestamp.setText(lastlogin_time);
        agent_name.setText(agentname);
        ministatement.setEnabled(true);
        aepsWithdraw.setEnabled(true);
        aepsBalance.setEnabled(true);
        aadhaarPay.setEnabled(true);

        //ON CLICK HANDLERS
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);

            }
        });

logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),activity_Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
});
        aepsBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "AEPS Balance Enquiry";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);*/
                //aepsBalance.setEnabled(false);
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_BalanceEnquiry.class);
                startActivity(intent);

            }
        });
        //this button onclick listener navigating to the user to  AEPS withdraw screen
        aepsWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "AEPS Withdraw";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                aepsWithdraw.setEnabled(false);*/
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_Withdraw.class);
                startActivity(intent);
            }
        });

//This button onclickListener directly navigating the user to the AEPS ministatement screen
        ministatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "Mini Statement";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                ministatement.setEnabled(false);*/

                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_Ministatement.class);
                startActivity(intent);
            }
        });
//This adaarPay button onClickLlistener directly navigate the user to AdhaarPay screen
        aadhaarPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "Aadhaar Pay";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
              /*  aadhaarPay.setEnabled(false);
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, AadhaarPay.class);
                startActivity(intent);*/
              /*  title = "Account Opening";
                message = "  This function is not available in your login please try after some time or contact your admin";
                dialog(title,message);*/
            }
        });

        //This eodreport Image button navigate the user directly to EOD report screen
       /* eodreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "End Of Day Report";
                message = "  This function is not available in your login please try after some time or contact your admin";
                dialog(title,message);
            }
        });*/

       aepsDeposit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               title = "AEPS Deposit";
               message = "  This function is not available in your login please try after some time or contact your admin";
               utils.dialog(activity_Aeps_HomeScreen.this,title,message);

           }
       });

        rrnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "RRN Status";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
            }
        });



        Utils gethour=new Utils();
        String hour = gethour.gethour();
        textMessage.setText(hour+"!");
    }
}