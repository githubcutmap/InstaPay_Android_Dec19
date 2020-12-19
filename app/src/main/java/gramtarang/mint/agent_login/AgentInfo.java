package gramtarang.mint.agent_login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import gramtarang.mint.R;


public class AgentInfo extends Fragment {

    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    TextView agent_id,agent_name,agent_aadhar,agent_phone,agent_mail,agent_aepsim;
    String agentAadhar,agentName,agentEmail,agentPhone,agentId,aepsim;



    public AgentInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_agent_info, container, false);

        agent_id = v.findViewById(R.id.agent_id);
        agent_name = v.findViewById(R.id.agent_name);
        agent_aadhar = v.findViewById(R.id.agent_aadhar);
        agent_phone = v.findViewById(R.id.agent_phone);
        agent_mail = v.findViewById(R.id.agent_mail);
        agent_aepsim=v.findViewById(R.id.aepsim);

        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        aepsim=preferences.getString("OutletId","No name defined");
        agentName=preferences.getString("AgentName","No name defined");
        agentEmail=preferences.getString("AgentEmail","No name defined");
        agentPhone=preferences.getString("AgentPhone","No name defined");
        agentId=preferences.getString("Username","No name defined");
        agentAadhar=preferences.getString("AgentAadhaar","No name defined");
try{
        agent_id.setText(agentId);
        agent_name.setText(agentName);
        agent_aadhar.setText(agentAadhar);
        agent_phone.setText(agentPhone);
        agent_mail.setText(agentEmail);
        agent_aepsim.setText(aepsim);}catch (Exception e){e.printStackTrace();}
        return v;
    }
}