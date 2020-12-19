package gramtarang.mint.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.loans.LoanActivity_viewAppId;
import okhttp3.Callback;

public class Adapter_loansAgent extends RecyclerView.Adapter<Adapter_loansAgent.Viewholder>{
    private  ArrayList<String> agents = new ArrayList<String>();
    private Context context;


    public Adapter_loansAgent(ArrayList<String> agents, Context context) {
        this.agents = agents;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter_loansAgent.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_viewapplication_lable,parent,false);

        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_loansAgent.Viewholder holder, int position) {

        holder.tv_lable.setText(agents.get(position).toString());
        holder.tv_lable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LoanActivity_viewAppId.class);
                intent.putExtra("id",agents.get(position).toString());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return agents.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView tv_lable;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            tv_lable = itemView.findViewById(R.id.label);
        }

    }
}
