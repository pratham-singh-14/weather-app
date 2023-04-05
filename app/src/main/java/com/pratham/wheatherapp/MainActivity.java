package com.pratham.wheatherapp;

import static com.google.android.gms.common.util.ArrayUtils.concat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout home;
    private ProgressBar loading;
    private TextView citynametv,temperature,condition,date,tv;
    private TextInputEditText cityedt;
    private ImageView icon,backIV,searchh;
    private RecyclerView weatherRV;
    private ArrayList<model> list;
    private RecyclerAapter adapter;
    private int PERMISSION_CODE=1;
    private  int s=0;
    private  int i = 1;
    private TextInputLayout textInputLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        home = findViewById(R.id.homeurl);
        loading = findViewById(R.id.pbloading);
        citynametv = findViewById(R.id.tv_cityname);
        temperature = findViewById(R.id.tv_temp);
        condition = findViewById(R.id.condition);
        textInputLayout=findViewById(R.id.til_city);
        tv=findViewById(R.id.tv);
        date=findViewById(R.id.date);
        cityedt = findViewById(R.id.edt_city);
        icon = findViewById(R.id.icons);
        backIV = findViewById(R.id.background);
        searchh = findViewById(R.id.searchh);
        weatherRV = findViewById(R.id.recycler);
        list = new ArrayList<>();
        adapter = new RecyclerAapter(this, list);
        weatherRV.setAdapter(adapter);
        String cityname="Mumbai";
        citynametv.setText(cityname);
        cityedt.setText(cityname);
        loading.setVisibility(View.VISIBLE);
        home.setVisibility(View.GONE);
        getWeatherInfo(cityname);

        searchh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityedt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    citynametv.setText(city);
                    loading.setVisibility(View.VISIBLE);
                    home.setVisibility(View.GONE);
                    getWeatherInfo(city);

                }
            }
        });

    }
    public void getWeatherInfo(String city)
    {
        String url="https://api.weatherapi.com/v1/forecast.json?key=21b8bfb760244248afa160032231801&q="+city+"&days=1&aqi=yes&alerts=yes";
        citynametv.setText(city);
       RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                list.clear();
                try {
                    String tempreatur = response.getJSONObject("current").getString("temp_c");
                         temperature.setText(tempreatur+" °C");



                    String date_v = response.getJSONObject("current").getString("last_updated");
                    SimpleDateFormat input=new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    SimpleDateFormat output=new SimpleDateFormat("dd-MM-YYYY");
                    try {
                        Date to=input.parse(date_v);
                       date.setText("Today Date: "+output.format(to));
                    } catch (ParseException e) {

                    }

                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String conditions=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Glide.with(MainActivity.this).load("https:".concat(conditionIcon)).into(icon);
                    condition.setText(conditions);


                 if(isDay==1)
                    {
                        Glide.with(MainActivity.this).load("https://media.istockphoto.com/id/1192679095/id/foto/latar-belakang-langit-biru-jernih-awan-dengan-latar-belakang.jpg?s=170667a&w=0&k=20&c=HM0OO9ukYA2Hq7jVWHbRF2eq96OSFwQnUZbSq1Pw9Dk=").into(backIV);

                        citynametv.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));
                        cityedt.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));

                        temperature .setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));
                        condition .setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));
                        date.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));
                        tv.setTextColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black))));
                        searchh.setBackgroundColor(Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(MainActivity.this, R.color.black_shade))));

                    }else
                    {
                        Glide.with(MainActivity.this).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtQ86GxPvZLoPKz0CmjqtQ0Ar12mEOm6KWcXnwTHciNuWx8yhjbNeqXRfucSP-xGh3v5o&usqp=CAU").into(backIV);



                    }
                    JSONObject forecastObj=response.getJSONObject("forecast");
                    JSONObject forecastO=forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forecastO.getJSONArray("hour");
                    for(int i=0;i<hourArray.length();i++)
                    {
                        JSONObject hourobj=hourArray.getJSONObject(i);
                        String timee=hourobj.getString("time");
                        String temper=hourobj.getString("temp_c");
                        String img=hourobj.getJSONObject("condition").getString("icon");
                        String wind=hourobj.getString("wind_kph");
                        list.add(new model(timee,temper,img,wind));
                    }
                    adapter.notifyDataSetChanged();

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                home.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                date.setText("");
                temperature.setText("No data available");
                condition.setText("");
                icon.setVisibility(View.GONE);


                Toast.makeText(MainActivity.this,"City not Found !", Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}







//  "current": {
//          "last_updated_epoch": 1680671700,
//          "last_updated": "2023-04-05 06:15",
//          "temp_c": 4.0,
//          "temp_f": 39.2,