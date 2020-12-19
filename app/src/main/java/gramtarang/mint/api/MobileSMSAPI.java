package gramtarang.mint.api;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import gramtarang.mint.utils.Utils;

public class  MobileSMSAPI extends AppCompatActivity {



    public String sendOTP(String otp, String PhoneNumber, String username){
        Utils gethour=new Utils();
        String greeting = gethour.gethour();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try
        {
// Construct data
            Log.d("MobileSMS","Message sent "+otp);
            String data="user=" + URLEncoder.encode("GRAMTARANG", "UTF-8");
            data +="&password=" + URLEncoder.encode("Gramtarang@2020", "UTF-8");
            data +="&message=" + URLEncoder.encode(greeting+","+" "+username+"\n"+"Your OTP for login is:"+otp+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
            data +="&sender=" + URLEncoder.encode("MINTSM", "UTF-8");
            data +="&mobile=" + URLEncoder.encode(PhoneNumber, "UTF-8");
            data +="&type=" + URLEncoder.encode("3", "UTF-8");
// Send data
URL url = new URL("https://login.bulksmsgateway.in/sendmessage.php?"+data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1="";
            while ((line = rd.readLine()) != null)
            {
// Process line...
                sResult1=sResult1+line+" ";
            }
            wr.close();
            rd.close();
            return sResult1;

        }
        catch (Exception e)
        {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }




    public String sendtransmsg(String PhoneNumber,String username,String message,String transtype){
        Utils gethour=new Utils();
        String greeting = gethour.gethour();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try
        {
// Construct data

            String data="user=" + URLEncoder.encode("GRAMTARANG", "UTF-8");
            data +="&password=" + URLEncoder.encode("Gramtarang@2020", "UTF-8");
            data +="&message=" + URLEncoder.encode(greeting+","+" "+username+"\n"+"Thank you for banking with us"+"\n"+"Your transaction details are:"+"\n"+"Transaction Type:"+"\n"+transtype+"Message:"+message+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
            data +="&sender=" + URLEncoder.encode("MINTSM", "UTF-8");
            data +="&mobile=" + URLEncoder.encode(PhoneNumber, "UTF-8");
            data +="&type=" + URLEncoder.encode("3", "UTF-8");
// Send data
            URL url = new URL("https://login.bulksmsgateway.in/sendmessage.php?"+data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1="";
            while ((line = rd.readLine()) != null)
            {
// Process line...
                sResult1=sResult1+line+" ";
            }
            wr.close();
            rd.close();
            return sResult1;

        }
        catch (Exception e)
        {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }




    public String sendloanconfirmation(String name,String phnnumber,String loanid){
        Utils gethour=new Utils();
        String greeting = gethour.gethour();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try
        {
// Construct data

            String data="user=" + URLEncoder.encode("GRAMTARANG", "UTF-8");
            data +="&password=" + URLEncoder.encode("Gramtarang@2020", "UTF-8");
            data +="&message=" + URLEncoder.encode(greeting+","+" "+name+"\n"+"Thank you for banking with us"+"\n"+"Your Loan Application ID is:"+loanid+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
            data +="&sender=" + URLEncoder.encode("MINTSM", "UTF-8");
            data +="&mobile=" + URLEncoder.encode(phnnumber, "UTF-8");
            data +="&type=" + URLEncoder.encode("3", "UTF-8");
// Send data
            URL url = new URL("https://login.bulksmsgateway.in/sendmessage.php?"+data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1="";
            while ((line = rd.readLine()) != null)
            {
// Process line...
                sResult1=sResult1+line+" ";
            }
            wr.close();
            rd.close();
            return sResult1;

        }
        catch (Exception e)
        {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }



    public String sendloanverification(String name,String phnnumber,String otp){
        Utils gethour=new Utils();
        String greeting = gethour.gethour();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        try
        {
// Construct data

            String data="user=" + URLEncoder.encode("GRAMTARANG", "UTF-8");
            data +="&password=" + URLEncoder.encode("Gramtarang@2020", "UTF-8");
            data +="&message=" + URLEncoder.encode(greeting+","+" "+name+"\n"+"Your OTP for Loan Application  is:"+otp+"\n"+"\n"+"With Regards,"+"\n"+"GTIDS IT Team", "UTF-8");
            data +="&sender=" + URLEncoder.encode("MINTSM", "UTF-8");
            data +="&mobile=" + URLEncoder.encode(phnnumber, "UTF-8");
            data +="&type=" + URLEncoder.encode("3", "UTF-8");
// Send data
            URL url = new URL("https://login.bulksmsgateway.in/sendmessage.php?"+data);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
// Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String sResult1="";
            while ((line = rd.readLine()) != null)
            {
// Process line...
                sResult1=sResult1+line+" ";
            }
            wr.close();
            rd.close();
            return sResult1;

        }
        catch (Exception e)
        {
            System.out.println("Error SMS "+e);
            return "Error "+e;
        }
    }

// TODO Auto-generated method stub
}
