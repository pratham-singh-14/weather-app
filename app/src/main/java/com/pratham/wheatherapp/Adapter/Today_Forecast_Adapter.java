package com.pratham.wheatherapp.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pratham.wheatherapp.R;
import com.pratham.wheatherapp.Data_model.today_forecast_data_model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Today_Forecast_Adapter extends RecyclerView.Adapter<Today_Forecast_Adapter.ViewHolder> {
    private Context context;
    private ArrayList<today_forecast_data_model> datamodel;

    public Today_Forecast_Adapter(Context context, ArrayList<today_forecast_data_model> datamodel) {
        this.context = context;
        this.datamodel = datamodel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        today_forecast_data_model items= datamodel.get(position);
        holder.temperatue.setText(items.getTemperature()+"°C");
        Glide.with(context).load("https:".concat(items.getIcon())).into(holder.conditionIV);
        holder.windtv.setText(items.getWindSpeed()+"km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm a");
        SimpleDateFormat compare_output=new SimpleDateFormat("hh a");





        try {
           Date t= input.parse(items.getTime());


           //current_time and compare
            long currentTimeMillis = System.currentTimeMillis();
            Date currentTime = new Date(currentTimeMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("hh a", Locale.getDefault());
            String formattedTime = sdf.format(currentTime);
            String compare_time=compare_output.format(t);
            if (formattedTime.equals(compare_time))
            {
                holder.time.setText("Now");
                holder.time.setTextColor(Color.GREEN);
                holder.windtv.setTextColor(Color.GREEN);
                holder.temperatue.setTextColor(Color.GREEN);


            }
            else {

                holder.time.setText(output.format(t));
                holder.time.setTextColor(Color.WHITE);
                holder.windtv.setTextColor(Color.WHITE);
                holder.temperatue.setTextColor(Color.WHITE);
            }
        }catch (ParseException e)
        {
            Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public int getItemCount() {
        return datamodel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windtv,temperatue,time;
        private ImageView conditionIV;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            windtv=itemView.findViewById(R.id.tv_windSpeed);
            temperatue=itemView.findViewById(R.id.tv_temperature);
            time=itemView.findViewById(R.id.tv_time);
            conditionIV=itemView.findViewById(R.id.iv_condition);
        }
    }
}
