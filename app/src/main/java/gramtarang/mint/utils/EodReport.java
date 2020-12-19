package gramtarang.mint.utils;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_Login;

public class EodReport<date1> extends AppCompatActivity {

    private TextView dateView_from,dateView_to;

    private RadioGroup radioGroup;
    private RadioButton radioSelect_today,radioSelect_month,radioSelect_custom,radioSelect;
    private int year, month, day,date;
    private Button btnChangeDate, btnChangeDate2;
    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;

    int cur = 0;

   boolean doubleBackToExitPressedOnce = false;
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
                Intent intent=new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eod_report);

        radioGroup = findViewById(R.id.radio_group_by);
        radioSelect_today = findViewById(R.id.radio_today);
        radioSelect_month = findViewById(R.id.radio_month);
        radioSelect_custom = findViewById(R.id.radio_custom);

        dateView_from = (TextView) findViewById(R.id.view_from_date);
        dateView_to = (TextView) findViewById(R.id.view_to_date);
        btnChangeDate =  findViewById(R.id.btn_from_date);
        btnChangeDate2 = (Button) findViewById(R.id.btn_to_date);






        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.radio_today) {
                    dateView_from.setVisibility(View.INVISIBLE);
                    dateView_to.setVisibility(View.INVISIBLE);
                    btnChangeDate.setVisibility(View.INVISIBLE);
                    btnChangeDate2.setVisibility(View.INVISIBLE);

                    Toast.makeText(getApplicationContext(),"today" ,
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_month) {
                    dateView_from.setVisibility(View.INVISIBLE);
                    dateView_to.setVisibility(View.INVISIBLE);
                    btnChangeDate.setVisibility(View.INVISIBLE);
                    btnChangeDate2.setVisibility(View.INVISIBLE);

                    Toast.makeText(getApplicationContext(), "month",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_custom) {
                    dateView_from.setVisibility(View.VISIBLE);
                    dateView_to.setVisibility(View.VISIBLE);
                    btnChangeDate.setVisibility(View.VISIBLE);
                    btnChangeDate2.setVisibility(View.VISIBLE);
                    setCurrentDateOnView();
                    addListenerOnButton();
                    Toast.makeText(getApplicationContext(), "custom",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    // display current date
    public void setCurrentDateOnView() {


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        // set current date into textview
        dateView_from.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day - 1).append("/").append(month + 1).append("/")
                .append(year).append(" "));

        dateView_to.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year).append(" "));
    }

    public void addListenerOnButton() {



        btnChangeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });


        btnChangeDate2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID2);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case DATE_DIALOG_ID:

                System.out.println("onCreateDialog  : " + id);
                cur = DATE_DIALOG_ID;
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
            case DATE_DIALOG_ID2:
                cur = DATE_DIALOG_ID2;
                System.out.println("onCreateDialog2  : " + id);
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {



        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {



            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;


            if(cur == DATE_DIALOG_ID){
                final Calendar c = Calendar.getInstance();
                date = c.get(Calendar.DATE);
                Calendar today = (Calendar) c.clone();
                today.setTimeInMillis(date);
                view.setMaxDate(today.getTimeInMillis());

                // set selected date into textview
                dateView_from.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/")
                        .append(year).append(" "));
            }
            else{
                dateView_to.setText(new StringBuilder().append(day).append("/").append(month + 1).append("/")
                        .append(year).append(" "));
            }

        }
    };




}
