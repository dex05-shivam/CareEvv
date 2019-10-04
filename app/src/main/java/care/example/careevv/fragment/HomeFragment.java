package care.example.careevv.fragment;


import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import care.example.careevv.MainActivity;
import care.example.careevv.R;
import care.example.careevv.adapter.DateAdapter;
import care.example.careevv.adapter.ScheduleAdapter;
import care.example.careevv.constants.ApiUrl;
import care.example.careevv.model.DateModel;
import care.example.careevv.model.ScheduleModel;
import care.example.careevv.model.ServiceModel;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.CustomProgressDialog;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.MyLog;
import care.example.careevv.util.ParseJasonLang;
import care.example.careevv.util.ProjectUtil;
import com.whiteelephant.monthpicker.MonthPickerDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DateAdapter.SingleClickListener {
  //  private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;
    private CustomProgressDialog dialog;
    List<ScheduleModel> scheduleModelArrayList = new ArrayList<>();
    RecyclerView date_recycler,recyclerView;
    LinearLayout ll1,llCheckInternet;

    String month="",year="",month1="",selected_date="";
    TextView text_month_year,tryAgain;
    String array_year[]=new String[2];
    static String months[] =
            {
                    "January" , "February" , "March" , "April", "May",
                    "June", "July", "August", "September", "October",
                    "November", "December"
            };
    View x;

    private RecyclerView.LayoutManager mLayoutManager;
    private DateAdapter mAdapter;
    ArrayList<DateModel> dateModelArrayList= new ArrayList<>();
    LoggedInUser loggedInUser;
    DatabaseHandler db ;
    DateFormat dateFormat2,dateFormat1,dateFormat;
    Date date,date1,date2;
    ParseJasonLang parseJasonLang;
    TextView no_data_text;
    ImageView select_month_year;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        x=inflater.inflate(R.layout.fragment_home, container, false);
        date_recycler=x.findViewById(R.id.date_recycler);
        loggedInUser= new LoggedInUser(getContext());
        db = new DatabaseHandler(getContext());
        ll1=x.findViewById(R.id.ll1);
        llCheckInternet=x.findViewById(R.id.llCheckInternet);
        tryAgain=x.findViewById(R.id.tryAgain);
        text_month_year=x.findViewById(R.id.text_month_year);
        no_data_text=x.findViewById(R.id.no_data_text);
        select_month_year=x.findViewById(R.id.select_month_year);

        DateAdapter.sSelected=-1;
        DateAdapter.date_flag=0;





        getCurrentDate(x);
        parseJasonLang=new ParseJasonLang(getContext());

        recyclerView=x.findViewById(R.id.recyclerView);

        dateFormat1 = new SimpleDateFormat("yyyy");
        Date date1 = new Date();

        for (int i =0;i<2;i++){

                array_year[i]=String.valueOf(Integer.parseInt(dateFormat1.format(date1))+i);

        }

        for(int i = 0; i<array_year.length;i++){
            if(Integer.parseInt(dateFormat1.format(date1))==Integer.parseInt(array_year[i])){
                year=array_year[i];
            }
        }



        no_data_text.setText(parseJasonLang.getJsonToString("no_schedule"));

        MainActivity.main_title.setText(parseJasonLang.getJsonToString("Schedule"));

        dateFormat = new SimpleDateFormat("MM");
        date = new Date();

        for(int i = 0; i<months.length;i++){
            if(Integer.parseInt(dateFormat.format(date))==i+1){
                month=months[i];
                month1=dateFormat.format(date);
            }
        }
        text_month_year.setText(month +", "+year);
        select_month_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showCustomDialog();
                final Calendar today = Calendar.getInstance();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(getContext(),
                        new MonthPickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(int selectedMonth, int selectedYear) {
                                // on date set

                                for(int i = 0; i<months.length;i++){
                                    if(selectedMonth==i){
                                        month=months[i];
                                        month1=String.valueOf(selectedMonth+1);
                                    }
                                }
                                year=String.valueOf(selectedYear);
                                month1=String.valueOf(selectedMonth+1);
                                text_month_year.setText(month +", "+String.valueOf(selectedYear));

                                if(MainActivity.isConnected) {

                                    try {
                                        if(loggedInUser.getLocal_language().equals("")) {
                                            getService_category("en", selected_date + "-" + String.valueOf(selectedMonth+1) + "-" + String.valueOf(selectedYear), 0);
                                        }else {
                                            getService_category(loggedInUser.getLocal_language(), selected_date + "-" + String.valueOf(selectedMonth+1) + "-" + String.valueOf(selectedYear), 0);
                                        }
                                    }catch (Exception e){

                                    }

                                }

                                dateModelArrayList.clear();



                                if(month.equals("June")||month.equals("April")||month.equals("September")||month.equals("November")){
                                    for(int i = 0;i<30;i++){

                                        DateModel dateModel;

                                        dateModel = new DateModel("1",String.valueOf(i+1));

                                        dateModelArrayList.add(dateModel);


                                    }
                                }
                                if(month.equals("February")){
                                    for(int i = 0;i<28;i++){

                                        DateModel dateModel;

                                        dateModel = new DateModel("1",String.valueOf(i+1));

                                        dateModelArrayList.add(dateModel);


                                    }
                                }


                                if(month.equals("January")||month.equals("March")||month.equals("May")||month.equals("July")||month.equals("August")||month.equals("October")||month.equals("December")){
                                    for(int i = 0;i<31;i++){

                                        DateModel dateModel;

                                        dateModel = new DateModel("1",String.valueOf(i+1));

                                        dateModelArrayList.add(dateModel);


                                    }
                                }
                                mAdapter.notifyDataSetChanged();


                            }
                            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
                builder.setActivatedMonth((Integer.parseInt(dateFormat.format(date))-1))
                        .setMinYear(2019)
                        .setActivatedYear(2019)
                        .setMaxYear(2020)
                        //.setMinMonth(Calendar.FEBRUARY)
                        .setTitle(parseJasonLang.getJsonToString("select_month_year"))
                        .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                        // .setMaxMonth(Calendar.OCTOBER)
                        // .setYearRange(1890, 1890)
                        // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                        //.showMonthOnly()
                        // .showYearOnly()
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {

                            } })
       .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                                    @Override
                                    public void onYearChanged(int selectedYear) {  } })
        .build()
                                                .show();



            }
        });



        dateModelArrayList.clear();



        if(month.equals("June")||month.equals("April")||month.equals("September")||month.equals("November")){
            for(int i = 0;i<30;i++){

                DateModel dateModel;

                dateModel = new DateModel("1",String.valueOf(i+1));

                dateModelArrayList.add(dateModel);


            }
        }
        if(month.equals("February")){
            for(int i = 0;i<28;i++){

                DateModel dateModel;

                dateModel = new DateModel("1",String.valueOf(i+1));

                dateModelArrayList.add(dateModel);


            }
        }


        if(month.equals("January")||month.equals("March")||month.equals("May")||month.equals("July")||month.equals("August")||month.equals("October")||month.equals("December")){
            for(int i = 0;i<31;i++){

                DateModel dateModel;

                dateModel = new DateModel("1",String.valueOf(i+1));

                dateModelArrayList.add(dateModel);


            }
        }




        mLayoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerView.setHasFixedSize(true);
        date_recycler.setLayoutManager(mLayoutManager);

        mAdapter = new DateAdapter(getContext(),dateModelArrayList);
        //DateModel dateModel = dateModelArrayList.get(position);
        dateFormat2 = new SimpleDateFormat("dd");
        date2 = new Date();
        Log.v("Month",dateFormat2.format(date2));
        Log.v("Month1",month);
        selected_date=dateFormat2.format(date2);
        if(Integer.parseInt(dateFormat2.format(date2))>10) {
            date_recycler.smoothScrollToPosition(Integer.parseInt(dateFormat2.format(date2)) - 5);
            date_recycler.scrollToPosition((Integer.parseInt(dateFormat2.format(date2)) - 5));
        }else {
            date_recycler.smoothScrollToPosition(0);
            date_recycler.scrollToPosition(0);
        }

        // Set an adapter for RecyclerView
        date_recycler.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);


        if(MainActivity.isConnected) {
            if(loggedInUser.getLocal_language().equals("")) {
                getService_category("en", dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
            }else {
                getService_category(loggedInUser.getLocal_language(), dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
            }
        }else {
            scheduleModelArrayList=db.getAllImages(loggedInUser.getLocal_user_id());



            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //creating recyclerview adapter
            ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleModelArrayList);

            //setting adapter to recyclerview
            recyclerView.setAdapter(adapter);
        }

        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm =
                        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                MainActivity.isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(MainActivity.isConnected) {

                    try {

                        if(loggedInUser.getLocal_language().equals("")) {
                            getService_category("en", dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
                        }else {
                            getService_category(loggedInUser.getLocal_language(), dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
                        }
                    }catch (Exception e){

                    }


                }else {
                    scheduleModelArrayList=db.getAllImages(loggedInUser.getLocal_user_id());

                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    //creating recyclerview adapter
                    ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleModelArrayList);

                    //setting adapter to recyclerview
                    recyclerView.setAdapter(adapter);
                }
            }
        });
//        if(isConnected) {
//            if(loggedInUser.getLocal_language().equals("")){
//                getService_category1("en",)
//            }
//
//        }
        return x;
    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(getContext());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_year_month);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        ImageView login_back = (ImageView) dialog.findViewById(R.id.login_back);
        Spinner spinner_year = (Spinner) dialog.findViewById(R.id.spinner_year);
        Spinner spinner_month = (Spinner) dialog.findViewById(R.id.spinner_month);
        TextView sign_up_button = (TextView) dialog.findViewById(R.id.sign_up_button);
        TextView select_date_text=dialog.findViewById(R.id.select_date_text);
        select_date_text.setText(parseJasonLang.getJsonToString("select_month_year"));
        DateFormat dateFormat1 = new SimpleDateFormat("YYYY");
        Date date1 = new Date();

        ArrayAdapter adapter_year = new ArrayAdapter(getContext(),R.layout.custom_spinner,array_year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_year.setAdapter(adapter_year);
        for(int i = 0; i<array_year.length;i++){
            if(Integer.parseInt(dateFormat1.format(date1))==Integer.parseInt(array_year[i])){
                spinner_year.setSelection(i);
            }
        }

        spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                year=array_year[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        ArrayAdapter adapter_month = new ArrayAdapter(getContext(),R.layout.custom_spinner,months);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner_month.setAdapter(adapter_month);
        spinner_month.setSelection(Integer.parseInt(dateFormat.format(date))-1);
        spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month=months[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
        sign_up_button.setText(parseJasonLang.getJsonToString("done"));
        // if decline button is clicked, close the custom dialog
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                text_month_year.setText(month+" / "+year);
                for(int i = 0; i<months.length;i++){
                    if(month.equals(months[i])){
                       // month=months[i];
                        month1=String.valueOf(i+1);
                    }
                }
                if(MainActivity.isConnected) {

                    try {
                        if(loggedInUser.getLocal_language().equals("")) {
                            getService_category("en", dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
                        }else {
                            getService_category(loggedInUser.getLocal_language(), dateFormat2.format(date2) + "-" + month1 + "-" + year, 0);
                        }
                    }catch (Exception e){

                    }

                }

                dateModelArrayList.clear();



                if(month.equals("June")||month.equals("April")||month.equals("September")||month.equals("November")){
                    for(int i = 0;i<30;i++){

                        DateModel dateModel;

                        dateModel = new DateModel("1",String.valueOf(i+1));

                        dateModelArrayList.add(dateModel);


                    }
                }
                if(month.equals("February")){
                    for(int i = 0;i<28;i++){

                        DateModel dateModel;

                        dateModel = new DateModel("1",String.valueOf(i+1));

                        dateModelArrayList.add(dateModel);


                    }
                }


                if(month.equals("January")||month.equals("March")||month.equals("May")||month.equals("July")||month.equals("August")||month.equals("October")||month.equals("December")){
                    for(int i = 0;i<31;i++){

                        DateModel dateModel;

                        dateModel = new DateModel("1",String.valueOf(i+1));

                        dateModelArrayList.add(dateModel);


                    }
                }
                mAdapter.notifyDataSetChanged();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size() > 0) {
            Log.v("isCheckin", "not present +"+String.valueOf(db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size()));
            for (int j = 0; j < db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size(); j++) {
                String sch_id = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").get(j).getSch_id();
                String[] parts = db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_location().split(",");
                String client_lat = parts[0];
                String client_long = parts[1];
                postClockIn1(sch_id, db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_date(), db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_time(),
                        client_lat, client_long, 0);
            }


        }
        if (db.getAllCheckOut(loggedInUser.getLocal_user_id()).size() > 0) {
            for (int i = 0; i < db.getAllCheckOut(loggedInUser.getLocal_user_id()).size(); i++) {

                getClockOutApi1(db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getDmas_activity(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getClient_sign(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCaregiver_sign(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion1_yn(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion2_yn(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion3_yn(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion4_yn(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion1_note(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion2_note(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion3_note(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion4_note(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getScheduleId(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getClient_sign_name(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheckout_date(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheckout_time(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheck_out_latitude(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheck_out_longitude(),
                        db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getAdditional_comments(),0);
            }

        }
    }

    @Override
    public void onItemClickListener(int position, View view) {
        mAdapter.selectedItem();
        DateFormat dateFormat2 = new SimpleDateFormat("dd");
        Date date2 = new Date();
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        MainActivity.isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.d("Month",dateFormat2.format(date2));
        selected_date=dateModelArrayList.get(position).getDate();
        if(MainActivity.isConnected) {
            recyclerView.setVisibility(View.VISIBLE);
            llCheckInternet.setVisibility(View.GONE);
            //recyclerView.setVisibility(View.VISIBLE);
            ll1.setVisibility(View.GONE);
            if(loggedInUser.getLocal_language().equals("")){
                try {
                    getService_category("en", dateModelArrayList.get(position).getDate() + "-" + month1 + "-" + year, position);
                }catch (Exception e){

                }

            }else {
                try {

                    getService_category(loggedInUser.getLocal_language(),dateModelArrayList.get(position).getDate()+"-"+month1+"-"+year,position);

                }catch (Exception e){

                }

            }
            if (db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size() > 0) {
                Log.v("isCheckin", "not present +"+String.valueOf(db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size()));
                for (int j = 0; j < db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").size(); j++) {
                    String sch_id = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").get(j).getSch_id();
                    String[] parts = db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_location().split(",");
                    String client_lat = parts[0];
                    String client_long = parts[1];
                    postClockIn1(sch_id, db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_date(), db.getAllCheckIn(loggedInUser.getLocal_user_id(), sch_id).get(0).getCheckin_time(),
                            client_lat, client_long, 0);
                }


            }
            if (db.getAllCheckOut(loggedInUser.getLocal_user_id()).size() > 0) {
                for (int i = 0; i < db.getAllCheckOut(loggedInUser.getLocal_user_id()).size(); i++) {

                    getClockOutApi1(db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getDmas_activity(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getClient_sign(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCaregiver_sign(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion1_yn(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion2_yn(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion3_yn(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion4_yn(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion1_note(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion2_note(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion3_note(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getQuestion4_note(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getScheduleId(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getClient_sign_name(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheckout_date(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheckout_time(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheck_out_latitude(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getCheck_out_longitude(),
                            db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(i).getAdditional_comments(),position);
                }

            }

        }else {
            if(Integer.parseInt(dateFormat2.format(date2))==position+1) {
                scheduleModelArrayList=db.getAllImages(loggedInUser.getLocal_user_id());

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                //creating recyclerview adapter
                ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleModelArrayList);

                //setting adapter to recyclerview
                recyclerView.setAdapter(adapter);
                recyclerView.setVisibility(View.VISIBLE);
                llCheckInternet.setVisibility(View.GONE);
                //recyclerView.setVisibility(View.VISIBLE);
                ll1.setVisibility(View.GONE);
            }else {
                recyclerView.setVisibility(View.GONE);
                llCheckInternet.setVisibility(View.VISIBLE);
                //recyclerView.setVisibility(View.VISIBLE);
                ll1.setVisibility(View.GONE);
            }
        }
    }

    public void getCurrentDate(View view) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = "Current Date : " + mdformat.format(calendar.getTime());
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();
        Log.d("Month",dateFormat.format(date));
    }




    public void getService_category(final String lang, final String date, final int position){

        final DateFormat dateFormat2 = new SimpleDateFormat("dd");
        final Date date2 = new Date();
        Log.d("Month",dateFormat2.format(date2));
        scheduleModelArrayList.clear();
      //  category_name_list.clear();
        if(date.equals(dateFormat2.format(date2)+"-"+month1+"-"+year)) {
            Log.d("Month","clear");
            db.clearSCHEDULELIST();

        }
        dialog= new CustomProgressDialog();
        dialog.startProgress(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl.scheduleList,
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




                            JSONArray heroArray = jsonObject.getJSONArray("result");
                            for(int i =0;i<heroArray.length();i++) {
                                JSONObject jsonObject1 = heroArray.getJSONObject(i);

                                String id = jsonObject1.getString("id");
                                String firstName = jsonObject1.getString("firstName");
                                String lastName = jsonObject1.getString("lastName");
                                String startTime = jsonObject1.getString("startTime");
                                String endTime = jsonObject1.getString("endTime");
                                String address = jsonObject1.getString("address");
                                String mobileNo = jsonObject1.getString("mobileNo");
                                String unitRate = jsonObject1.getString("unitRate");
                                String clientId = jsonObject1.getString("clientId");
                                String latitude = jsonObject1.getString("latitude");
                                String longitude = jsonObject1.getString("longitude");
                                String sechudule_status = jsonObject1.getString("sechudule_status");
                                String profileImg = jsonObject1.getString("profileImg");
                                String caregiverId = jsonObject1.getString("caregiverId");
                                String additional_note = jsonObject1.getString("additional_note");


                                ScheduleModel scheduleModel = new ScheduleModel(clientId, unitRate, id, profileImg, firstName + " " + lastName, sechudule_status, address, startTime, endTime, latitude, longitude, mobileNo, caregiverId, additional_note);








                                try {
                                    if (date.equals(dateFormat2.format(date2) + "-" + month1 + "-" + year)) {
                                        if(db.getAllCheckOut(loggedInUser.getLocal_user_id()).size()>0){
                                            if (!id.equals(db.getAllCheckOut(loggedInUser.getLocal_user_id()).get(0).getScheduleId()) || !id.equals(db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(), "1").get(0).getSch_id())) {
                                                db.addScheduleList(scheduleModel);
                                                scheduleModelArrayList.add(scheduleModel);
                                                Log.v("stateArray", id);
                                                getService_categorySelectedList(lang, id);
                                            }
                                        }else {
                                            if (date.equals(dateFormat2.format(date2) + "-" + month1 + "-" + year)) {
                                                db.addScheduleList(scheduleModel);
                                                getService_categorySelectedList(lang, id);
                                                scheduleModelArrayList.add(scheduleModel);
                                            }
                                        }


                                    }else {
                                        scheduleModelArrayList.add(scheduleModel);
                                    }

                                } catch (Exception e) {


                                    if (date.equals(dateFormat2.format(date2) + "-" + month1 + "-" + year)) {
                                        db.addScheduleList(scheduleModel);
                                        scheduleModelArrayList.add(scheduleModel);
                                        getService_categorySelectedList(lang, id);
                                    }
                                }


                            }
                           // scheduleModelArrayList=db.getAllImages();

                            if(!scheduleModelArrayList.isEmpty()) {
                                recyclerView.setVisibility(View.VISIBLE);
                                ll1.setVisibility(View.GONE);
                                llCheckInternet.setVisibility(View.GONE);
                                Log.v("stateArray", "not empty");
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                //creating recyclerview adapter
                                ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleModelArrayList);

                                //setting adapter to recyclerview
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();

                              //  adapter.notifyDataSetChanged();


                            }
                            else {
                                recyclerView.setVisibility(View.GONE);
                                ll1.setVisibility(View.VISIBLE);
                                llCheckInternet.setVisibility(View.GONE);
                                Log.v("stateArray", " empty");
                            }
                            Log.v("stateArray", String.valueOf(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.stopProgress();
                          recyclerView.setVisibility(View.GONE);
                          ll1.setVisibility(View.VISIBLE);
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
                        ProjectUtil.showErrorResponse(getContext(), error);
                        if(Integer.parseInt(dateFormat2.format(date2))==position+1) {
                            scheduleModelArrayList=db.getAllImages(loggedInUser.getLocal_user_id());

                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            //creating recyclerview adapter
                            ScheduleAdapter adapter = new ScheduleAdapter(getContext(), scheduleModelArrayList);

                            //setting adapter to recyclerview
                            recyclerView.setAdapter(adapter);
                            recyclerView.setVisibility(View.VISIBLE);
                            llCheckInternet.setVisibility(View.GONE);
                            //recyclerView.setVisibility(View.VISIBLE);
                            ll1.setVisibility(View.GONE);
                        }else {
                            recyclerView.setVisibility(View.GONE);
                            llCheckInternet.setVisibility(View.VISIBLE);
                            //recyclerView.setVisibility(View.VISIBLE);
                            ll1.setVisibility(View.GONE);
                        }
                        Log.v("stateArray",String.valueOf(error));
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                    params.put("token", loggedInUser.getLocal_session_toekn());
                    params.put("userId",loggedInUser.getLocal_user_id());
                    params.put("date",date);
                    params.put("agencyId", loggedInUser.getAgency_id());
                    params.put("lang", lang);
                    //params.put("token", MyPreferences.getInstance(getApplicationContext()).getSESSION_TOKEN());


//                params.put("deviceId", Settings.Secure.getString(
//                        getContentResolver(), Settings.Secure.ANDROID_ID));
//                params.put("deviceType", "2");

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
        ProjectUtil.setRequest(getContext(), stringRequest);
    }


    public void getService_categorySelectedList(String lang,String id1){
        db.clearSELECTEDLIST();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ApiUrl.avail_acitivity+"/"+id1+"/"+lang,
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




                            JSONArray heroArray = jsonObject.getJSONArray("result");
                            // JSONArray heroArray = new JSONArray(response);
                            Log.v("stateArray",String.valueOf(response));

                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject heroObject = heroArray.getJSONObject(i);
                                String id=heroObject.getString("code_value");
                                String code_desc=heroObject.getString("code_desc");
                                ServiceModel serviceModel = new ServiceModel(id,code_desc,id1,"");
                                db.addSelectedList(serviceModel);
                               // Log.v("stateArray",db.getAllSelected(id1).get(i).getId());
                            }



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
                        ProjectUtil.showErrorResponse(getContext(), error);
                        Log.v("stateArray",String.valueOf(error));
                    }
                });

        //creating a request queue
        ProjectUtil.setRequest(getContext(), stringRequest);
    }



    public void postClockIn1( String sch_id,final String startDate,String startTime,String client_lat,String client_long ,final int position){




        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUrl.checkin_start,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            //we have the array named hero inside the object
                            //so here we are getting that json array
                            JSONObject jsonObject = new JSONObject(response);


                            Log.v("stateArray", String.valueOf(response));

                            //   Log.v("direction",String.valueOf(direction));
                            //db.addCheckInDetails(new CheckInModel(id, loggedInUser.getLocal_agency(), client_id, loggedInUser.getLocal_user_id(), startDate, startTime, String.valueOf(latitude) + "," + String.valueOf(longitude)));
                            // text_time.setText(parseJasonLang.getJsonToString("clock_out"));

//                            String clockId=jsonObject.getString("clockId");
                            // String status=jsonObject.getString("status");
                            // String message=jsonObject.getString("message");




                            //   String startTime=jsonObject1.getString("startTime");
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

                        DateFormat dateFormat2 = new SimpleDateFormat("dd");
                        Date date2 = new Date();
                        Log.d("Month",dateFormat2.format(date2));
                        ProjectUtil.showErrorResponse(getContext(), error);

                        Log.v("stateArray",String.valueOf(error));
                    }
                }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("token", loggedInUser.getLocal_session_toekn());
                params.put("userId",loggedInUser.getLocal_user_id());
                params.put("latitude",client_lat);
                params.put("scheduleId",sch_id);
                params.put("checkinDate",startDate);

                params.put("agencyId", loggedInUser.getAgency_id());
                params.put("checkinTime ", startTime);
                params.put("longitude", client_long);


//                params.put("deviceId", Settings.Secure.getString(
//                        getContentResolver(), Settings.Secure.ANDROID_ID));
//                params.put("deviceType", "2");

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
        ProjectUtil.setRequest(getContext(), stringRequest);
    }

    public void getClockOutApi1(String dmas_activity,String client_sign,String caregiver_sign, String question1_yn ,String question2_yn,String question3_yn ,String question4_yn,String question1_note,
                                String question2_note ,String question3_note,String question4_note,String sch_id,String client_signName,
                                final String startDate,String startTime,String client_lat,String client_long ,String notes,int position){




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

                            // Log.v("direction",String.valueOf(direction));
                            //db.addCheckInDetails(new CheckInModel(id, loggedInUser.getLocal_agency(), client_id, loggedInUser.getLocal_user_id(), startDate, startTime, String.valueOf(latitude) + "," + String.valueOf(longitude)));


                            String status=jsonObject.getString("status");
                            String message=jsonObject.getString("message");

                            String action;
                            if(loggedInUser.getLocal_language().equals("")){
                                try {
                                    getService_category("en", dateModelArrayList.get(position).getDate() + "-" + month1 + "-" + year, position);
                                }catch (Exception e){

                                }

                            }else {
                                try {

                                    getService_category(loggedInUser.getLocal_language(),dateModelArrayList.get(position).getDate()+"-"+month1+"-"+year,position);

                                }catch (Exception e){

                                }

                            }
                            String billId =jsonObject.getString("billId");

                         //   db.clearSELECTEDLIST();
                            db.clearSelectedByUser();
                            db.clearCheckOut();
                            db.deleteCheckIn(sch_id);


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

                            Log.v("stateArray_clock", String.valueOf(response));
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

                        DateFormat dateFormat2 = new SimpleDateFormat("dd");
                        Date date2 = new Date();
                        Log.d("Month",dateFormat2.format(date2));
                        ProjectUtil.showErrorResponse(getContext(), error);

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
                params.put("additional_comments",notes );
                params.put("client_sign_name", client_signName);
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
        ProjectUtil.setRequest(getContext(), stringRequest);
    }


}
