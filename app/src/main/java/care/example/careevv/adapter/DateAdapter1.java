package care.example.careevv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import care.example.careevv.R;
import care.example.careevv.model.DateModel;

public class DateAdapter1 extends RecyclerView.Adapter<DateAdapter1.ViewHolder> {
    private Context mContext;
    private List<DateModel> mDataSet;
    private String[] mData;
    private static SingleClickListener sClickListener;
    public static int sSelected = -1;
    public static int date_flag=0;

    public DateAdapter1(Context context, List<DateModel> list){
        mContext = context;
        mDataSet = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        LinearLayout card_view;
        TextView tv;
        ImageView arrow_drop;
        View view1;


        public ViewHolder(View v){
            super(v);
            // Get the widget reference from the custom layout
            card_view = v.findViewById(R.id.card_view);
            tv = v.findViewById(R.id.tv);
            arrow_drop = v.findViewById(R.id.arrow_drop);
            view1= v.findViewById(R.id.view1);
            v.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            sSelected = getAdapterPosition();
            sClickListener.onItemClickListener(getAdapterPosition(), view);
            date_flag=1;
        }
    }
    public void selectedItem() {
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SingleClickListener clickListener) {
        sClickListener = clickListener;
    }
    @Override
    public DateAdapter1.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(mContext).inflate(R.layout.list_date,parent,false);
        ViewHolder vh = new ViewHolder(v);

        // Return the ViewHolder
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // position = position % ACTUAL_SIZE_OF_LIST;
        DateModel dateModel = mDataSet.get(position);
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();
        Log.d("Month",dateFormat.format(date));

        if(dateModel.getDate().length()<2) {
            holder.tv.setText("0"+dateModel.getDate());
        }else {

            holder.tv.setText(dateModel.getDate());
        }
        if (sSelected == position) {
            // holder.arrow_drop.setVisibility(View.VISIBLE);
            holder.card_view.setBackgroundResource(R.drawable.button_touch_orange);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.card_view.setElevation(10);
            }
            //holder.view1.setVisibility(View.GONE);
            holder.tv.setTextColor(Color.WHITE);
        } else {

            if(date_flag==0) {
                if (Integer.parseInt(dateFormat.format(date)) == Integer.parseInt(dateModel.getDate())) {
                    //  holder.arrow_drop.setVisibility(View.VISIBLE);
                    holder.card_view.setBackgroundResource(R.drawable.button_touch_orange);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.card_view.setElevation(10);
                    }
                    //   holder.view1.setVisibility(View.GONE);
                    holder.tv.setTextColor(Color.WHITE);
                }else {
                    holder.card_view.setBackgroundColor(Color.WHITE);
                    // holder.arrow_drop.setVisibility(View.GONE);
                    // holder.view1.setVisibility(View.VISIBLE);
                    holder.tv.setTextColor(Color.BLACK);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.card_view.setElevation(0);
                    }
                }
            }else {
                holder.card_view.setBackgroundColor(Color.WHITE);
                //  holder.arrow_drop.setVisibility(View.GONE);
                //  holder.view1.setVisibility(View.VISIBLE);
                holder.tv.setTextColor(Color.BLACK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.card_view.setElevation(0);
                }
            }
        }

    }

    @Override
    public int getItemCount(){
        // Count the items
        return mDataSet.size();
    }


    public interface SingleClickListener {
        void onItemClickListener(int position, View view);
    }

}

