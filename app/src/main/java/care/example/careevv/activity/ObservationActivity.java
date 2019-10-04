package care.example.careevv.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import care.example.careevv.R;
import care.example.careevv.constants.ApiUrl;
import care.example.careevv.model.CheckInModel;
import care.example.careevv.model.CheckOutModel;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.CustomProgressDialog;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.LocationTrack;
import care.example.careevv.util.MyLog;
import care.example.careevv.util.MyToast;
import care.example.careevv.util.ParseJasonLang;
import care.example.careevv.util.ProjectUtil;

import care.example.careevv.MainActivity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class ObservationActivity extends AppCompatActivity {
    RadioGroup radioGroup,radioGroup1,radioGroup2,radioGroup3;
    ImageView login_back;
    AppCompatRadioButton radioyes,radiono,radioyes1,radioyes2,radioyes3,radiono1,radiono2,radiono3;
    EditText et_ques1,et_ques2,et_ques3,et_ques4,et_client_name,et_notes,et_caregiver_name;
    LinearLayout sign_button;
    private boolean isValid = false;
    RelativeLayout sign_rl,sign_rl1;
    ImageView sign_img,reset_img,sign_img1,reset_img1;
    SignatureView signature_view,signature_view1;
    Bitmap bitmap=null,bitmap1=null;
    String name="";
    long diffHours = 0;
    long diffMinutes;
    LoggedInUser loggedInUser;
    ParseJasonLang parseJasonLang;
    DatabaseHandler db;
    String dmas_activity="";
    boolean sign_flag=false,sign_flag1=false;

    long total_unit,total_unit_reminder;
    private CustomProgressDialog dialog;


    String sch_id="",client_id="",checkinTime="",checkInDate="",unitRate;
    CheckInModel checkInModel;
    String ques1="",ques2="",ques3="",ques4="";
    double latitude,longitude;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack appLocationService;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    String client_lat,client_long;
    TextView quest_ins_text,ques_txt4,ques_txt2,careguver_name_text,ques_txt3,ques_txt1,title,add_comment_text,client_sign_text,client_name_text,care_giver_sign,clockOut_text;
    void getId(){
        radioGroup=findViewById(R.id.radioGroup);
        radioGroup1=findViewById(R.id.radioGroup1);
        radioGroup2=findViewById(R.id.radioGroup2);
        radioGroup3=findViewById(R.id.radioGroup3);

        login_back=findViewById(R.id.login_back);
        et_client_name=findViewById(R.id.et_client_name);

        radioyes=findViewById(R.id.radioyes);
        radioyes1=findViewById(R.id.radioyes1);
        radioyes2=findViewById(R.id.radioyes2);
        radioyes3=findViewById(R.id.radioyes3);
        radiono=findViewById(R.id.radiono);
        radiono1=findViewById(R.id.radiono1);
        radiono2=findViewById(R.id.radiono2);
        radiono3=findViewById(R.id.radiono3);

        et_caregiver_name=findViewById(R.id.et_caregiver_name);
        careguver_name_text=findViewById(R.id.careguver_name_text);

        et_ques1=findViewById(R.id.et_ques1);
        et_ques2=findViewById(R.id.et_ques2);
        et_ques3=findViewById(R.id.et_ques3);
        et_ques4=findViewById(R.id.et_ques4);
        add_comment_text=findViewById(R.id.add_comment_text);
        client_sign_text=findViewById(R.id.client_sign_text);
        client_name_text=findViewById(R.id.client_name_text);
        care_giver_sign=findViewById(R.id.care_giver_sign);
        clockOut_text=findViewById(R.id.clockOut_text);


        quest_ins_text=findViewById(R.id.quest_ins_text);

        ques_txt1=findViewById(R.id.ques_txt1);
        ques_txt2=findViewById(R.id.ques_txt2);
        ques_txt3=findViewById(R.id.ques_txt3);
        ques_txt4=findViewById(R.id.ques_txt4);

        title=findViewById(R.id.title);

        sign_button=findViewById(R.id.sign_button);

        sign_rl=findViewById(R.id.sign_rl);
        sign_rl1=findViewById(R.id.sign_rl1);
        reset_img=findViewById(R.id.reset_img);
        sign_img=findViewById(R.id.sign_img);
        sign_img1=findViewById(R.id.sign_img1);
        reset_img1=findViewById(R.id.reset_img1);
        et_notes=findViewById(R.id.et_notes);
    }

    @Override
    public void onBackPressed() {
        loggedInUser.setDmas_activity("");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            name=bundle.getString("name");
            dmas_activity=bundle.getString("dmas_activity");

           // intent.putExtra("dmas_activity",dmas_activity);

        }

        getId();

        db = new DatabaseHandler(ObservationActivity.this);
        parseJasonLang= new ParseJasonLang(ObservationActivity.this);
        loggedInUser= new LoggedInUser(ObservationActivity.this);

        sch_id = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getSch_id();
        checkinTime = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getCheckin_time();
        checkInDate = db.getAllCompleteCheckIn(loggedInUser.getLocal_user_id(),"0").get(0).getCheckin_date();
        unitRate = db.getSchedule(sch_id).getUnitRate();

        client_lat=db.getSchedule(sch_id).getLat();
        client_long=db.getSchedule(sch_id).getLongi();

        title.setText(parseJasonLang.getJsonToString("observations"));

        add_comment_text.setText(parseJasonLang.getJsonToString("add_comment"));
        et_notes.setHint(parseJasonLang.getJsonToString("add_cmnt_text"));
        client_sign_text.setText(parseJasonLang.getJsonToString("client_sign"));
        client_name_text.setText(parseJasonLang.getJsonToString("client_name"));
        care_giver_sign.setText(parseJasonLang.getJsonToString("caregiver_signature"));
        clockOut_text.setText(parseJasonLang.getJsonToString("clock_out"));
        if(db.getAllQuestions().size()>0) {
            ques_txt4.setText(db.getAllQuestions().get(3).getQuestions());
            ques_txt3.setText(db.getAllQuestions().get(2).getQuestions());
            ques_txt2.setText(db.getAllQuestions().get(1).getQuestions());
            ques_txt1.setText(db.getAllQuestions().get(0).getQuestions());

        }else {
            Toast.makeText(getApplicationContext(),"Kindly Restart Application In Internet Mode",Toast.LENGTH_LONG).show();
        }

        radioGroup.clearCheck();
        radioGroup1.clearCheck();
        radioGroup2.clearCheck();
        radioGroup3.clearCheck();

        et_client_name.setText(name);

        appLocationService = new LocationTrack(
                ObservationActivity.this);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                   // Toast.makeText(ObservationActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                   if(rb.getId()==radioyes.getId()){
                       et_ques1.setVisibility(View.VISIBLE);
                       ques1="1";
                   }else {
                       et_ques1.setVisibility(View.GONE);
                       ques1="0";
                   }
                }

            }
        });

        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loggedInUser.setDmas_activity("");
                finish();
            }
        });

        radioyes.setText(parseJasonLang.getJsonToString("yes"));
        radioyes1.setText(parseJasonLang.getJsonToString("yes"));
        radioyes2.setText(parseJasonLang.getJsonToString("yes"));
        radioyes3.setText(parseJasonLang.getJsonToString("yes"));

        radiono.setText(parseJasonLang.getJsonToString("no"));
        radiono1.setText(parseJasonLang.getJsonToString("no"));
        radiono2.setText(parseJasonLang.getJsonToString("no"));
        radiono3.setText(parseJasonLang.getJsonToString("no"));



        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                   // Toast.makeText(ObservationActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getId()==radioyes1.getId()){
                        et_ques2.setVisibility(View.VISIBLE);
                        ques2="1";
                    }else {
                        et_ques2.setVisibility(View.GONE);
                        ques2="0";
                    }
                }

            }
        });

        quest_ins_text.setText(parseJasonLang.getJsonToString("quest_ins_text"));
        et_caregiver_name.setText(loggedInUser.getLocal_name());
        et_caregiver_name.setVisibility(View.GONE);
        careguver_name_text.setText(parseJasonLang.getJsonToString("caregiver_name")+": "+ loggedInUser.getLocal_name());
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                   // Toast.makeText(ObservationActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getId()==radioyes2.getId()){
                        et_ques3.setVisibility(View.VISIBLE);
                        ques3="1";
                    }else {
                        ques3="0";
                        et_ques3.setVisibility(View.GONE);
                    }
                }

            }
        });

        radioGroup3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                 //   Toast.makeText(ObservationActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getId()==radioyes3.getId()){
                        et_ques4.setVisibility(View.VISIBLE);
                        ques4="1";
                    }else {
                        et_ques4.setVisibility(View.GONE);
                        ques4="0";
                    }
                }

            }
        });









        getCareGiverSignature();
        getClientSignature();


        sign_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String action;
                String ques_text1 = et_ques1.getText().toString().trim();
                String ques_text2 = et_ques2.getText().toString().trim();
                String ques_text3 = et_ques3.getText().toString().trim();
                String ques_text4 = et_ques4.getText().toString().trim();
                ConnectivityManager cm =
                        (ConnectivityManager) ObservationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

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


                if (checkValidation()) {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ObservationActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("CareEvv");
                    alertDialog.setCancelable(false);

                    // Setting Dialog Message
                    alertDialog.setMessage(parseJasonLang.getJsonToString("clockout_message"));


                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.korevv);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton(parseJasonLang.getJsonToString("yes"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            if (MainActivity.isConnected) {
                                getClockOutApi(dmas_activity,getBase64(bitmap), getBase64(bitmap1), ques1, ques2, ques3, ques4,
                                        et_ques1.getText().toString().trim(), et_ques2.getText().toString().trim(),
                                        et_ques3.getText().toString().trim(), et_ques4.getText().toString().trim(), formattedDate, formattedtime, client_lat, client_long, 0);
                            } else {
                                db.addCheckOutDetails(new CheckOutModel(sch_id, loggedInUser.getLocal_user_id(), String.valueOf(latitude),
                                        String.valueOf(longitude), formattedDate, formattedtime, ques1, ques2, ques3, ques4, et_ques1.getText().toString().trim(),
                                        et_ques2.getText().toString().trim(), et_ques3.getText().toString().trim(),
                                        et_ques4.getText().toString().trim(), getBase64(bitmap), et_client_name.getText().toString().trim(),
                                        getBase64(bitmap1), et_notes.getText().toString().trim(),dmas_activity));
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


                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton(parseJasonLang.getJsonToString("no"), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            //  Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();


                }
            }
        });

    }

    void getClientSignature(){


        reset_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_img.setImageBitmap(null);
                sign_flag=false;
                bitmap=null;
            }
        });

        sign_rl.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                sign_flag=false;
                final Dialog dialog = new Dialog(ObservationActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.signature_dialog);
                // Set dialog title
                dialog.setCancelable(false);
                ImageView login_back_dialog = dialog.findViewById(R.id.login_back_dialog);
                login_back_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                signature_view=dialog.findViewById(R.id.signature_view);
                TextView done= dialog.findViewById(R.id.done);

                TextView clear= dialog.findViewById(R.id.clear);
                TextView sign_dialog_title= dialog.findViewById(R.id.sign_dialog_title);
                sign_dialog_title.setText(parseJasonLang.getJsonToString("client_sign"));

                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signature_view.clearCanvas();
                        sign_flag=false;
                        bitmap=null;
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signature_view.setEnableSignature(true);

                        bitmap = signature_view.getSignatureBitmap();

                        sign_img.setImageBitmap(bitmap);

                        if(getBase64(bitmap).equals(ApiUrl.blank_image)) {
                            Log.v("blank_bitmap", getBase64(bitmap).substring(0, 629).trim());
                        }
                        //MyPreferences.getInstance(getApplicationContext()).setUSER_CLASS_ID(getBase64(bitmap));
                        ///  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        dialog.dismiss();
                    }
                });
                signature_view.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sign_flag=true;
                        return false;
                    }
                });
                done.setText(parseJasonLang.getJsonToString("done"));
                clear.setText(parseJasonLang.getJsonToString("reset"));



                dialog.show();


            }
        });
    }

    void getCareGiverSignature(){

        reset_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_img1.setImageBitmap(null);
                bitmap1=null;
                sign_flag1=false;
            }
        });


        sign_rl1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                sign_flag1=false;

                final Dialog dialog = new Dialog(ObservationActivity.this);
                // Include dialog.xml file
                dialog.setContentView(R.layout.signature_dialog);
                // Set dialog title
                dialog.setCancelable(false);
                ImageView login_back_dialog = dialog.findViewById(R.id.login_back_dialog);
                login_back_dialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                signature_view1=dialog.findViewById(R.id.signature_view);
                TextView done= dialog.findViewById(R.id.done);
                TextView clear= dialog.findViewById(R.id.clear);

                TextView sign_dialog_title= dialog.findViewById(R.id.sign_dialog_title);
                sign_dialog_title.setText(parseJasonLang.getJsonToString("caregiver_sign"));
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signature_view1.clearCanvas();
                        bitmap1=null;
                        sign_flag1=false;
                    }
                });
                signature_view1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        sign_flag1=true;
                        return false;
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bitmap1 = signature_view1.getSignatureBitmap();
                        sign_img1.setImageBitmap(bitmap1);
                        ///  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        dialog.dismiss();
                    }
                });

                done.setText(parseJasonLang.getJsonToString("done"));
                clear.setText(parseJasonLang.getJsonToString("reset"));



                dialog.show();


            }
        });
    }


    public void getClockOutApi(final String dmas_activity,final String client_sign, final String caregiver_sign, final String question1_yn , final String question2_yn, final String question3_yn , final String question4_yn, final String question1_note,
                               final String question2_note , final String question3_note, final String question4_note,
                               final String startDate, final String startTime, final String client_lat, final String client_long , final int position){


        dialog= new CustomProgressDialog();
        dialog.startProgress(ObservationActivity.this);

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
                        ProjectUtil.showErrorResponse(ObservationActivity.this, error);

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
                params.put("additional_comments", et_notes.getText().toString().trim());
                params.put("client_sign_name", et_client_name.getText().toString().trim());
                params.put("caregiver_sign", caregiver_sign);
                params.put("dmas_activity",dmas_activity);

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
        ProjectUtil.setRequest(ObservationActivity.this, stringRequest);
    }


    String getBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ObservationActivity.this)
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



    private boolean checkValidation() {
        isValid = true;
        if (ques1.equals("")) {
            isValid = false;
            //et_email.requestFocus();

            MyToast.showToast(this,
                    parseJasonLang.getJsonToString("please_ans_ques1"));

        } else if (ques2.equals("")) {
            isValid = false;

            MyToast.showToast(this,
                    parseJasonLang.getJsonToString("please_ans_ques2"));

        } else if (ques3.equals("")) {
            isValid = false;

            MyToast.showToast(this, parseJasonLang.getJsonToString("please_ans_ques3"));

        } else if (ques4.equals("")) {
            isValid = false;

            MyToast.showToast(this,
                    parseJasonLang.getJsonToString("please_ans_ques4"));
        }else if (bitmap==null||sign_flag==false) {
            isValid = false;
            sign_flag=false;
            MyToast.showToast(this, parseJasonLang.getJsonToString("please_complete_sign"));

        }else if (bitmap1==null||sign_flag1==false) {
            sign_flag1=false;
            isValid = false;
            MyToast.showToast(this, parseJasonLang.getJsonToString("please_complete_caregiver_sign"));

        }
        else if (et_client_name.getText().toString().trim().equals("")) {
            isValid = false;
            MyToast.showToast(this, parseJasonLang.getJsonToString("please_enter_client_name"));

        }
        else if (!ques1.equals("0")&&et_ques1.getText().toString().trim().equals("")) {
//            if(et_ques1.getText().toString().trim().equals("")){
                isValid = false;
                MyToast.showToast(this, parseJasonLang.getJsonToString("ques1_comments"));
            //}


        }
        else if (!ques2.equals("0")&&et_ques2.getText().toString().trim().equals("")) {

                isValid = false;
                MyToast.showToast(this, parseJasonLang.getJsonToString("ques2_comments"));



        }

        else if (!ques3.equals("0")&&et_ques3.getText().toString().trim().equals("")) {
//            if(et_ques3.getText().toString().trim().equals("")){
                isValid = false;
                MyToast.showToast(this, parseJasonLang.getJsonToString("ques3_comments"));
          //  }


        }

        else if (!ques4.equals("0")&&et_ques4.getText().toString().trim().equals("")) {
//            if(et_ques4.getText().toString().trim().equals("")){
                isValid = false;
                MyToast.showToast(this, parseJasonLang.getJsonToString("ques4_comments"));
           // }


        }
        return isValid;
    }


}
