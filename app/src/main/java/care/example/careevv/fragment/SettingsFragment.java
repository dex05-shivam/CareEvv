package care.example.careevv.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import care.example.careevv.R;
import care.example.careevv.activity.ChangeLanguageActivity;
import care.example.careevv.activity.ChangePassword;
import care.example.careevv.activity.ScheduleActivity;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.DatabaseHandler;
import care.example.careevv.util.MyToast;
import care.example.careevv.util.ParseJasonLang;

import care.example.careevv.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    View x;
    LinearLayout password_layout,lang_layout;
    ParseJasonLang parseJasonLang;
    TextView lang_text,pass_text;
    DatabaseHandler db ;
    LoggedInUser loggedInuser;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        x=inflater.inflate(R.layout.fragment_settings, container, false);
        password_layout=x.findViewById(R.id.password_layout);
        lang_layout=x.findViewById(R.id.lang_layout);
        pass_text=x.findViewById(R.id.pass_text);
        db = new DatabaseHandler(getContext());
        loggedInuser = new LoggedInUser(Objects.requireNonNull(getContext()));
        lang_text=x.findViewById(R.id.lang_text);
        parseJasonLang = new ParseJasonLang(getContext());
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        MainActivity.isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        lang_layout.setOnClickListener(v -> {
            ConnectivityManager cm12 =
                    (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork12 = cm12.getActiveNetworkInfo();
            MainActivity.isConnected = activeNetwork12 != null &&
                    activeNetwork12.isConnectedOrConnecting();
            if(MainActivity.isConnected){
                Intent intent = new Intent(getContext(), ChangeLanguageActivity.class);
                startActivity(intent);
            }else {
                MyToast.showToast(getContext(),parseJasonLang.getJsonToString("please_connect_internet"));
            }

        });

      //  if()
        MainActivity.main_title.setText(parseJasonLang.getJsonToString("Setting"));
        if(db.getAllCompleteCheckIn(loggedInuser.getLocal_user_id(),"0").size()>0) {
            ScheduleActivity.title.setText(parseJasonLang.getJsonToString("Setting"));
        }
        password_layout.setOnClickListener(v -> {
            ConnectivityManager cm1 =
                    (ConnectivityManager) Objects.requireNonNull(getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork1 = cm1.getActiveNetworkInfo();
            MainActivity.isConnected = activeNetwork1 != null &&
                    activeNetwork1.isConnectedOrConnecting();
            if(MainActivity.isConnected) {
                Intent intent = new Intent(getContext(), ChangePassword.class);
                startActivity(intent);
            }else {
                MyToast.showToast(getContext(),parseJasonLang.getJsonToString("please_connect_internet"));
            }
        });
        pass_text.setText(parseJasonLang.getJsonToString("change_password"));
        lang_text.setText(parseJasonLang.getJsonToString("change_language"));
        return x;
    }

}
