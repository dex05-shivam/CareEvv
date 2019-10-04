package care.example.careevv.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import care.example.careevv.R;
import care.example.careevv.constants.CityState;
import care.example.careevv.model.ServiceModel;
import care.example.careevv.util.DatabaseHandler;

public class AllServiceListAdapter extends RecyclerView.Adapter<AllServiceListAdapter.ViewHolder> {

    private Context mContext;
    private List<ServiceModel> mDataSet;
    DatabaseHandler db ;

    public AllServiceListAdapter(Context context, List<ServiceModel> list){
        mContext = context;
        mDataSet = list;
        db = new DatabaseHandler(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        //CardView card_view;
        TextView textView;//,location,date_Tv,status,view_deatils_button;
        ImageView imageView,image;
        LinearLayout ll1,imageViewLayout;
     //   CircleImageView profile_image;


        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
           // card_view = (CardView) v.findViewById(R.id.card_view);
            textView = (TextView) v.findViewById(R.id.textView);
            ll1 = (LinearLayout) v.findViewById(R.id.ll1);
           // date_Tv = (TextView) v.findViewById(R.id.date_Tv);
           // status = (TextView) v.findViewById(R.id.status);
           // view_deatils_button = (TextView) v.findViewById(R.id.view_deatils_button);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            imageViewLayout =  v.findViewById(R.id.imageViewLayout);
            image=v.findViewById(R.id.image);
           // profile_image = (CircleImageView) v.findViewById(R.id.profile_image);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_all_services,parent,false);
        ViewHolder vh = new ViewHolder(v);



        // Return the ViewHolder
        return vh;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        // Get the current color from the data set
        final ServiceModel scheduleModel = mDataSet.get(position);

       // Log.e("image_url", CityState.service_categoryImgurl[position]);
        holder.textView.setText(scheduleModel.getName());
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            Glide.with(mContext)
                    .load(CityState.service_categoryImgurl[position])
                    .fitCenter()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.logo) // will be displayed if the image cannot be loaded
                    .into(holder.image);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .fitCenter()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.logo) // will be displayed if the image cannot be loaded
                    .into(holder.image);
        }



        holder.ll1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(holder.imageView.getVisibility()==View.GONE) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.imageViewLayout.setBackgroundResource(R.drawable.circle_orange);
                    holder.ll1.setElevation(0);
                    db.addSelectedByUser(new ServiceModel(scheduleModel.getId(),scheduleModel.getName(),"",""));
                }else {
                    holder.imageViewLayout.setBackgroundResource(R.drawable.circle_gray);
                    holder.imageView.setVisibility(View.GONE);
                    db.deleteSelectedByID(scheduleModel.getId());
                    holder.ll1.setElevation(5);
                }
            }
        });


    }

    @Override
    public int getItemCount(){
        // Count the items
        return mDataSet.size();
    }



}
