package com.pratham.wheatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerAapter extends RecyclerView.Adapter<RecyclerAapter.ViewHolder> {
    private Context context;
    private ArrayList<model> datamodel;

    public RecyclerAapter(Context context, ArrayList<model> datamodel) {
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
        model items= datamodel.get(position);
        holder.temperatue.setText(items.getTemperature()+"°C");
        Glide.with(context).load("https:".concat(items.getIcon())).into(holder.conditionIV);
        holder.windtv.setText(items.getWindSpeed()+"km/h");
        SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output=new SimpleDateFormat("hh:mm a");

        try {
           Date t= input.parse(items.getTime());
            holder.time.setText(output.format(t));
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
