package care.example.careevv.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import care.example.careevv.R;
import care.example.careevv.adapter.AllServiceListAdapter;
import care.example.careevv.constants.ApiUrl;
import care.example.careevv.model.CheckOutModel;
import care.example.careevv.model.ServiceModel;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.CustomProgressDialog;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.LocationTrack;
import care.example.careevv.util.MyLog;
import care.example.careevv.util.ParseJasonLang;
import care.example.careevv.util.ProjectUtil;

import org.json.JSONException;
import org.json.JSONObject;

import care.example.careevv.MainActivity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ClockOutActivity extends AppCompatActivity {
    private final static int ALL_PERMISSIONS_RESULT = 101;
    RecyclerView services_recycler;
    LinearLayout clock_in_out,weekly_checkOut;
    ArrayList<ServiceModel> serviceModelArrayList = new ArrayList<>();
    String name="",id="";
    TextView title,activity_text,save_text,save_text1;
    ImageView login_back;
    LoggedInUser loggedInUser;
    DatabaseHandler db ;
    int flag_equal=0;
    ParseJasonLang parseJasonLang;
    String client_lat,client_long;
    long diffHours = 0;
    long diffMinutes;
    String sch_id="",client_id="",checkinTime="",checkInDate="",unitRate;
    long total_unit,total_unit_reminder;
    double latitude,longitude;
    LocationTrack appLocationService;
    private CustomProgressDialog dialog;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    String dmas_activity="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_out);
        db = new DatabaseHandler(ClockOutActivity.this);
        parseJasonLang=new ParseJasonLang(ClockOutActivity.this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            name=bundle.getString("name");
            id=bundle.getString("id");


        }
        db.clearSelectedByUser();
        appLocationService = new LocationTrack(
                ClockOutActivity.this);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        loggedInUser = new LoggedInUser(getApplicationContext());
        db = new DatabaseHandler(ClockOutActivity.this);
        parseJasonLang= new ParseJasonLang(ClockOutActivity.this);
        loggedInUser= new LoggedInUser(ClockOutActivity.this);

        sch_id = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getSch_id();
        checkinTime = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getCheckin_time();
        checkInDate = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getCheckin_date();
        unitRate = db.getSchedule(sch_id).getUnitRate();

        client_lat=db.getSchedule(sch_id).getLat();
        client_long=db.getSchedule(sch_id).getLongi();
        services_recycler=findViewById(R.id.services_recycler);
        clock_in_out=findViewById(R.id.sign_button);
        title=findViewById(R.id.title);
        login_back=findViewById(R.id.login_back);
        save_text=findViewById(R.id.save_text);
        save_text1=findViewById(R.id.save_text1);
        activity_text=findViewById(R.id.activity_text);
        weekly_checkOut=findViewById(R.id.weekly_checkOut);
        title.setText(name);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearSelectedByUser();
                finish();
            }
        });


        getService();

        activity_text.setText(parseJasonLang.getJsonToString("activities"));


        save_text.setText(parseJasonLang.getJsonToString("daily_checkout"));
        save_text1.setText(parseJasonLang.getJsonToString("weekly_checkout"));



    }
    void getService(){
        serviceModelArrayList.clear();
        serviceModelArrayList= db.getAllContacts();
        services_recycler.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        services_recycler.setLayoutManager(manager);
      //  services_recycler.setLayoutManager(new LinearLayoutManager(ClockOutActivity.this));
        //creating recyclerview adapter
        AllServiceListAdapter adapter = new AllServiceListAdapter(ClockOutActivity.this, serviceModelArrayList);

        //setting adapter to recyclerview
        services_recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        weekly_checkOut.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                String[] selectedArray = new String[db.getAllSelected(id).size()];
                                                String[] selectedByUser = new String[db.getAllSelectedByUser().size()];

                                                for(int i =0; i< selectedArray.length;i++){
                                                    selectedArray[i]=db.getAllSelected(id).get(i).getId();
                                                    Log.v("selectedArray",selectedArray[i]);
                                                }



                                                for(int i =0; i< selectedByUser.length;i++){
                                                    selectedByUser[i]=db.getAllSelectedByUser().get(i).getId();
                                                    if(dmas_activity.equals("")){
                                                        dmas_activity=db.getAllSelectedByUser().get(i).getId();
                                                    }else {
                                                        dmas_activity=dmas_activity+","+db.getAllSelectedByUser().get(i).getId();
                                                    }
                                                    Log.v("selectedArray1",selectedByUser[i]);
                                                }

                                                loggedInUser.setDmas_activity(dmas_activity);

                                                if(selectedArray.length>selectedByUser.length||selectedArray.length<selectedByUser.length){

                                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);

                                                    // Setting Dialog Title
                                                    alertDialog.setTitle(parseJasonLang.getJsonToString("service_validation2"));
                                                    alertDialog.setCancelable(false);

                                                    // Setting Dialog Message
                                                    alertDialog.setMessage(parseJasonLang.getJsonToString("service_validation_message2"));

                                                    // Setting Icon to Dialog
                                                    alertDialog.setIcon(R.drawable.korevv);

                                                    // Setting Positive "Yes" Button
                                                    alertDialog.setPositiveButton(parseJasonLang.getJsonToString("continue"), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,int which) {

                                                            // Write your code here to invoke YES event
                                                            Intent intent = new Intent(getApplicationContext(), ObservationActivity.class);
                                                            intent.putExtra("name",name);
                                                            intent.putExtra("dmas_activity",dmas_activity);
                                                            startActivity(intent);

                                                        }
                                                    });

                                                    // Setting Negative "NO" Button
                                                    alertDialog.setNegativeButton(parseJasonLang.getJsonToString("cancel"), new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Write your code here to invoke NO event
                                                            loggedInUser.setDmas_activity("");
                                                            dmas_activity="";
                                                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                                            dialog.cancel();
                                                        }
                                                    });

                                                    // Showing Alert Message
                                                    alertDialog.show();

                                                }else {

                                                        for(int i=0;i<selectedArray.length;i++){

                                                            for(int j=0;j<selectedByUser.length;j++){
                                                                if(selectedArray[i].equals(selectedByUser[j])){
                                                                 flag_equal++;
                                                                }

                                                            }


                                                        }
                                                        if(flag_equal<selectedByUser.length){

                                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);

                                                            // Setting Dialog Title
                                                            alertDialog.setTitle(parseJasonLang.getJsonToString("service_validation2"));
                                                            alertDialog.setCancelable(false);

                                                            // Setting Dialog Message
                                                            alertDialog.setMessage(parseJasonLang.getJsonToString("service_validation_message2"));

                                                            // Setting Icon to Dialog
                                                            alertDialog.setIcon(R.drawable.korevv);

                                                            // Setting Positive "Yes" Button
                                                            alertDialog.setPositiveButton(parseJasonLang.getJsonToString("continue"), new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog,int which) {
                                                                    flag_equal=0;
                                                                    // Write your code here to invoke YES event
                                                                    Intent intent = new Intent(getApplicationContext(), ObservationActivity.class);
                                                                    intent.putExtra("name",name);
                                                                    intent.putExtra("dmas_activity",dmas_activity);
                                                                    startActivity(intent);

                                                                }
                                                            });

                                                            // Setting Negative "NO" Button
                                                            alertDialog.setNegativeButton(parseJasonLang.getJsonToString("cancel"), new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    // Write your code here to invoke NO event
                                                                    loggedInUser.setDmas_activity("");
                                                                    dmas_activity="";
                                                                    //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            // Showing Alert Message
                                                            alertDialog.show();
                                                            }else {
                                                            flag_equal=0;
                                                            Intent intent = new Intent(getApplicationContext(), ObservationActivity.class);
                                                            intent.putExtra("name",name);
                                                            intent.putExtra("dmas_activity",dmas_activity);
                                                            startActivity(intent);
                                                        }
                                                    }





                                            }
                                        }
            );


        clock_in_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] selectedArray = new String[db.getAllSelected(id).size()];
                String[] selectedByUser = new String[db.getAllSelectedByUser().size()];

                for(int i =0; i< selectedArray.length;i++){
                    selectedArray[i]=db.getAllSelected(id).get(i).getId();
                    Log.v("selectedArray",selectedArray[i]);
                }



                for(int i =0; i< selectedByUser.length;i++){
                    selectedByUser[i]=db.getAllSelectedByUser().get(i).getId();
                    if(dmas_activity.equals("")){
                        dmas_activity=db.getAllSelectedByUser().get(i).getId();
                    }else {
                        dmas_activity=dmas_activity+","+db.getAllSelectedByUser().get(i).getId();
                    }
                    Log.v("selectedArray1",selectedByUser[i]);
                }

                loggedInUser.setDmas_activity(dmas_activity);



                if(selectedArray.length>selectedByUser.length||selectedArray.length<selectedByUser.length){

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(parseJasonLang.getJsonToString("service_validation2"));
                    alertDialog.setCancelable(false);

                    // Setting Dialog Message
                    alertDialog.setMessage(parseJasonLang.getJsonToString("service_validation_message2"));

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.korevv);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton(parseJasonLang.getJsonToString("continue"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            String action;

                            ConnectivityManager cm =
                                    (ConnectivityManager) ClockOutActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                            MainActivity.isConnected = activeNetwork != null &&
                                    activeNetwork.isConnectedOrConnecting();

                            Calendar c = Calendar.getInstance();
                            System.out.println("Current time =&gt; " + c.getTime());

                            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                            SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
                            final String formattedDate = df.format(c.getTime());
                            final String formattedtime = df1.format(c.getTime());

                            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

                            Date d1 = null;
                            Date d2 = null;


                            try {
                                d1 = format.parse(checkInDate + " " + checkinTime);
                                d2 = format.parse(formattedDate + " " + formattedtime);

                                //in milliseconds
                                long diff = d2.getTime() - d1.getTime();

                                long diffSeconds = diff / 1000 % 60;
                                diffMinutes = diff / (60 * 1000) % 60;
                                diffHours = diff / (60 * 60 * 1000) % 24;
                                long diffDays = diff / (24 * 60 * 60 * 1000);

                                if(diffHours>0){
                                    diffMinutes = (diffHours * 60) + diffMinutes;
                                }else {
                                    diffMinutes = diffMinutes;
                                }
                                System.out.print(diffDays + " days, ");
                                System.out.print(diffHours + " hours, ");
                                System.out.print(diffMinutes + " minutes, ");
                                System.out.print(diffSeconds + " seconds.");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(diffMinutes<8) {
                                total_unit=0;

                            }else {

                                total_unit = diffMinutes / Long.parseLong(unitRate.split("\\.")[0], 10);
                                total_unit_reminder = diffMinutes % Long.parseLong(unitRate.split("\\.")[0], 10);
                                if(total_unit==0){
                                    total_unit=1;
                                }else {
                                    if(total_unit_reminder>7){
                                        total_unit = total_unit+1;
                                    }else {
                                        total_unit = total_unit;
                                    }
                                }
                            }
                            Location gpsLocation = appLocationService
                                    .getLocation(LocationManager.GPS_PROVIDER);


                            if (gpsLocation != null) {
                                latitude = gpsLocation.getLatitude();
                                longitude = gpsLocation.getLongitude();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "Mobile Location (GPS): \nLatitude: " + latitude
//                                    + "\nLongitude: " + longitude+ formattedDate + " "+formattedDate1,
//                            Toast.LENGTH_LONG).show();
                            } else {



                            }
                            double direction = distance(Double.parseDouble(client_lat), Double.parseDouble(client_long), latitude, longitude);

                            Log.v("direction", String.valueOf(direction));



//                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);
//
//                            // Setting Dialog Title
//                            alertDialog.setTitle("CareEvv");
//                            alertDialog.setCancelable(false);
//
//                            // Setting Dialog Message
//                            alertDialog.setMessage(parseJasonLang.getJsonToString("clockout_message"));
//
//
//                            // Setting Icon to Dialog
//                            alertDialog.setIcon(R.drawable.korevv);
//
//                            // Setting Positive "Yes" Button
//                            alertDialog.setPositiveButton(parseJasonLang.getJsonToString("yes"), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {

                                    if (MainActivity.isConnected) {
                                        getClockOutApi(dmas_activity,"", "", "", "", "", "",
                                                "", "",
                                                "", "", formattedDate, formattedtime, client_lat, client_long, 0);
                                    } else {
                                        db.addCheckOutDetails(new CheckOutModel(sch_id, loggedInUser.getLocal_user_id(), String.valueOf(latitude),
                                                String.valueOf(longitude), formattedDate, formattedtime, "", "", "", "", "",
                                                "", "",
                                                "", "", "",
                                                "","",dmas_activity));
                                        Intent intent = new Intent(getApplicationContext(), BillingSummaryActivity.class);
                                        intent.putExtra("date", formattedDate);
                                        intent.putExtra("checkinTime", checkinTime);
                                        intent.putExtra("checkoutTime", formattedtime);
                                        intent.putExtra("total_time", String.valueOf(diffHours +":" + diffMinutes));
                                        intent.putExtra("total_unit", String.valueOf(total_unit));
                                        intent.putExtra("sch_id",sch_id);
                                        startActivity(intent);
                                        finish();
                                    }


//                                }
//                            });
//
//                            // Setting Negative "NO" Button
//                            alertDialog.setNegativeButton(parseJasonLang.getJsonToString("no"), new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dmas_activity="";
//                                    loggedInUser.setDmas_activity("");
//                                    // Write your code here to invoke NO event
//                                    //  Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                                    dialog.cancel();
//                                }
//                            });
//
//                            // Showing Alert Message
//                            alertDialog.show();
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton(parseJasonLang.getJsonToString("cancel"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            loggedInUser.setDmas_activity("");
                            dmas_activity="";
                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                }else {

                    for(int i=0;i<selectedArray.length;i++){

                        for(int j=0;j<selectedByUser.length;j++){
                            if(selectedArray[i].equals(selectedByUser[j])){
                                flag_equal++;
                            }

                        }


                    }
                    if(flag_equal<selectedByUser.length){

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);

                        // Setting Dialog Title
                        alertDialog.setTitle(parseJasonLang.getJsonToString("service_validation2"));
                        alertDialog.setCancelable(false);

                        // Setting Dialog Message
                        alertDialog.setMessage(parseJasonLang.getJsonToString("service_validation_message2"));

                        // Setting Icon to Dialog
                        alertDialog.setIcon(R.drawable.korevv);

                        // Setting Positive "Yes" Button
                        alertDialog.setPositiveButton(parseJasonLang.getJsonToString("continue"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                flag_equal=0;
                                // Write your code here to invoke YES event
                                String action;

                                ConnectivityManager cm =
                                        (ConnectivityManager) ClockOutActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                                MainActivity.isConnected = activeNetwork != null &&
                                        activeNetwork.isConnectedOrConnecting();

                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time =&gt; " + c.getTime());

                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
                                final String formattedDate = df.format(c.getTime());
                                final String formattedtime = df1.format(c.getTime());

                                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

                                Date d1 = null;
                                Date d2 = null;


                                try {
                                    d1 = format.parse(checkInDate + " " + checkinTime);
                                    d2 = format.parse(formattedDate + " " + formattedtime);

                                    //in milliseconds
                                    long diff = d2.getTime() - d1.getTime();

                                    long diffSeconds = diff / 1000 % 60;
                                    diffMinutes = diff / (60 * 1000) % 60;
                                    diffHours = diff / (60 * 60 * 1000) % 24;
                                    long diffDays = diff / (24 * 60 * 60 * 1000);

                                    if(diffHours>0){
                                        diffMinutes = (diffHours * 60) + diffMinutes;
                                    }else {
                                        diffMinutes = diffMinutes;
                                    }
                                    System.out.print(diffDays + " days, ");
                                    System.out.print(diffHours + " hours, ");
                                    System.out.print(diffMinutes + " minutes, ");
                                    System.out.print(diffSeconds + " seconds.");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if(diffMinutes<8) {
                                    total_unit=0;

                                }else {

                                    total_unit = diffMinutes / Long.parseLong(unitRate.split("\\.")[0], 10);
                                    total_unit_reminder = diffMinutes % Long.parseLong(unitRate.split("\\.")[0], 10);
                                    if(total_unit==0){
                                        total_unit=1;
                                    }else {
                                        if(total_unit_reminder>7){
                                            total_unit = total_unit+1;
                                        }else {
                                            total_unit = total_unit;
                                        }
                                    }
                                }
                                Location gpsLocation = appLocationService
                                        .getLocation(LocationManager.GPS_PROVIDER);


                                if (gpsLocation != null) {
                                    latitude = gpsLocation.getLatitude();
                                    longitude = gpsLocation.getLongitude();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "Mobile Location (GPS): \nLatitude: " + latitude
//                                    + "\nLongitude: " + longitude+ formattedDate + " "+formattedDate1,
//                            Toast.LENGTH_LONG).show();
                                } else {



                                }
                                double direction = distance(Double.parseDouble(client_lat), Double.parseDouble(client_long), latitude, longitude);

                                Log.v("direction", String.valueOf(direction));



//                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);
//
//                                // Setting Dialog Title
//                                alertDialog.setTitle("CareEvv");
//                                alertDialog.setCancelable(false);
//
//                                // Setting Dialog Message
//                                alertDialog.setMessage(parseJasonLang.getJsonToString("clockout_message"));
//
//
//                                // Setting Icon to Dialog
//                                alertDialog.setIcon(R.drawable.korevv);
//
//                                // Setting Positive "Yes" Button
//                                alertDialog.setPositiveButton(parseJasonLang.getJsonToString("yes"), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {

                                        if (MainActivity.isConnected) {
                                            getClockOutApi(dmas_activity,"", "", "", "", "", "",
                                                    "", "",
                                                    "", "", formattedDate, formattedtime, client_lat, client_long, 0);
                                        } else {
                                            db.addCheckOutDetails(new CheckOutModel(sch_id, loggedInUser.getLocal_user_id(), String.valueOf(latitude),
                                                    String.valueOf(longitude), formattedDate, formattedtime, "", "", "", "", "",
                                                    "", "",
                                                    "", "", "",
                                                    "","",dmas_activity));
                                            Intent intent = new Intent(getApplicationContext(), BillingSummaryActivity.class);
                                            intent.putExtra("date", formattedDate);
                                            intent.putExtra("checkinTime", checkinTime);
                                            intent.putExtra("checkoutTime", formattedtime);
                                            intent.putExtra("total_time", String.valueOf(diffHours +":" + diffMinutes));
                                            intent.putExtra("total_unit", String.valueOf(total_unit));
                                            intent.putExtra("sch_id",sch_id);
                                            startActivity(intent);
                                            finish();
                                        }

//
//                                    }
//                                });
//
//                                // Setting Negative "NO" Button
//                                alertDialog.setNegativeButton(parseJasonLang.getJsonToString("no"), new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // Write your code here to invoke NO event
//                                        loggedInUser.setDmas_activity("");
//                                        dmas_activity="";
//                                        //  Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                                        dialog.cancel();
//                                    }
//                                });
//
//                                // Showing Alert Message
//                                alertDialog.show();

                            }
                        });

                        // Setting Negative "NO" Button
                        alertDialog.setNegativeButton(parseJasonLang.getJsonToString("cancel"), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to invoke NO event
                                loggedInUser.setDmas_activity("");
                                dmas_activity="";
                                //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                        // Showing Alert Message
                        alertDialog.show();
                    }else {
                        flag_equal=0;
                        String action;

                        ConnectivityManager cm =
                                (ConnectivityManager) ClockOutActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        MainActivity.isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();

                        Calendar c = Calendar.getInstance();
                        System.out.println("Current time =&gt; " + c.getTime());

                        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat df1 = new SimpleDateFormat("hh:mm a");
                        final String formattedDate = df.format(c.getTime());
                        final String formattedtime = df1.format(c.getTime());

                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm");

                        Date d1 = null;
                        Date d2 = null;


                        try {
                            d1 = format.parse(checkInDate + " " + checkinTime);
                            d2 = format.parse(formattedDate + " " + formattedtime);

                            //in milliseconds
                            long diff = d2.getTime() - d1.getTime();

                            long diffSeconds = diff / 1000 % 60;
                            diffMinutes = diff / (60 * 1000) % 60;
                            diffHours = diff / (60 * 60 * 1000) % 24;
                            long diffDays = diff / (24 * 60 * 60 * 1000);

                            if(diffHours>0){
                                diffMinutes = (diffHours * 60) + diffMinutes;
                            }else {
                                diffMinutes = diffMinutes;
                            }
                            System.out.print(diffDays + " days, ");
                            System.out.print(diffHours + " hours, ");
                            System.out.print(diffMinutes + " minutes, ");
                            System.out.print(diffSeconds + " seconds.");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if(diffMinutes<8) {
                            total_unit=0;

                        }else {

                            total_unit = diffMinutes / Long.parseLong(unitRate.split("\\.")[0], 10);
                            total_unit_reminder = diffMinutes % Long.parseLong(unitRate.split("\\.")[0], 10);
                            if(total_unit==0){
                                total_unit=1;
                            }else {
                                if(total_unit_reminder>7){
                                    total_unit = total_unit+1;
                                }else {
                                    total_unit = total_unit;
                                }
                            }
                        }
                        Location gpsLocation = appLocationService
                                .getLocation(LocationManager.GPS_PROVIDER);


                        if (gpsLocation != null) {
                            latitude = gpsLocation.getLatitude();
                            longitude = gpsLocation.getLongitude();
//                    Toast.makeText(
//                            getApplicationContext(),
//                            "Mobile Location (GPS): \nLatitude: " + latitude
//                                    + "\nLongitude: " + longitude+ formattedDate + " "+formattedDate1,
//                            Toast.LENGTH_LONG).show();
                        } else {



                        }
                        double direction = distance(Double.parseDouble(client_lat), Double.parseDouble(client_long), latitude, longitude);

                        Log.v("direction", String.valueOf(direction));



//                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ClockOutActivity.this);
//
//                        // Setting Dialog Title
//                        alertDialog.setTitle("CareEvv");
//                        alertDialog.setCancelable(false);
//
//                        // Setting Dialog Message
//                        alertDialog.setMessage(parseJasonLang.getJsonToString("clockout_message"));
//
//
//                        // Setting Icon to Dialog
//                        alertDialog.setIcon(R.drawable.korevv);
//
//                        // Setting Positive "Yes" Button
//                        alertDialog.setPositiveButton(parseJasonLang.getJsonToString("yes"), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {

                                if (MainActivity.isConnected) {
                                    getClockOutApi(dmas_activity,"", "", "", "", "", "",
                                            "", "",
                                            "", "", formattedDate, formattedtime, client_lat, client_long, 0);
                                } else {
                                    db.addCheckOutDetails(new CheckOutModel(sch_id, loggedInUser.getLocal_user_id(), String.valueOf(latitude),
                                            String.valueOf(longitude), formattedDate, formattedtime, "", "", "", "", "",
                                            "", "",
                                            "", "", "",
                                            "","",dmas_activity));
                                    Intent intent = new Intent(getApplicationContext(), BillingSummaryActivity.class);
                                    intent.putExtra("date", formattedDate);
                                    intent.putExtra("checkinTime", checkinTime);
                                    intent.putExtra("checkoutTime", formattedtime);
                                    intent.putExtra("total_time", String.valueOf(diffHours +":" + diffMinutes));
                                    intent.putExtra("total_unit", String.valueOf(total_unit));
                                    intent.putExtra("sch_id",sch_id);
                                    startActivity(intent);
                                    finish();
                                }


//                            }
//                        });
//
//                        // Setting Negative "NO" Button
//                        alertDialog.setNegativeButton(parseJasonLang.getJsonToString("no"), new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // Write your code here to invoke NO event
//                                loggedInUser.setDmas_activity("");
//                                dmas_activity="";
//                                //  Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                                dialog.cancel();
//                            }
//                        });
//
//                        // Showing Alert Message
//                        alertDialog.show();
                    }
                }











            }
        });

    }



    public void getClockOutApi(
            final String dmas_activity,final String client_sign, final String caregiver_sign, final String question1_yn , final String question2_yn, final String question3_yn , final String question4_yn, final String question1_note,
                               final String question2_note , final String question3_note, final String question4_note,
                               final String startDate, final String startTime, final String client_lat, final String client_long , final int position){


        dialog= new CustomProgressDialog();
        dialog.startProgress(ClockOutActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl.checkout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONObject jsonObject = new JSONObject(response);
                            dialog.stopProgress();

                            // Log.v("direction",String.valueOf(direction));
                            //db.addCheckInDetails(new CheckInModel(id, loggedInUser.getLocal_agency(), client_id, loggedInUser.getLocal_user_id(), startDate, startTime, String.valueOf(latitude) + "," + String.valueOf(longitude)));


                            String status=jsonObject.getString("status");
                            String message=jsonObject.getString("message");

                            String billId =jsonObject.getString("billId");
                            getBillingDetails(billId);
//                                String endTime=jsonObject1.getString("endTime");
//                                String address=jsonObject1.getString("address");
//                                String mobileNo=jsonObject1.getString("mobileNo");
//                                String unitRate=jsonObject1.getString("unitRate");
//                                String clientId=jsonObject1.getString("clientId");
//                                String latitude=jsonObject1.getString("latitude");
//                                String longitude=jsonObject1.getString("longitude");
//                                String sechudule_status=jsonObject1.getString("sechudule_status");
//                                String profileImg=jsonObject1.getString("profileImg");
//
//


                            // scheduleModelArrayList=db.getAllImages();

                            Log.v("stateArray", String.valueOf(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.stopProgress();

                            Log.v("stateArray",String.valueOf(e));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        dialog.stopProgress();
                        DateFormat dateFormat2 = new SimpleDateFormat("dd");
                        Date date2 = new Date();
                        Log.d("Month",dateFormat2.format(date2));
                        ProjectUtil.showErrorResponse(ClockOutActivity.this, error);

                        Log.v("stateArray",String.valueOf(error));
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("token", loggedInUser.getLocal_session_toekn());
                params.put("userId",loggedInUser.getLocal_user_id());
                params.put("check_out_latitude",client_lat);
                params.put("scheduleId",sch_id);
                params.put("checkout_date",startDate);

                params.put("client_sign", client_sign);
                params.put("checkout_time", startTime);
                params.put("check_out_longitude", client_long);


//                params.put("deviceId", Settings.Secure.getString(
//                        getContentResolver(), Settings.Secure.ANDROID_ID));
                params.put("question1_yn", question1_yn);
                params.put("question2_yn",question2_yn );
                params.put("question3_yn", question3_yn);
                params.put("question4_yn", question4_yn);
                params.put("question1_note", question1_note);
                params.put("question2_note", question2_note);
                params.put("question3_note",question3_note);
                params.put("question4_note", question4_note);
                params.put("additional_comments","");
                params.put("client_sign_name", "");
                params.put("caregiver_sign", caregiver_sign);
                params.put("dmas_activity", dmas_activity);

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
        ProjectUtil.setRequest(ClockOutActivity.this, stringRequest);
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ClockOutActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onBackPressed() {
        db.clearSelectedByUser();
        finish();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

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
                            Intent intent = new Intent(getApplicationContext(),BillingSummaryActivity.class);
                            intent.putExtra("date",date);
                            intent.putExtra("checkinTime",checkinTime);
                            intent.putExtra("checkoutTime",checkoutTime);
                            intent.putExtra("total_time",total_time);
                            intent.putExtra("total_unit",total_unit);
                            intent.putExtra("sch_id",sch_id);
                            startActivity(intent);
                            finish();




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
    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

}
