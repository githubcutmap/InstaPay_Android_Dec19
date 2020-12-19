package gramtarang.mint.loans;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import gramtarang.mint.R;
import gramtarang.mint.adapters.Adapter_loansAgent;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.LogOutTimer;


public class LoanActivity_viewAppId extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private  String TAG="LOAN_appid";
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

    private RecyclerView rv_agentApplicationId;
    private LinearLayoutManager layoutManager;
    private Adapter_loansAgent adapter_loansAgent;
    private ArrayList<String> LoansListArr,LoansDetailsArr;
    private String agentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_view_app_id);
        agentId = getIntent().getStringExtra("id");
        rv_agentApplicationId = findViewById(R.id.id_rv_agentApplicationId);
        layoutManager = new LinearLayoutManager(this);
        rv_agentApplicationId.setLayoutManager(layoutManager);



        Log.d("viewApplicationAppid"," ids agent id: "+agentId);
        loansList(agentId);



    }
    private void loansList(String selectedAgentId) {
   /*     SQLQueries loanslist = new SQLQueries();
        LoansListArr = loanslist.getLoanList(selectedAgentId);*/
        Log.d("ViewApplication"," ids of agents under manager loanslist: "+LoansListArr);
        adapter_loansAgent = new Adapter_loansAgent(LoansListArr,this);
        rv_agentApplicationId.setAdapter(adapter_loansAgent);



    }

}