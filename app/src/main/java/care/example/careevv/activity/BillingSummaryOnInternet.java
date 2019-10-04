package care.example.careevv.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import care.example.careevv.R;
import care.example.careevv.constants.ApiUrl;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.MyLog;
import care.example.careevv.util.ParseJasonLang;
import care.example.careevv.util.ProjectUtil;

import care.example.careevv.MainActivity;

public class BillingSummaryOnInternet extends AppCompatActivity {
    TextView tv_date,tv_start_time,tv_end_time,tv_total_time,tv_total_unit,title,date_title,start_time_title,end_time_title,total_time_title,
            total_unit_title,tryAgain,close_text,billing_text;


    ParseJasonLang parseJasonLang;
    LinearLayout llCheckInternet;
    DatabaseHandler db ;
    RelativeLayout scroll_internet;
    String billId="";

    void getId(){
        tv_date=findViewById(R.id.tv_date);
        tv_start_time=findViewById(R.id.tv_start_time);
        tv_end_time=findViewById(R.id.tv_end_time);
        tv_total_time=findViewById(R.id.tv_total_time);
        tv_total_unit=findViewById(R.id.tv_total_unit);
        title=findViewById(R.id.title);
        date_title=findViewById(R.id.date_title);
        start_time_title=findViewById(R.id.start_time_title);
        end_time_title=findViewById(R.id.end_time_title);
        total_time_title=findViewById(R.id.total_time_title);
        total_unit_title=findViewById(R.id.total_unit_title);
        llCheckInternet=findViewById(R.id.llCheckInternet);
        tryAgain=findViewById(R.id.tryAgain);
        scroll_internet=findViewById(R.id.scroll_internet);
        close_text=findViewById(R.id.close_text);
        billing_text=findViewById(R.id.billing_text);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_summary_on_internet);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            billId=bundle.getString("billId");
        }
        ImageView login_back=findViewById(R.id.login_back);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        parseJasonLang=new ParseJasonLang(BillingSummaryOnInternet.this);
        ConnectivityManager cm =
                (ConnectivityManager) BillingSummaryOnInternet.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        MainActivity.isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();




        final LinearLayout sign_button=findViewById(R.id.sign_button);
        getId();
        close_text.setText(parseJasonLang.getJsonToString("close"));
        sign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                ConnectivityManager cm =
                        (ConnectivityManager)BillingSummaryOnInternet.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                MainActivity.isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                finish();
            }
        });
        if(MainActivity.isConnected){
            llCheckInternet.setVisibility(View.GONE);
            scroll_internet.setVisibility(View.VISIBLE);
            sign_button.setVisibility(View.VISIBLE);
            getBillingDetails(billId);
        }else {
            llCheckInternet.setVisibility(View.VISIBLE);
            scroll_internet.setVisibility(View.GONE);
            sign_button.setVisibility(View.GONE);
        }
        title.setText(parseJasonLang.getJsonToString("Billing"));
        billing_text.setText(parseJasonLang.getJsonToString("Billing"));

        date_title.setText(parseJasonLang.getJsonToString("date"));
        start_time_title.setText(parseJasonLang.getJsonToString("started_time"));
        end_time_title.setText(parseJasonLang.getJsonToString("end_time"));
        total_time_title.setText(parseJasonLang.getJsonToString("total time"));
        total_unit_title.setText(parseJasonLang.getJsonToString("total_unit"));

       tryAgain.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ConnectivityManager cm =
                       (ConnectivityManager) BillingSummaryOnInternet.this.getSystemService(Context.CONNECTIVITY_SERVICE);

               NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
               MainActivity.isConnected = activeNetwork != null &&
                       activeNetwork.isConnectedOrConnecting();

               if(MainActivity.isConnected){
                   llCheckInternet.setVisibility(View.GONE);
                   scroll_internet.setVisibility(View.VISIBLE);
                   sign_button.setVisibility(View.VISIBLE);
                   getBillingDetails(billId);
               }else {
                   llCheckInternet.setVisibility(View.VISIBLE);
                   scroll_internet.setVisibility(View.GONE);
                   sign_button.setVisibility(View.GONE);
               }
           }
       });
    }


    @Override
    public void onBackPressed() {
        String action;
        ConnectivityManager cm =
                (ConnectivityManager)BillingSummaryOnInternet.this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        MainActivity.isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        finish();
    }

    public void getBillingDetails(final String billId ){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl.billing_summary,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONObject jsonObject = new JSONObject(response);
                            //dialog.stopProgress();


                            Log.v("stateArray",String.valueOf(response));

                            //now looping through all the elements of the json array

                            String date=jsonObject.getString("date");
                            String checkinTime=jsonObject.getString("checkinTime");
                            String checkoutTime=jsonObject.getString("checkoutTime");
                            String total_time=jsonObject.getString("total_time");
                            String total_unit=jsonObject.getString("total_unit");
                            tv_date.setText(date);
                            tv_start_time.setText(checkinTime);
                            tv_end_time.setText(checkoutTime);
                            tv_total_time.setText(total_time);
                            tv_total_unit.setText(total_unit);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v("stateArray",String.valueOf(e));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        ProjectUtil.showErrorResponse(getApplicationContext(), error);
                        Log.v("stateArray",String.valueOf(error));
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

//                params.put("token", MyPreferences.getInstance(ObservationActivity.this).getUSER_TOKEN());
//                params.put("userId",MyPreferences.getInstance(ObservationActivity.this).getUSERID());
                params.put("billId",billId );
                // params.put("scheduleId",sch_id);



                MyLog.showLog(params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                // This is where you specify the content type
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        //creating a request queue
        ProjectUtil.setRequest(getApplicationContext(), stringRequest);
    }
}
