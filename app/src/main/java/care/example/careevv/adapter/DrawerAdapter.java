package care.example.careevv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import care.example.careevv.MainActivity;
import care.example.careevv.model.DrawerItem;
import care.example.careevv.preferences.MyPreferences;
import care.example.careevv.util.MyToast;
import de.hdodenhof.circleimageview.CircleImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import care.example.careevv.R;
import care.example.careevv.activity.ChangeLanguageActivity;
import care.example.careevv.activity.ChangePassword;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.ParseJasonLang;

/**
 * Created by darshanz on 7/6/15.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    public final static int TYPE_HEADER = 0;
    public final static int TYPE_MENU = 1;
    public final static int TYPE_FOOTER = 2;
    Context context;
    LoggedInUser loggedInUser;

    int flags[] = { R.drawable.translation, R.drawable.padlock};
    ParseJasonLang parseJasonLang;


  //  String[] country = { "Settings", "Change Language", "Change Password"};


    private ArrayList<DrawerItem> drawerMenuList;

    private OnItemSelecteListener mListener;

    public DrawerAdapter(ArrayList<DrawerItem> drawerMenuList, Context context) {
        this.drawerMenuList = drawerMenuList;
        this.context=context;
        loggedInUser=new LoggedInUser(context);
        parseJasonLang = new ParseJasonLang(context);
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_HEADER){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_drawer_header, parent, false);
            return new DrawerViewHolder(view, viewType);
        }
        if(viewType == TYPE_FOOTER){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_footer, parent, false);
            return new DrawerViewHolder(view, viewType);

        }
        if(viewType == TYPE_MENU){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item, parent, false);
            return new DrawerViewHolder(view, viewType);
        }else {
            return null;
        }



    }


    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {

        if(position == 0) {
            if(MyPreferences.getInstance(context).getUSERID().equals("")){
                holder.header_email.setText(loggedInUser.getLocal_email());
                holder.headerText.setText(loggedInUser.getLocal_name());

                holder.settings_text.setText(parseJasonLang.getJsonToString("Setting"));
                String[] countryNames={parseJasonLang.getJsonToString("change_language"),parseJasonLang.getJsonToString("change_password")};

                holder.settings_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        holder.settings_text.setVisibility(View.VISIBLE);
//                        holder.settings_spinner.setVisibility(View.VISIBLE);
                        ConnectivityManager cm =
                                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                        MainActivity.isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();
                        if(MainActivity.isConnected) {
//                            if (holder.settings_spinner.getSelectedItem() == null) { // user selected nothing...
//                                holder.settings_spinner.performClick();
//                            }

                            final AppCompatDialog dialog = new AppCompatDialog(context);
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_settings);
                            dialog.getWindow().setGravity(Gravity.TOP);
                            // Set dialog title


                            // set values for custom dialog components - text, image and button
                            TextView select_date_text = (TextView) dialog.findViewById(R.id.select_date_text);
                            ImageView login_back = (ImageView) dialog.findViewById(R.id.login_back);
                            LinearLayout password_layout =  dialog.findViewById(R.id.password_layout);
                            TextView pass_text =  dialog.findViewById(R.id.pass_text);
                            LinearLayout lang_layout = dialog.findViewById(R.id.lang_layout);
                            TextView lang_text =  dialog.findViewById(R.id.lang_text);
                            TextView sign_up_button =  dialog.findViewById(R.id.sign_up_button);

                            select_date_text.setText(parseJasonLang.getJsonToString("Setting"));
                            login_back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            password_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, ChangePassword.class);
                                    Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                                    context.startActivity(intent);
                                }
                            });
                            pass_text.setText(parseJasonLang.getJsonToString("change_password"));

                            lang_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, ChangeLanguageActivity.class);
                                    Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                                    context.startActivity(intent);
                                }
                            });
                            lang_text.setText(parseJasonLang.getJsonToString("change_language"));


                            dialog.show();



                        }else {
                            MyToast.showToast(context,parseJasonLang.getJsonToString("please_connect_internet"));
                        }
                    }
                });

                CustomAdapter customAdapter=new CustomAdapter(context,flags,countryNames);
                holder.settings_spinner.setAdapter(customAdapter);
                //Setting the ArrayAdapter data on the Spinner

                holder.settings_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(position==0){
                            Intent intent = new Intent(context, ChangeLanguageActivity.class);
                            context.startActivity(intent);
                          //  Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                        if(position==1){
                            Intent intent = new Intent(context, ChangePassword.class);
                          //  Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                            context.startActivity(intent);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if(loggedInUser.getLocal_image().equals("")&&MyPreferences.getInstance(context).getIMAGE().equals("")){
                    Glide.with(context)
                        .load(loggedInUser.getLocal_image())
                        .fitCenter()
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.sign_up_icon) // will be displayed if the image cannot be loaded
                        .into(holder.drawer_header_icon);
                }else {
                    byte[] decodedString = Base64.decode(loggedInUser.getLocal_image(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.drawer_header_icon.setImageBitmap(decodedByte);
                }
//                Glide.with(context)
//                        .load(loggedInUser.getLocal_image())
//                        .fitCenter()
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .error(R.drawable.user) // will be displayed if the image cannot be loaded
//                        .into(holder.drawer_header_icon);



            }else {
//                List<String> categories = new ArrayList<String>();
//                categories.add("Item 1");
//                categories.add("Item 2");
//                categories.add("Item 3");
//                categories.add("Item 4");
//                categories.add("Item 5");
//                categories.add("Item 6");
                holder.settings_text.setText(parseJasonLang.getJsonToString("Setting"));
                String[] countryNames={parseJasonLang.getJsonToString("change_language"),parseJasonLang.getJsonToString("change_password")};

                holder.settings_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        holder.settings_text.setVisibility(View.VISIBLE);
//                        holder.settings_spinner.setVisibility(View.VISIBLE);
                        ConnectivityManager cm =
                                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                        MainActivity.isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();
                        if(MainActivity.isConnected) {
//                            if (holder.settings_spinner.getSelectedItem() == null) { // user selected nothing...
//                                holder.settings_spinner.performClick();
//                            }
                            final AppCompatDialog dialog = new AppCompatDialog(context);
                            // Include dialog.xml file
                            dialog.setContentView(R.layout.dialog_settings);
                            dialog.getWindow().setGravity(Gravity.CENTER);
                            // Set dialog title


                            // set values for custom dialog components - text, image and button
                            TextView select_date_text = (TextView) dialog.findViewById(R.id.select_date_text);
                            ImageView login_back = (ImageView) dialog.findViewById(R.id.login_back);
                            LinearLayout password_layout =  dialog.findViewById(R.id.password_layout);
                            TextView pass_text =  dialog.findViewById(R.id.pass_text);
                            LinearLayout lang_layout = dialog.findViewById(R.id.lang_layout);
                            TextView lang_text =  dialog.findViewById(R.id.lang_text);
                            TextView sign_up_button =  dialog.findViewById(R.id.sign_up_button);

                            select_date_text.setText(parseJasonLang.getJsonToString("Setting"));
                            login_back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            password_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, ChangePassword.class);
                                    Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                                    context.startActivity(intent);
                                }
                            });
                            pass_text.setText(parseJasonLang.getJsonToString("change_password"));

                            lang_layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, ChangeLanguageActivity.class);
                                    Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                                    context.startActivity(intent);
                                }
                            });
                            lang_text.setText(parseJasonLang.getJsonToString("change_language"));
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

                            dialog.show();
                        }else {
                            MyToast.showToast(context,parseJasonLang.getJsonToString("please_connect_internet"));
                        }
                    }
                });

                CustomAdapter customAdapter=new CustomAdapter(context,flags,countryNames);
                holder.settings_spinner.setAdapter(customAdapter);
                //Setting the ArrayAdapter data on the Spinner

                holder.settings_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if(position==0){
                            Intent intent = new Intent(context, ChangeLanguageActivity.class);
                        //    Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                            context.startActivity(intent);
                        }
                        Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                        if(position==1){
                           // Toast.makeText(context, countryNames[position], Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, ChangePassword.class);
                            context.startActivity(intent);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                holder.header_email.setText(MyPreferences.getInstance(context).getEMAIL());
                holder.headerText.setText(MyPreferences.getInstance(context).getFIRSTNAME() + " " + MyPreferences.getInstance(context).getLASTNAME());


//                Glide.with(context)
//                        .load(MyPreferences.getInstance(context).getIMAGE())
//                        .fitCenter()
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(true)
//                        .error(R.drawable.user) // will be displayed if the image cannot be loaded
//                        .into(holder.drawer_header_icon);
//
//                holder.change_password.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        MainActivity.flagIsSliderOpen=false;
//                        MainActivity.mDrawerLayout.closeDrawer(Gravity.LEFT);
//                        Fragment fragment= new SettingsFragment();
//                        ProjectUtil.replaceFragment(context, fragment);
////                    Fragment fragment= new HowToUse();
////                    ProjectUtil.replaceFragment(MainActivity.this, fragment);
//                        // Intent intent = new Intent(getApplicationContext(),HowToUseActivity.class);
//                        // intent.putExtra("key",key);
//                        // startActivity(intent);
//                    }
//                });

                if(loggedInUser.getLocal_image().equals("")&&MyPreferences.getInstance(context).getIMAGE().equals("")){
                    Glide.with(context)
                        .load(MyPreferences.getInstance(context).getIMAGE())
                        .fitCenter()
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.sign_up_icon) // will be displayed if the image cannot be loaded
                        .into(holder.drawer_header_icon);
                }else {
                    byte[] decodedString = Base64.decode(MyPreferences.getInstance(context).getIMAGE(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.drawer_header_icon.setImageBitmap(decodedByte);
                }

            }
        }
        if(position > 0 && position < drawerMenuList.size()+1){
            holder.title.setText(drawerMenuList.get(position - 1).getTitle());
            holder.icon.setImageResource(drawerMenuList.get(position - 1).getIcon());
//            if(position==5&&MyPreferences.getInstance(context).getUSER_ROLE().equals("2")){
//                holder.notification.setVisibility(View.VISIBLE);
//            }else {
//                holder.notification.setVisibility(View.GONE);
//            }

        }
//        Glide.with(context)
//                .load(MyPreferences.getInstance(context).getIMAGE())
//                .fitCenter()
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .error(R.drawable.login_seeker) // will be displayed if the image cannot be loaded
//                .into(holder.drawer_header_icon);
    }

    @Override
    public int getItemCount() {
        return drawerMenuList.size()+2;
    }



    @Override
    public int getItemViewType(int position) {

        if(position == 0){
            return  TYPE_HEADER;
        }
        if(position > drawerMenuList.size()+1){
            return  TYPE_FOOTER;
        }
        if(position > 0 && position < drawerMenuList.size()+1){
            return  TYPE_MENU;
        }
      else {
          return TYPE_FOOTER;
        }
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView headerText,notification,header_email,settings_text;
        ImageView change_password;
        ImageView icon;
        CircleImageView drawer_header_icon;
        LinearLayout settings_layout;
        Spinner settings_spinner;

        public DrawerViewHolder(View itemView, int viewType) {
            super(itemView);


            if(viewType == 0){
                headerText = (TextView)itemView.findViewById(R.id.headerText);
                header_email=(TextView)itemView.findViewById(R.id.header_email);
                drawer_header_icon=(CircleImageView)itemView.findViewById(R.id.drawer_header_icon);
                change_password=(ImageView)itemView.findViewById(R.id.change_password);
                settings_layout=itemView.findViewById(R.id.settings_layout);
                settings_spinner=itemView.findViewById(R.id.settings_spinner);
                settings_text=itemView.findViewById(R.id.settings_text);
            }if(viewType ==1) {
                title = (TextView) itemView.findViewById(R.id.title);
                icon = (ImageView) itemView.findViewById(R.id.icon);
                notification=(TextView)itemView.findViewById(R.id.notification);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(view, getAdapterPosition());

                }
            });
        }

    }




    public void setOnItemClickLister(OnItemSelecteListener mListener) {
        this.mListener = mListener;
    }

   public interface OnItemSelecteListener{
        public void onItemSelected(View v, int position);
    }

}