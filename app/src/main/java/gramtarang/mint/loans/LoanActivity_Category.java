package gramtarang.mint.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.Dashboard;

public class LoanActivity_Category extends AppCompatActivity {

    Spinner bank, scheme;
    String selected_bank,selected_scheme;
    Button next;
    ImageView back;
    public static final String mypreference = "Loanpreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_category);

        bank = findViewById(R.id.sp_bank);
        scheme = findViewById(R.id.sp_cat);
        next = findViewById(R.id.next);
        back = findViewById(R.id.backimg);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_bank=bank.getSelectedItem().toString().trim();
                selected_scheme= scheme.getSelectedItem().toString().trim();
                Log.d("SELECTION","BANK"+selected_bank);
                Log.d("SELECTION","SCHEME"+selected_scheme);
                if(selected_bank.equals("Select Bank")){
                    Toast.makeText(LoanActivity_Category.this,"Select Bank",Toast.LENGTH_SHORT).show();
                }
                if(selected_scheme.equals("Select Scheme")){
                    Toast.makeText(LoanActivity_Category.this,"Select Scheme",Toast.LENGTH_SHORT).show();
                }
                if(!selected_bank.equals("Select Bank") && !selected_scheme.equals("Select Scheme")){
                  //  Toast.makeText(LoanActivity_Category.this,"Success",Toast.LENGTH_SHORT).show();
                   SharedPreferences preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LoanBank", selected_bank);
                    editor.putString("LoanScheme", selected_scheme);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(),LoanActivity_PrimaryScreen.class);
                    startActivity(intent);
                }
              /*  if (selected_bank.matches("Select Bank")){
                    //
                }
                if (selected_scheme.matches("Select Scheme")){
                    //
                }
                else{
                    SharedPreferences preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LoanBank", selected_bank);
                    editor.putString("LoanScheme", selected_scheme);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(),LoanActivity_MainScreen.class);
                    startActivity(intent);
                }*/
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        });

    }
}