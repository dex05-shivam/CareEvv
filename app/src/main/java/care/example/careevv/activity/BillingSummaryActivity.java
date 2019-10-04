package care.example.careevv.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import care.example.careevv.MainActivity;
import care.example.careevv.R;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.ParseJasonLang;

public class BillingSummaryActivity extends AppCompatActivity {

    TextView tv_date,tv_start_time,tv_end_time,tv_total_time,tv_total_unit,title,date_title,start_time_title,end_time_title,total_time_title,
            total_unit_title,close_text,billing_text;

    String date,checkinTime,checkoutTime,total_time,total_unit,sch_id;
    ParseJasonLang parseJasonLang;
    DatabaseHandler db ;

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
        close_text=findViewById(R.id.close_text);
        billing_text=findViewById(R.id.billing_text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_summary);
        db = new DatabaseHandler(BillingSummaryActivity.this);
        Bundle bundle = getIntent().getExtras();
        parseJasonLang=new ParseJasonLang(BillingSummaryActivity.this);

        if(bundle!=null){
            date=bundle.getString("date");
            checkinTime=bundle.getString("checkinTime");
            checkoutTime=bundle.getString("checkoutTime");
            total_time=bundle.getString("total_time");
            total_unit=bundle.getString("total_unit");
            sch_id=bundle.getString("sch_id");
        }

        ImageView login_back=findViewById(R.id.login_back);
        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                db.updateCheckInColumn(sch_id);
                db.clearSelectedByUser();
                //db.clearSELECTEDLIST();
                db.deleteSchBySchId(sch_id);
                startActivity(intent);
                finish();
            }
        });


        LinearLayout sign_button=findViewById(R.id.sign_button);
        getId();
        sign_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                db.clearSelectedByUser();
                db.deleteSchBySchId(sch_id);
                db.updateCheckInColumn(sch_id);
               // db.clearSELECTEDLIST();
                startActivity(intent);
                finish();
            }
        });
        close_text.setText(parseJasonLang.getJsonToString("close"));
        title.setText(parseJasonLang.getJsonToString("Billing"));
        billing_text.setText(parseJasonLang.getJsonToString("Billing"));

        date_title.setText(parseJasonLang.getJsonToString("date"));
        start_time_title.setText(parseJasonLang.getJsonToString("started_time"));
        end_time_title.setText(parseJasonLang.getJsonToString("end_time"));
        total_time_title.setText(parseJasonLang.getJsonToString("total time"));
        total_unit_title.setText(parseJasonLang.getJsonToString("total_unit"));

        tv_date.setText(date);
        tv_start_time.setText(checkinTime);
        tv_end_time.setText(checkoutTime);
        tv_total_time.setText(total_time);
        tv_total_unit.setText(total_unit);

    }

    @Override
    public void onBackPressed() {
        String action;
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        db.clearSelectedByUser();
        db.deleteSchBySchId(sch_id);
        db.updateCheckInColumn(sch_id);
       // db.clearSELECTEDLIST();
        startActivity(intent);
        finish();
    }
}
