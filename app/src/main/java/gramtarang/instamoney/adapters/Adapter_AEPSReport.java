package gramtarang.instamoney.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import gramtarang.instamoney.R;
import gramtarang.instamoney.aeps.AepsReport;

public class Adapter_AEPSReport extends RecyclerView.Adapter<Adapter_AEPSReport.AepsReportHolder> {

    private List<gramtarang.instamoney.aeps.AepsReport> AepsReportList;
    public Adapter_AEPSReport(List<gramtarang.instamoney.aeps.AepsReport> AepsReportList){
        this.AepsReportList = AepsReportList;
    }
    @Override
    public Adapter_AEPSReport.AepsReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_aeps__report_template,parent,false);
        return new AepsReportHolder(view);
    }
    @Override
    public void onBindViewHolder(Adapter_AEPSReport.AepsReportHolder holder, final int position) {
        final AepsReport aepsReport = AepsReportList.get(position);
        holder.orderId.setText(aepsReport.getOrderId());
        holder.timeStamp.setText(aepsReport.getTimeStamp());
        holder.agentId.setText(aepsReport.getAgentId());
        holder.status.setText(aepsReport.getStatus());
        holder.message.setText(aepsReport.getMessage());
        holder.customerName.setText(aepsReport.getCustomerName());
        holder.transType.setText(aepsReport.getTransType());
        holder.TransId.setText(aepsReport.getTransId());
        holder.commision.setText(aepsReport.getCommision());
        holder.amount.setText(aepsReport.getAmount());


    }
    @Override
    public int getItemCount() {
        return AepsReportList.size();
    }
    public class AepsReportHolder extends RecyclerView.ViewHolder {
        private TextView orderId;
        private TextView timeStamp;
        private TextView agentId;
        private TextView status;
        private TextView message;
        private TextView customerName;
        private TextView transType;
        private TextView TransId;
        private TextView commision;
        private TextView amount;
        public AepsReportHolder(View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.id_orderid);
            timeStamp = itemView.findViewById(R.id.id_timestamp);
            agentId = itemView.findViewById(R.id.id_agentid);
            status = itemView.findViewById(R.id.id_status);
            message = itemView.findViewById(R.id.id_message);
            customerName = itemView.findViewById(R.id.id_customer_name);
            transType = itemView.findViewById(R.id.id_trans_type);
            TransId = itemView.findViewById(R.id.id_trans_id);
            commision = itemView.findViewById(R.id.id_commission);
            amount = itemView.findViewById(R.id.id_amount);
        }
    }
}
