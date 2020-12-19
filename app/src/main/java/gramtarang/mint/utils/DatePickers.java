package gramtarang.mint.utils;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;

import gramtarang.mint.R;

public class DatePickers extends AppCompatActivity {


    private EditText edt_Date1, edt_Date2;
StringBuilder startDate,endDate;
    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 1;
    static final int DATE_DIALOG_ID2 = 2;
    int cur = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        setCurrentDateOnView();
        addListenerOnButton();

    }


    public void setCurrentDateOnView() {



        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);



    }

    public void addListenerOnButton() {

        edt_Date1 = (EditText) findViewById(R.id.ed_ChangeDate);

        edt_Date1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });
        edt_Date2 = (EditText) findViewById(R.id.ed_ChangeDate2);

        edt_Date2.setOnClickListener(new OnClickListener() {

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
                cur = DATE_DIALOG_ID;
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
            case DATE_DIALOG_ID2:
                cur = DATE_DIALOG_ID2;
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);

        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;


            if(cur == DATE_DIALOG_ID){
                String monthString = String.valueOf(month+1);
                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }
                String dateString = String.valueOf(day);
                if (dateString.length() == 1) {
                    dateString = "0" + dateString;
                }
                startDate=new StringBuilder().append(year)
                        .append("-").append(monthString).append("-").append(dateString)
                        .append(" ");
                edt_Date1.setText(new StringBuilder().append(year)
                        .append("-").append(monthString).append("-").append(dateString)
                        .append(" "));

                System.out.println("START DATE IS"+startDate);

            }
            else{
                String dateString = String.valueOf(day);
                if (dateString.length() == 1) {
                    dateString = "0" + dateString;
                }
                String monthString = String.valueOf(month+1);
                if (monthString.length() == 1) {
                    monthString = "0" + monthString;
                }
                endDate=new StringBuilder().append(year)
                        .append("-").append(monthString).append("-").append(dateString)
                        .append(" ");
                edt_Date2.setText(new StringBuilder().append(year)
                        .append("-").append(monthString).append("-").append(dateString)
                        .append(" "));
                System.out.println("END DATE IS"+endDate);

            }

        }
    };

}