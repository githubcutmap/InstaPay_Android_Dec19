package gramtarang.mint.loans.areamgr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import gramtarang.mint.R;

public class SearchbyidFragment extends Fragment {

    EditText et_appId;
    Button bt_search;
    String appId;

    public SearchbyidFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_searchbyid, container, false);

        et_appId = v.findViewById(R.id.search_id);
        bt_search = v.findViewById(R.id.search_btn);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appId = et_appId.getText().toString();
                Log.d("appID",appId);
                Intent intent = new Intent(v.getContext(),loan_viewapp.class);
                intent.putExtra("applicationId",appId);
                startActivity(intent);
            }
        });

        return v;
    }
}