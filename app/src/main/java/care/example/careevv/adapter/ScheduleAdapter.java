package care.example.careevv.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import care.example.careevv.activity.ScheduleActivity;
import care.example.careevv.model.ScheduleModel;
import care.example.careevv.preferences.LoggedInUser;
import care.example.careevv.util.ParseJasonLang;
import de.hdodenhof.circleimageview.CircleImageView;
import care.example.careevv.R;

public class ScheduleAdapter  extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context mContext;
    private List<ScheduleModel> mDataSet;
    LoggedInUser loggedInUser;
    ParseJasonLang parseJasonLang;

    public ScheduleAdapter(Context context, List<ScheduleModel> list){
        mContext = context;
        mDataSet = list;
        loggedInUser= new LoggedInUser(context);
        parseJasonLang = new ParseJasonLang(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CardView card_view;
        TextView name_tv,location,date_Tv,status,view_deatils_button;
        ImageView call_icon,map_icon;
        CircleImageView profile_image;


        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            card_view = (CardView) v.findViewById(R.id.card_view);
            name_tv = (TextView) v.findViewById(R.id.name_tv);
            location = (TextView) v.findViewById(R.id.location);
            date_Tv = (TextView) v.findViewById(R.id.date_Tv);
            status = (TextView) v.findViewById(R.id.status);
            view_deatils_button = (TextView) v.findViewById(R.id.view_deatils_button);
            call_icon = (ImageView) v.findViewById(R.id.call_icon);
            map_icon = (ImageView) v.findViewById(R.id.map_icon);
            profile_image = (CircleImageView) v.findViewById(R.id.profile_image);

        }
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_schedule,parent,false);
        ScheduleAdapter.ViewHolder vh = new ScheduleAdapter.ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ViewHolder holder, int position){
        // Get the current color from the data set
        final ScheduleModel scheduleModel = mDataSet.get(position);

        holder.name_tv.setText(scheduleModel.getName());
        holder.date_Tv.setText(scheduleModel.getStart_time() + " To "+scheduleModel.getEnd_time());
        holder.location.setText(scheduleModel.getLocation());
//        Glide.with(mContext)
//                .load(scheduleModel.getPic())
//                .fitCenter()
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .error(R.drawable.schimg) // will be displayed if the image cannot be loaded
//                .into(holder.profile_image);

        if(scheduleModel.getPic().equals("")){
            holder.profile_image.setImageResource(R.drawable.sign_up_icon);
        }else {
            byte[] decodedString = Base64.decode(scheduleModel.getPic(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.profile_image.setImageBitmap(decodedByte);
        }
        holder.view_deatils_button.setText(parseJasonLang.getJsonToString("view_detail"));

        if(scheduleModel.getStatus().equals("1")){
            holder.status.setText(parseJasonLang.getJsonToString("personal"));
        }

        if(scheduleModel.getStatus().equals("2")){
            holder.status.setText(parseJasonLang.getJsonToString("respite"));
        }

        holder.view_deatils_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                Intent intent = new Intent(mContext, ScheduleActivity.class);
                intent.putExtra("location",scheduleModel.getLocation());
                intent.putExtra("id",scheduleModel.getId());
                intent.putExtra("time",scheduleModel.getStart_time()+" To "+scheduleModel.getEnd_time());
                intent.putExtra("name",scheduleModel.getName());
                intent.putExtra("phone",scheduleModel.getCall());
                intent.putExtra("pic",scheduleModel.getPic());
                intent.putExtra("client_id",scheduleModel.getClientId());
                intent.putExtra("direction",scheduleModel.getLat()+","+scheduleModel.getLongi());
                intent.putExtra("additional_notes",scheduleModel.getAdditional_note());
                mContext.startActivity(intent);
            }
        });

        holder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+scheduleModel.getCall()));
                mContext.startActivity(callIntent);
            }
        });
      //  String parts[]=scheduleModel.getDirection().split(",");
        final String lat=scheduleModel.getLat();
        final String longi=scheduleModel.getLongi();
        holder.map_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = Double.parseDouble(lat);
                String value;
                double longitude = Double.parseDouble(longi);
                String label = "I'm Here!";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(mapIntent);

            }
        });


    }

    @Override
    public int getItemCount(){
        // Count the items
        return mDataSet.size();
    }
}
