package gramtarang.instamoney.agent_login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import gramtarang.instamoney.R;
import gramtarang.instamoney.aeps.activity_Aeps_HomeScreen;
import gramtarang.instamoney.loans.LoanActivity_MainScreen;
import gramtarang.instamoney.pan.Pan;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.LogOutTimer;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dashboard extends AppCompatActivity implements LogOutTimer.LogOutListener {
    //LOGOUT TIMER
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        //Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
         //Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

         //Log.e(TAG, "onResume()");
    }

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Dashboard.this, activity_WelcomeScreen.class);
        startActivity(intent);
    }

    //DECLARATIONS
    private final String TAG = "Dashboard";
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    String agentname, androidId, jsonString, response_String, lastlogin_time, username, password,panNo;
    int aeps, bbps, loan, pan, card;
    ImageView imaeps, imbbps, impan, imcard, imloan, improfile, logout;
    LinearLayout llaeps, llbbps, llpan, llcard, llloan;
    OkHttpClient client, httpClient;
    TextView tv_timestamp, tv_agentname, tv_textMessage;
    boolean doubleBackToExitPressedOnce = false;
    Utils utils = new Utils();


    //BACK PRESSED HANDLING
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
//LAYOUT DECLARATIONS
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        imaeps = findViewById(R.id.aeps);
        impan = findViewById(R.id.pan);
        imloan = findViewById(R.id.loan);
        imcard = findViewById(R.id.card);
        imbbps = findViewById(R.id.bbps);
        logout = findViewById(R.id.logout);
        improfile = findViewById(R.id.agentprofile);
        tv_timestamp = findViewById(R.id.menu_timestamp);
        tv_agentname = findViewById(R.id.agent_name);
        tv_textMessage = findViewById(R.id.textMessage);
        llaeps = findViewById(R.id.ll_aeps);
        llbbps = findViewById(R.id.ll_bbps);
        llpan = findViewById(R.id.ll_pan);
        llcard = findViewById(R.id.ll_card);
        llloan = findViewById(R.id.ll_loan);


        //SHARED PREFERENCES
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId = preferences.getString("AndroidId", "No name defined");
        agentname = preferences.getString("AgentName", "No name defined");
        username = preferences.getString("Username", "No name defined");
        password = preferences.getString("Password", "No name defined");
        aeps = preferences.getInt("aeps", 0);
        pan = preferences.getInt("pan", 0);
        bbps = preferences.getInt("bbps", 0);
        loan = preferences.getInt("loan", 0);
        card = preferences.getInt("card", 0);
        panNo =preferences.getString("panno","No name defined");
        Toast.makeText(Dashboard.this,panNo,Toast.LENGTH_SHORT).show();
        tv_agentname.setText(agentname);
        String hour = utils.gethour();
        tv_textMessage.setText(hour + "!");
        new apiCall_getlastlogin().execute();//GET LAST LOGIN API


        //BUTTON ONCLICK
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, activity_Login.class);
                startActivity(intent);
            }
        });


        imaeps.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (aeps == 1) {
                                              Intent intent = new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                                              startActivity(intent);
                                          } else {
                                              DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "This feature is not available for your login.", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                  }
                                              });
                                          }
                                      }
                                  }
        );
        impan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((pan == 1) && !panNo.isEmpty()) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        Intent intent = new Intent(Dashboard.this, Pan.class);
                        startActivity(intent);
                    }

                } else {
                    DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "This feature is not available for your login.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imbbps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bbps == 1) {
                    Intent intent = new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                    startActivity(intent);
                } else {
                    DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "This feature is not available for your login.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imloan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loan == 1) {
                    Intent intent = new Intent(Dashboard.this, LoanActivity_MainScreen.class);
                    startActivity(intent);
                } else {
                    DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "This feature is not available for your login.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card == 1) {
                    Intent intent = new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                    startActivity(intent);
                } else {
                    DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "This feature is not available for your login.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        improfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, activity_AgentProfile.class);
                startActivity(intent);
            }
        });
    }


    //GET LAST LOGIN TIME OF AGENT API
    class apiCall_getlastlogin extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            httpClient = utils.createAuthenticatedClient(username, password);
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("agentid", username);
                jsonObject.put("loginstatus", "Success");
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/im/getagentlastlogin")
                    .addHeader("Accept", "*/*")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Dashboard.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(Dashboard.this, "Alert", "Server Unreachable.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAffinity();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //JSON PARSING
                    assert response.body() != null;
                    response_String = response.body().string();
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            lastlogin_time = jsonResponse.getString("timestamp");
                            Dashboard.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_timestamp.setText(lastlogin_time.substring(8, 10) + "-" + lastlogin_time.substring(5, 7) + "-" + lastlogin_time.substring(0, 4) + " " + lastlogin_time.substring(11, 16));

                                }
                            });

                            //EDIT SHARE PREFERENCES
                            preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("LastLogin", lastlogin_time.substring(8, 10) + "-" + lastlogin_time.substring(5, 7) + "-" + lastlogin_time.substring(0, 4) + " " + lastlogin_time.substring(11, 16));
                            editor.commit();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }

            });


            return null;
        }

    }
}