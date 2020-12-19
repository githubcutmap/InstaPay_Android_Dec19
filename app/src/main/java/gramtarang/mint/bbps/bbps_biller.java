package gramtarang.mint.bbps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import gramtarang.mint.R;

public class bbps_biller extends AppCompatActivity {
    private ArrayList<String> arr = new ArrayList();
    private AutoCompleteTextView fromAmount,category;
    private TextView avlBalance;
    private EditText amount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bbps_biller);
        fromAmount = findViewById(R.id.actv_billamount);
        category = findViewById(R.id.actv_category);
        avlBalance = findViewById(R.id.tv_avaliableBal);
        amount = findViewById(R.id.amountbill);
        setupAutoCompleteView();

    }

    private void setupAutoCompleteView() {
        arr.add("hello");
        arr.add("dssd");
        arr.add("auto");
        arr.add("fdsds");

        Log.d("Main","arraylist: "+arr);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(bbps_biller.this,android.R.layout.select_dialog_item,arr);

fromAmount.setAdapter(adapter);
        //fromAmount.setAdapter(adapter);
        category.setAdapter(adapter);

        /*AutoCompleteTextView editTextFilledExposedDropdown =
                findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);*/

        fromAmount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Snackbar.make(view,i,Snackbar.LENGTH_LONG);
                //avlBalance.setText(i);
            }
        });
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Snackbar.make(view,i,Snackbar.LENGTH_LONG);
            }
        });
    }
}