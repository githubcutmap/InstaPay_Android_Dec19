package gramtarang.mint.loans;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.LogOutTimer;
import gramtarang.mint.utils.Utils;


public class LoanActivity_SecondaryScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
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

SharedPreferences preferences;
TextView tv_uniqueId,heading;
    EditText apgvb,other;
    RadioGroup ownProperty_group;
    RadioButton ownProperty_yes,ownProperty_no,ownPropertyradioButton;
    Spinner et_category,et_purpose,s_ro;
    EditText et_loanamount,et_father_husband,
        et_months,et_years,et_education,et_adult,et_child,et_sustenance,
        et_income,et_expenses,et_other_expenses,et_net_surplus, et_existingapgvb_loan_details;
    String loan_amount,loanpurpose,beneficiaryUniqueId,beneficiary_father_husband,ro,
        be_1,be_2,beneficiary_business_period,beneficiary_education,beneficiary_category,beneficiary_fm_adult,beneficiary_otherloan,
        beneficiary_fm_child,beneficiary_sustenance,beneficiary_income,beneficiary_expenses,beneficiary_other_expenses,
        beneficiary_net_surplus, ownPropertydetails,otherLoanProperty,TAG="LoanSecondary",beneficiary_loandetails_with,beneficiary_loandetails_without;
//Button next,prev;
ImageView prev,next;
public static final String mypreference = "Loanpreferences";
Utils util=new Utils();
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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

/*    public void onclickbuttonMethod(View v){
        int selectedId = ownProperty_group.getCheckedRadioButtonId();
        ownPropertyradioButton = (RadioButton) findViewById(selectedId);
        if(selectedId==-1){
            Toast.makeText(LoanActivity_SecondaryScreen.this,"Select Own House Option", Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(LoanActivity_SecondaryScreen.this,ownPropertyradioButton.getText(), Toast.LENGTH_SHORT).show();
        }

    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__secondary_screen);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        beneficiaryUniqueId = preferences.getString("BeneficiaryUniqueId2", "No name defined");

        //init
        et_father_husband=findViewById(R.id.father_husband);
        apgvb=findViewById(R.id.with_apgvb);
        next=findViewById(R.id.next);
        prev=findViewById(R.id.prev);
        other=findViewById(R.id.other_bank);
        et_category=findViewById(R.id.category);
        tv_uniqueId = findViewById(R.id.uniqueId);
        tv_uniqueId.setText(beneficiaryUniqueId);
      //  et_loanamount = findViewById(R.id.loan_payment);
        et_purpose = findViewById(R.id.purpose);
        et_adult=findViewById(R.id.adult);
        et_child=findViewById(R.id.child);
        et_category=findViewById(R.id.category);
        et_months=findViewById(R.id.months);
        et_years=findViewById(R.id.years);
        et_education=findViewById(R.id.education_qualification);
        et_sustenance = findViewById(R.id.sustainance_pm);
        s_ro = findViewById(R.id.ro);
        /*ownProperty_yes = findViewById(R.id.prop_yes);
        ownProperty_no = findViewById(R.id.prop_no);*/
      //  et_existingapgvb_loan_details = findViewById(R.id.with_apgvb);
        /*et_expenses=findViewById(R.id.expenses_family);
        et_other_expenses=findViewById(R.id.other_exp);
        et_income=findViewById(R.id.monthly_income);
        et_net_surplus=findViewById(R.id.net_surplus);
        cb_ownProperty = findViewById(R.id.own_property);*/


/*
if(apgvb.isChecked()){
    beneficiary_otherloan="APGVB";
    et_other_loan_details.setVisibility(View.VISIBLE);
    Log.d(TAG,"loandetails: apgvb");

}
else if(other.isChecked()){
    beneficiary_otherloan="Other Banks";
    et_other_loan_details.setVisibility(View.VISIBLE);
    Log.d(TAG,"loandetails: others");
}
else if(apgvb.isChecked() && other.isChecked()){
    beneficiary_otherloan="APGVB and Other Banks";
    et_other_loan_details.setVisibility(View.VISIBLE);
    Log.d(TAG,"loandetails: others");
}
else{
    beneficiary_otherloan="None";
}*/

        ownProperty_group = (RadioGroup) findViewById(R.id.prop_group);


        ownProperty_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId != -1) {
                    Toast.makeText(LoanActivity_SecondaryScreen.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    ownPropertydetails = rb.getText().toString().trim();
                    Log.d(TAG,"loan" +ownPropertydetails);

                }

            }
        });






        //Textwatcher calling

        //et_loanamount.addTextChangedListener(LoanSecondaryTextWatcher);
        //et_purpose.addTextChangedListener(LoanSecondaryTextWatcher);
        et_adult.addTextChangedListener(LoanSecondaryTextWatcher);
        et_child.addTextChangedListener(LoanSecondaryTextWatcher);
        et_months.addTextChangedListener(LoanSecondaryTextWatcher);
        et_years.addTextChangedListener(LoanSecondaryTextWatcher);
        et_education.addTextChangedListener(LoanSecondaryTextWatcher);
        et_sustenance.addTextChangedListener(LoanSecondaryTextWatcher);

       /* et_other_expenses.addTextChangedListener(LoanSecondaryTextWatcher);
        et_income.addTextChangedListener(LoanSecondaryTextWatcher);
        et_expenses.addTextChangedListener(LoanSecondaryTextWatcher);
        et_net_surplus.addTextChangedListener(LoanSecondaryTextWatcher);*/








    prev.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
   Intent intent=new Intent(LoanActivity_SecondaryScreen.this,LoanActivity_PrimaryScreen.class);
   startActivity(intent);
    }
    });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                        //loan_amount = et_loanamount.getText().toString().trim();
                        loanpurpose = et_purpose.getSelectedItem().toString().trim();
                  beneficiary_fm_adult=  et_child.getText().toString().trim();
                  beneficiary_fm_child= et_adult.getText().toString().trim();
                   be_1= et_months.getText().toString().trim();
                  be_2=et_years.getText().toString().trim();
                   beneficiary_education= et_education.getText().toString().trim();
                    beneficiary_business_period=be_2+"Years"+be_1+"Months";
                    beneficiary_father_husband=et_father_husband.getText().toString().trim();
                    beneficiary_category=et_category.getSelectedItem().toString().trim();
beneficiary_loandetails_with = apgvb.getText().toString().trim();
beneficiary_loandetails_without = other.getText().toString().trim();
beneficiary_sustenance = et_sustenance.getText().toString().trim();
ro = s_ro.getSelectedItem().toString().trim();



                    if (
                            !ro.equals("Regional Office") && !loanpurpose.equals("Purpose of Loan") &&beneficiary_father_husband.length()>0 && !beneficiary_category.equals("Category")&&
                    beneficiary_fm_adult.length() > 0 &&
                            beneficiary_fm_child.length() > 0 &&
                            be_1.length() > 0 &&
                            be_2.length() > 0 &&
                            beneficiary_education.length() > 0 &&
                            beneficiary_sustenance.length() >0


                    ) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("LoanAmount", "10000");
                        editor.putString("OtherLoans", beneficiary_otherloan);
                        editor.putString("FatherHusbandName", beneficiary_father_husband);
                        editor.putString("LoanPurpose", loanpurpose);
                        editor.putString("BeneficiaryAdult", beneficiary_fm_adult);
                        editor.putString("BeneficiaryChild", beneficiary_fm_child);
                        editor.putString("BeneficiaryEducation", beneficiary_education);
                        editor.putString("BeneficiaryExpenses", beneficiary_expenses);
                        editor.putString("BeneficiaryOtherExpenses", beneficiary_other_expenses);
                        editor.putString("BeneficiaryIncome", beneficiary_income);
                        editor.putString("BeneficiarySustenance",beneficiary_sustenance);
                        editor.putString("BeneficiaryNetSurplus", beneficiary_net_surplus);
                        editor.putString("BeneficiaryBusinessPeriod",beneficiary_business_period);
                        editor.putString("BeneficiaryCategory",beneficiary_category);
                        editor.putString("OwnProperty", ownPropertydetails);
                        editor.putString("BeneficiaryLoanDetailsAPGVB", beneficiary_loandetails_with);
                        editor.putString("BeneficiaryLoanDetailsOthers", beneficiary_loandetails_without);
                        editor.putString("RegionalOffice",ro);
                        editor.commit();
                        Intent intent = new Intent(LoanActivity_SecondaryScreen.this, LoanActivity_DocumentsUpload .class);
                        startActivity(intent);
                    }

                    /*if (loan_amount.length()!=0) {
                        if(loan_amount.length()<=3){
                        et_loanamount.setError("Minimum Loan Amount is Rs 1000");}
                    }
                    else{
                        et_loanamount.setError("Enter Loan Amount");
                    }*/
                    if(ro.equals("Regional Office")){
                        Toast.makeText(LoanActivity_SecondaryScreen.this,"Select Regional Office",Toast.LENGTH_SHORT).show();
                    }
                    if(loanpurpose.equals("Category")){
                        Toast.makeText(LoanActivity_SecondaryScreen.this,"Select Purpose",Toast.LENGTH_SHORT).show();
                    }
                    if(beneficiary_fm_child.length()==0){
                        et_child.setError("Enter No. of Children");
                    }
                    if(beneficiary_fm_adult.length()==0){
                        et_adult.setError("Enter No. of Adults");
                    }
                    if(beneficiary_sustenance.length() ==0){
                        et_sustenance.setError("Enter Sustainance required per month");
                    }
                    if(beneficiary_father_husband.length()==0){
                        et_father_husband.setError("Enter Father / Husband Name");
                    }
                    if(beneficiary_category.equals("Category")){
                        Toast.makeText(LoanActivity_SecondaryScreen.this,"Select Category",Toast.LENGTH_SHORT).show();
                    }
                    if(beneficiary_education.length()==0){
                        et_education.setError("Enter Educational Qualification");
                    }
                    if(be_1.length()!=0){
                        if(Integer.parseInt(be_1)==0&&Integer.parseInt(be_1)>12){
                        et_months.setError("Enter Valid Months");}
                    }
                    else{
                        et_months.setError("Enter Months");
                    }
                    if(be_2.length()==0){
                        et_years.setError("Enter Years");
                    }

                }
            });

    }

    public TextWatcher LoanSecondaryTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           // loan_amount = et_loanamount.getText().toString().trim();

            beneficiary_fm_adult=  et_child.getText().toString().trim();
            beneficiary_fm_child= et_adult.getText().toString().trim();
            be_1= et_months.getText().toString().trim();
            be_2=et_years.getText().toString().trim();
            beneficiary_sustenance= et_sustenance.getText().toString().trim();
            beneficiary_education= et_education.getText().toString().trim();
           /* beneficiary_expenses= et_expenses.getText().toString().trim();
            beneficiary_other_expenses= et_other_expenses.getText().toString().trim();
            beneficiary_income= et_income.getText().toString().trim();

            beneficiary_net_surplus= et_net_surplus.getText().toString().trim();*/

try {
    if (true) {
        next.setEnabled(true);

    }
}catch (NullPointerException e){
    //Toast.makeText(LoanActivity_SecondaryScreen.this,"Please Fill ALl Fields",Toast.LENGTH_SHORT).show();
}


        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };
}

