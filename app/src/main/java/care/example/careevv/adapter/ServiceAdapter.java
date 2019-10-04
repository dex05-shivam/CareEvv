package care.example.careevv.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import care.example.careevv.R;
import care.example.careevv.model.ServiceModel;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private Context mContext;
    private List<ServiceModel> mDataSet;

    public ServiceAdapter(Context context, List<ServiceModel> list){
        mContext = context;
        mDataSet = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CardView card_view;
        TextView name_tv,location,date_Tv,status,view_deatils_button;
        ImageView call_icon,map_icon;
        CircleImageView profile_image;
        TextView service_checkbox;


        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            service_checkbox = (TextView) v.findViewById(R.id.service_checkbox);


        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_services,parent,false);
        ViewHolder vh = new ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        // Get the current color from the data set
        ServiceModel scheduleModel = mDataSet.get(position);

        holder.service_checkbox.setText(scheduleModel.getName());

    }

    @Override
    public int getItemCount(){
        // Count the items
        return mDataSet.size();
    }

}
