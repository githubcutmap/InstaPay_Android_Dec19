package gramtarang.instamoney.agent_login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gramtarang.instamoney.R;
import gramtarang.instamoney.utils.DialogActivity;
import gramtarang.instamoney.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletFragment extends Fragment {
    OkHttpClient httpClient;
    Utils utils;
    TextView tv_totComlastsevenDays, tv_totWDlastsevenDays, tv_totTransferlastsevenDays, tv_amountTransferTillDate, tv_amountTransferCurrentDate, tv_commissionAmountCurrentDate, tv_commissionAmountTillDate, tv_totalwd, tv_totalms, tv_walletamount, tv_totalbe, tv_totaltransAmountT, tv_totalWDtransCurrentDate;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    String totTransferlastsevenDays, totWDlastsevenDays, totComlastsevenDays, pastweekDate, amountTransferTillDate, amountTransferCurrentDate, commissionAmountCurrentDate, commissionAmountTillDate, currentDate, totalWDtransCurrentDate, totaltransAmountT, totalWithdrawCount, totalMinistatementCount, agentPhn, agentId, password, totalBECount, walletAmount;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_wallet_info, container, false);
        tv_totalwd = v.findViewById(R.id.id_totalwd);
        tv_totalms = v.findViewById(R.id.id_totalms);
        tv_totalbe = v.findViewById(R.id.id_totalbe);
        tv_walletamount = v.findViewById(R.id.tv_walletamount);
        tv_totaltransAmountT = v.findViewById(R.id.id_totaltransamountT);
        tv_totalWDtransCurrentDate = v.findViewById(R.id.id_totalwdtransCurrent);
        tv_commissionAmountTillDate = v.findViewById(R.id.id_comAmountTillDate);
        tv_commissionAmountCurrentDate = v.findViewById(R.id.commAmountCurrentDate);
        tv_amountTransferCurrentDate = v.findViewById(R.id.id_amountTransferCurrentDate);
        tv_amountTransferTillDate = v.findViewById(R.id.id_amountTransferTillDate);
        tv_totComlastsevenDays = v.findViewById(R.id.totComlastsevenDays);
        tv_totWDlastsevenDays = v.findViewById(R.id.totWDlastsevenDays);
        tv_totTransferlastsevenDays = v.findViewById(R.id.totTransferlastsevenDays);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat dfc = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = dfc.format(c);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDateandTime = sdf.format(new Date());
        Date cdate = null;
        try {
            cdate = sdf.parse(currentDateandTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now2 = Calendar.getInstance();
        now2.add(Calendar.DATE, -7);
        pastweekDate = now2.get(Calendar.YEAR) + "-" + (now2.get(Calendar.MONTH) + 1) + "-" + now2.get(Calendar.DATE);
        Date BeforeDate1 = null;
        try {
            BeforeDate1 = sdf.parse(pastweekDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cdate.compareTo(BeforeDate1);
        Log.d("TAG", "Before Date" + pastweekDate);
        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        agentPhn = preferences.getString("AgentPhone", "No name defined");
        agentId = preferences.getString("Username", "No name defined");
        password = preferences.getString("Password", "No name defined");
        //Get Date




Log.d("Date Check","Current Date"+currentDate+" "+"Last 7 Days:"+pastweekDate);
        new apiCall_getWalletDashboardDetails().execute();
        final SwipeRefreshLayout pullToRefresh = v.findViewById(R.id.refresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new apiCall_getWalletDashboardDetails().execute();
                pullToRefresh.setRefreshing(false);
            }
        });
        return v;
    }

    class apiCall_getWalletDashboardDetails extends AsyncTask<Request, Void, String> {
        String response_String, jsonString, jsonString2, jsonString3, jsonString4, jsonString5, jsonString6, jsonString7;


        ProgressDialog dialog = new ProgressDialog(getActivity(), ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading,Please wait....");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            httpClient = utils.createAuthenticatedClient(agentId, password);
            //  Log.d("TAG","EN_FLAG"+en_flag);
            //1.TOTAL WITHDRAW COUNT
            try {
                jsonObject.put("accountno", agentPhn);
                jsonObject.put("status", "SUCCESS");

                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totalWithdrawCount = jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalwd.setText(totalWithdrawCount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Withdrawal Count " + totalWithdrawCount);

                    }

                }

            });
            //2.TOTAL MINISTATEMENT COUNT
            Request request2 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByMSAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request2).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totalMinistatementCount = jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalms.setText(totalMinistatementCount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Mini Statement Count " + totalMinistatementCount);

                    }

                }

            });
            //3.TOTAL BE COUNT
            Request request3 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByBEAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request3).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totalBECount = jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalbe.setText(totalBECount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total BE Count " + totalBECount);

                    }

                }

            });
            //4.Total Wallet Amount Including Commission
            JSONObject jsonObject5 = new JSONObject();
            try {

                jsonObject5.put("agentid", agentId);
                jsonString5 = jsonObject5.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON2 = MediaType.parse("application/json");
            RequestBody body2 = RequestBody.create(JSON2, jsonString5);
            Request request4 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/WalletBalanceByagentID")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body2)
                    .build();
            httpClient.newCall(request4).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            walletAmount = jsonResponse.getString("sum");
                            Double value = Double.parseDouble(walletAmount);
                            if(value != null){
                                if(value == (double) Math.round(value)){
                                    if(value/1000000000 > 1.0){
                                        walletAmount = String.format("%.1f G", value/1000000000);
                                    }
                                    else if(value/1000000 > 1.0){
                                        walletAmount = String.format("%.1f M", value/1000000);
                                    }
                                    else if(value/1000 > 1.0){
                                        walletAmount = String.format("%.1f K", value/1000);
                                    }
                                    else{
                                        walletAmount = String.format("%.1f", value);
                                    }
                                }
                                else{
                                    walletAmount = String.format("%.2f", value);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_walletamount.setText(walletAmount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Wallet Amount Including Commission " + walletAmount);

                    }

                }

            });
            //5.Total Transaction Amount till Date

            Request request5 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/SumTotWithdrawalByAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request5).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totaltransAmountT = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totaltransAmountT.setText(totaltransAmountT);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Transaction Amount till Date " + totaltransAmountT);

                    }

                }

            });
            //6.Total Withdrawal Transactions Count current date
            //txndate,status,accountno
            JSONObject jsonObject2 = new JSONObject();
            try {
                jsonObject2.put("txndate", currentDate);
                jsonObject2.put("status", "TXN");
                jsonObject2.put("accountno", agentPhn);

                jsonString3 = jsonObject2.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("TAG","Total Withdrawal Transactions Count current date REQUEST"+jsonString3);
            MediaType JSON3 = MediaType.parse("application/json");
            RequestBody body3 = RequestBody.create(JSON3, jsonString3);
            Request request6 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/CountTotDateWithdrawal")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body3)
                    .build();
            httpClient.newCall(request6).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("Total Withdrawal Transactions Count current date Response" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totalWDtransCurrentDate = jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalWDtransCurrentDate.setText(totalWDtransCurrentDate);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Withdrawal Transactions Count current date " + totalWDtransCurrentDate);

                    }

                }

            });
            //7.Commission Amount Till Date
            //aeps/CommissionSumByagentID
            JSONObject jsonObject3 = new JSONObject();
            try {
                jsonObject3.put("agentid", agentId);


                jsonString4 = jsonObject3.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("TAG","COMMISSION AMOUNT REQUEST"+jsonString4);
            MediaType JSON4 = MediaType.parse("application/json");
            RequestBody body4 = RequestBody.create(JSON4, jsonString4);
            Request request7 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/CommissionSumByagentID")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body4)
                    .build();
            httpClient.newCall(request7).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    Log.d("TAG","COMMISSION AMOUNT RESPONSE IS" + response_String);
                    System.out.println("COMMISSION AMOUNT RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            commissionAmountTillDate = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_commissionAmountTillDate.setText(commissionAmountTillDate);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Commission Amount Till Date " + commissionAmountTillDate);

                    }

                }
            });
            //8.Commission Amount Current Date

            JSONObject jsonObject4 = new JSONObject();
            try {
                jsonObject4.put("agentid", agentId);
                jsonObject4.put("trandate", currentDate);

                jsonString4 = jsonObject4.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaType JSON5 = MediaType.parse("application/json");
            RequestBody body5 = RequestBody.create(JSON5, jsonString4);
            Request request8 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/CommissionSumByagentIDandDate")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body5)
                    .build();
            httpClient.newCall(request8).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            commissionAmountCurrentDate = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_commissionAmountCurrentDate.setText(commissionAmountCurrentDate);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Commission Amount Current Date " + commissionAmountCurrentDate);

                    }

                }


            });
            //9.Amount Transferred Till Date
            Request request9 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/TransferredAmountByagentID")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body4)
                    .build();
            httpClient.newCall(request9).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            amountTransferTillDate = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_amountTransferTillDate.setText(amountTransferTillDate);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Transferred Amount Till Date " + amountTransferTillDate);

                    }

                }
            });
            //10.Amount Transferred Current Date

            Request request10 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/TransferredAmountByagentIDandDate")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body5)
                    .build();
            httpClient.newCall(request10).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            amountTransferCurrentDate = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_amountTransferCurrentDate.setText(amountTransferCurrentDate);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Transfer Amount Current Date " + commissionAmountCurrentDate);

                    }

                }


            });
            //11.Total Withdrawal Transactions last 7 days
            JSONObject jsonObject6 = new JSONObject();
            try {
                jsonObject6.put("agentid", agentId);
                jsonObject6.put("datefrom", pastweekDate);
                jsonObject6.put("dateto", currentDate);

                jsonString6 = jsonObject6.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
Log.d("TAG","WITHDRAWAL LAST 7 DAYS REQUEST"+jsonString6);
            MediaType JSON6 = MediaType.parse("application/json");
            RequestBody body6 = RequestBody.create(JSON6, jsonString6);
            Request request11 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/CountRangeDateWithdrawal")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body6)
                    .build();
            httpClient.newCall(request11).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    Log.d("TAG","WITHDRAWAL LAST 7 DAYS RESPONSE"+response_String);
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totWDlastsevenDays = jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totWDlastsevenDays.setText(totWDlastsevenDays);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Withdrawal Last Seven Days " + totWDlastsevenDays);

                    }

                }


            });
            //12.Total Commission Last Seven Days
            JSONObject jsonObject7 = new JSONObject();
            try {
                jsonObject7.put("agentid", agentId);
                jsonObject7.put("datefrom", pastweekDate);
                jsonObject7.put("dateto", currentDate);

                jsonString7 = jsonObject7.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaType JSON7 = MediaType.parse("application/json");
            RequestBody body7 = RequestBody.create(JSON7, jsonString7);
            Request request12 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/CommissionSumByagentIDandDateRange")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body7)
                    .build();
            httpClient.newCall(request12).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totComlastsevenDays = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totComlastsevenDays.setText(totComlastsevenDays);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Commission Last Seven Days " + totComlastsevenDays);

                    }

                }


            });
            //13.Amount Transferred Last Seven Days
            Request request13 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/TransferredAmountByagentIDandDateRange")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body7)
                    .build();
            httpClient.newCall(request13).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure" + e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(), "Alert", "Invalid Credentials.\nPlease Contact Administrator", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS" + response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            totTransferlastsevenDays = jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totTransferlastsevenDays.setText(totTransferlastsevenDays);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Total Amount Transferred Last Seven Days " + totTransferlastsevenDays);

                    }

                }


            });
            return null;
        }
    }
}