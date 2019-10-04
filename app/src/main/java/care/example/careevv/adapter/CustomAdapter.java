package care.example.careevv.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import care.example.careevv.R;
import care.example.careevv.activity.ChangeLanguageActivity;
import care.example.careevv.activity.ChangePassword;

public class CustomAdapter extends BaseAdapter {
    Context context;
    int flags[];
    String[] countryNames;
    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, int[] flags, String[] countryNames) {
        this.context = applicationContext;
        this.flags = flags;
        this.countryNames = countryNames;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_sppiner2, null);
        ImageView icon = (ImageView) view.findViewById(R.id.imageView);
        TextView names = (TextView) view.findViewById(R.id.textView);
        icon.setImageResource(flags[i]);
        names.setText(countryNames[i]);

        LinearLayout layout_main= view.findViewById(R.id.layout_main);
        layout_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==0){
                    Intent intent = new Intent(context, ChangeLanguageActivity.class);
                    Toast.makeText(context, countryNames[i], Toast.LENGTH_LONG).show();
                    context.startActivity(intent);
                }
                Toast.makeText(context, countryNames[i], Toast.LENGTH_LONG).show();
                if(i==1){
                    Toast.makeText(context, countryNames[i], Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, ChangePassword.class);
                    context.startActivity(intent);
                }
            }
        });


        return view;
    }
}
