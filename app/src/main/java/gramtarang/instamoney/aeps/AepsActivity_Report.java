package gramtarang.instamoney.aeps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

import gramtarang.instamoney.R;
import gramtarang.instamoney.adapters.Adapter_AEPSReport;

public class AepsActivity_Report extends AppCompatActivity {
    private RecyclerView rv_aepsreport;
    private Adapter_AEPSReport rvAepsAdapter;
    private ArrayList<AepsReport> AepsReportList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aeps__report);
        prepareMovie();
        rv_aepsreport = findViewById(R.id.id_rv_reportList);
        rvAepsAdapter = new Adapter_AEPSReport(AepsReportList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_aepsreport.setLayoutManager(layoutManager);
        rv_aepsreport.setAdapter(rvAepsAdapter);
    }

    private void prepareMovie(){
        AepsReport aepsReport = new AepsReport("orderId", "timeStamp", "agentId", "status", "message", "customerName", "transType", "TransId",
                "commision", " am");
        AepsReportList.add(aepsReport);
        aepsReport = new  AepsReport("orderId2", "timeStamp2", "agentId2", "status2", "message2", "customerName2", "transType2",
                "TransId2",
                "commision2", " am2");
        AepsReportList.add(aepsReport);

        /*https://aepsapi.gramtarang.org:8008/mint/aeps/getAgentWithdrawalBody*/


    }
}