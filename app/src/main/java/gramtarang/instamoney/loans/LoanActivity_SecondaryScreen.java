package gramtarang.instamoney.loans;

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

import gramtarang.instamoney.R;
import gramtarang.instamoney.agent_login.activity_Login;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.Utils;


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
        Intent intent = new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }

    SharedPreferences preferences;
    private TextView tv_uniqueId, heading;
    private EditText apgvb, other;
    private RadioGroup ownProperty_group;
    private RadioButton ownProperty_yes, ownProperty_no, ownPropertyradioButton;
    private Spinner et_category, et_purpose, s_ro;
    private EditText et_loanamount, et_father_husband,
            et_months, et_years, et_education, et_adult, et_child, et_sustenance,
            et_income, et_expenses, et_other_expenses, et_net_surplus, et_existingapgvb_loan_details;
    private String loan_amount, loanpurpose, beneficiaryUniqueId, beneficiary_father_husband, ro,
            be_1, be_2, beneficiary_business_period, beneficiary_education, beneficiary_category, beneficiary_fm_adult, beneficiary_otherloan,
            beneficiary_fm_child, beneficiary_sustenance, beneficiary_income, beneficiary_expenses, beneficiary_other_expenses,
            beneficiary_net_surplus, ownPropertydetails, otherLoanProperty, TAG = "LoanSecondary", beneficiary_loandetails_with, beneficiary_loandetails_without;
    //Button next,prev;
    private ImageView prev, next;
    public static final String mypreference = "Loanpreferences";
    Utils util = new Utils();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__secondary_screen);
        init();

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        beneficiaryUniqueId = preferences.getString("BeneficiaryUniqueId2", "No name defined");

        tv_uniqueId.setText(beneficiaryUniqueId);
        //  et_loanamount = findViewById(R.id.loan_payment);


        ownProperty_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId != -1) {
                    Toast.makeText(LoanActivity_SecondaryScreen.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    ownPropertydetails = rb.getText().toString().trim();
                    Log.d(TAG, "loan" + ownPropertydetails);

                }

            }
        });


        //Textwatcher calling
        et_adult.addTextChangedListener(LoanSecondaryTextWatcher);
        et_child.addTextChangedListener(LoanSecondaryTextWatcher);
        et_months.addTextChangedListener(LoanSecondaryTextWatcher);
        et_years.addTextChangedListener(LoanSecondaryTextWatcher);
        et_education.addTextChangedListener(LoanSecondaryTextWatcher);
        et_sustenance.addTextChangedListener(LoanSecondaryTextWatcher);


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoanActivity_SecondaryScreen.this, LoanActivity_PrimaryScreen.class);
                startActivity(intent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loanpurpose = et_purpose.getSelectedItem().toString().trim();
                beneficiary_fm_adult = et_child.getText().toString().trim();
                beneficiary_fm_child = et_adult.getText().toString().trim();
                be_1 = et_months.getText().toString().trim();
                be_2 = et_years.getText().toString().trim();
                beneficiary_education = et_education.getText().toString().trim();
                beneficiary_business_period = be_2 + "Years" + be_1 + "Months";
                beneficiary_father_husband = et_father_husband.getText().toString().trim();
                beneficiary_category = et_category.getSelectedItem().toString().trim();
                beneficiary_loandetails_with = apgvb.getText().toString().trim();
                beneficiary_loandetails_without = other.getText().toString().trim();
                beneficiary_sustenance = et_sustenance.getText().toString().trim();
                ro = s_ro.getSelectedItem().toString().trim();


                if (
                        !ro.equals("Regional Office") && !loanpurpose.equals("Purpose of Loan") && beneficiary_father_husband.length() > 0 && !beneficiary_category.equals("Category") &&
                                beneficiary_fm_adult.length() > 0 &&
                                beneficiary_fm_child.length() > 0 &&
                                be_1.length() > 0 &&
                                be_2.length() > 0 &&
                                beneficiary_education.length() > 0 &&
                                beneficiary_sustenance.length() > 0


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
                    editor.putString("BeneficiarySustenance", beneficiary_sustenance);
                    editor.putString("BeneficiaryNetSurplus", beneficiary_net_surplus);
                    editor.putString("BeneficiaryBusinessPeriod", beneficiary_business_period);
                    editor.putString("BeneficiaryCategory", beneficiary_category);
                    editor.putString("OwnProperty", ownPropertydetails);
                    editor.putString("BeneficiaryLoanDetailsAPGVB", beneficiary_loandetails_with);
                    editor.putString("BeneficiaryLoanDetailsOthers", beneficiary_loandetails_without);
                    editor.putString("RegionalOffice", ro);
                    editor.commit();
                    Intent intent = new Intent(LoanActivity_SecondaryScreen.this, LoanActivity_DocumentsUpload.class);
                    startActivity(intent);
                }
                if (ro.equals("Regional Office")) {
                    Toast.makeText(LoanActivity_SecondaryScreen.this, "Select Regional Office", Toast.LENGTH_SHORT).show();
                }
                if (loanpurpose.equals("Category")) {
                    Toast.makeText(LoanActivity_SecondaryScreen.this, "Select Purpose", Toast.LENGTH_SHORT).show();
                }
                if (beneficiary_fm_child.length() == 0) {
                    et_child.setError("Enter No. of Children");
                }
                if (beneficiary_fm_adult.length() == 0) {
                    et_adult.setError("Enter No. of Adults");
                }
                if (beneficiary_sustenance.length() == 0) {
                    et_sustenance.setError("Enter Sustainance required per month");
                }
                if (beneficiary_father_husband.length() == 0) {
                    et_father_husband.setError("Enter Father / Husband Name");
                }
                if (beneficiary_category.equals("Category")) {
                    Toast.makeText(LoanActivity_SecondaryScreen.this, "Select Category", Toast.LENGTH_SHORT).show();
                }
                if (beneficiary_education.length() == 0) {
                    et_education.setError("Enter Educational Qualification");
                }
                if (be_1.length() != 0) {
                    if (Integer.parseInt(be_1) == 0 && Integer.parseInt(be_1) > 12) {
                        et_months.setError("Enter Valid Months");
                    }
                } else {
                    et_months.setError("Enter Months");
                }
                if (be_2.length() == 0) {
                    et_years.setError("Enter Years");
                }

            }
        });

    }

    private void init() {
        et_father_husband = findViewById(R.id.father_husband);
        apgvb = findViewById(R.id.with_apgvb);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        other = findViewById(R.id.other_bank);
        et_category = findViewById(R.id.category);
        tv_uniqueId = findViewById(R.id.uniqueId);
        et_purpose = findViewById(R.id.purpose);
        et_adult = findViewById(R.id.adult);
        et_child = findViewById(R.id.child);
        et_category = findViewById(R.id.category);
        et_months = findViewById(R.id.months);
        et_years = findViewById(R.id.years);
        et_education = findViewById(R.id.education_qualification);
        et_sustenance = findViewById(R.id.sustainance_pm);
        s_ro = findViewById(R.id.ro);
        ownProperty_group = (RadioGroup) findViewById(R.id.prop_group);
    }

    public TextWatcher LoanSecondaryTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // loan_amount = et_loanamount.getText().toString().trim();

            beneficiary_fm_adult = et_child.getText().toString().trim();
            beneficiary_fm_child = et_adult.getText().toString().trim();
            be_1 = et_months.getText().toString().trim();
            be_2 = et_years.getText().toString().trim();
            beneficiary_sustenance = et_sustenance.getText().toString().trim();
            beneficiary_education = et_education.getText().toString().trim();
           /* beneficiary_expenses= et_expenses.getText().toString().trim();
            beneficiary_other_expenses= et_other_expenses.getText().toString().trim();
            beneficiary_income= et_income.getText().toString().trim();

            beneficiary_net_surplus= et_net_surplus.getText().toString().trim();*/

            try {
                if (true) {
                    next.setEnabled(true);

                }
            } catch (NullPointerException e) {
                //Toast.makeText(LoanActivity_SecondaryScreen.this,"Please Fill ALl Fields",Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };
}

