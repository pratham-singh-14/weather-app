package com.pratham.wheatherapp.Activity;

import static com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.LocationData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.pratham.wheatherapp.Adapter.Forecast_Adapter;
import com.pratham.wheatherapp.Adapter.Today_Forecast_Adapter;
import com.pratham.wheatherapp.Data_model.Forecast_data_model;
import com.pratham.wheatherapp.Data_model.today_forecast_data_model;
import com.pratham.wheatherapp.Global_Class.GPSTracker;
import com.pratham.wheatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Listener, LocationData.AddressCallBack {
    private ScrollView home;
    private ProgressBar loading;
    private TextView citynametv, temperature, condition, date, sunrise_tv, moonrise_tv, sunset_tv, moonset_tv, min_max_tv;
    private TextInputEditText cityedt;
    private ImageView icon, backIV, searchh;
    private RecyclerView weatherRV, weather_forecast;
    private ArrayList<today_forecast_data_model> list;
    private ArrayList<Forecast_data_model> forecast_list;
    private Today_Forecast_Adapter adapter;
    private Forecast_Adapter forecastAdapter;
    private int PERMISSION_CODE = 1;
    LinearLayout sorry_layout;
    private int s = 0;
    private int i = 1;
    private String tv_date, tv_week_day, iv_icon, tv_min, tv_max;
    private TextInputLayout textInputLayout;
    ImageView bg_back;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView co_tv, no2_tv, so2_tv, o3_tv, air_quality_tv;
    TextView uv_tv, feel_tv, humidity_tv, wind_tv, wind, pressure_tv, visivility_tv;
    MaterialSearchBar searchBar;
    ImageView search_iv;
    int visible = 0;
    final long MIN_TIME = 5000;
    final long MAX_DISTANCE = 1000;

    String Location_provider = LocationManager.GPS_PROVIDER;
    LocationManager locationManager;
    LocationListener locationListener;
    private String url = "";
    Location location2;
    LinearLayout location_layout;
    EasyWayLocation easyWayLocation;
    int adbEnabled = 0;
    Dialog dialog;
    boolean ONE_TIME = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        home = findViewById(R.id.home);
        loading = findViewById(R.id.progress_bar);
        citynametv = findViewById(R.id.tv_location);
        temperature = findViewById(R.id.temp_original);
        condition = findViewById(R.id.tv_condition);
        bg_back = findViewById(R.id.bg_back);
        date = findViewById(R.id.date);
        sunrise_tv = findViewById(R.id.sunrise_tv);
        sunset_tv = findViewById(R.id.sunset_tv);
        moonrise_tv = findViewById(R.id.moonrise_tv);
        moonset_tv = findViewById(R.id.moonset_tv);
        min_max_tv = findViewById(R.id.min_max_temp);
        weatherRV = findViewById(R.id.recycler_view);
        weather_forecast = findViewById(R.id.recycler_view_forecast);
        no2_tv = findViewById(R.id.no2_tv);
        so2_tv = findViewById(R.id.so2_tv);
        o3_tv = findViewById(R.id.o3_tv);
        co_tv = findViewById(R.id.co_tv);
        air_quality_tv = findViewById(R.id.air_quality);
        uv_tv = findViewById(R.id.uv_tv);
        feel_tv = findViewById(R.id.feels_like);
        humidity_tv = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
        wind_tv = findViewById(R.id.wind_tv);
        pressure_tv = findViewById(R.id.pressure);
        visivility_tv = findViewById(R.id.visibility);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        searchBar = findViewById(R.id.searchBar);
        search_iv = findViewById(R.id.search_iv);
        sorry_layout = findViewById(R.id.sorry_layout);
        location_layout = findViewById(R.id.location_layout);
        easyWayLocation = new EasyWayLocation(this, false, false, this);
        set_adapter();


        loading.setVisibility(View.GONE);
        home.setVisibility(View.GONE);
        sorry_layout.setVisibility(View.GONE);
        location_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (location2 != null) {
                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", location2.getLatitude(), location2.getLongitude());
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });
        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible == 0) {
                    searchBar.setVisibility(View.VISIBLE);
                    visible = 1;
                } else if (visible == 1) {
                    searchBar.setVisibility(View.GONE);
                    visible = 0;
                }
            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (searchBar.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Enter valid city name", Toast.LENGTH_SHORT).show();
                } else {
                    searchBar.setVisibility(View.GONE);
                    visible = 0;
                    citynametv.setText(searchBar.getText().toString());
                    getWeatherInfo(citynametv.getText().toString(), "", "");
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                home.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                bg_back.setImageResource(R.drawable.bg_gradient);
                sorry_layout.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (citynametv.getText().toString().equals("")){
                                loading.setVisibility(View.VISIBLE);
                                bg_back.setImageResource(R.drawable.bg_gradient);
                                home.setVisibility(View.GONE);
                                sorry_layout.setVisibility(View.GONE);
                                if (isGooglePlayServicesAvailable(MainActivity.this)) {
                                    // Toast.makeText(DashboardActivity.this, "GMS supported", Toast.LENGTH_SHORT).show();
                                    easyWayLocation.startLocation();
                                } else {

                                    // Toast.makeText(DashboardActivity.this, "GMS not supported on this device", Toast.LENGTH_SHORT).show();
                                    getLongLat();
                                }
                            } else {
                                getWeatherInfo(citynametv.getText().toString(), "", "");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ONE_TIME = true;


        adbEnabled = Settings.Secure.getInt(getContentResolver(), Settings.Global.ADB_ENABLED, 0);

        if (adbEnabled == 1) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Developer Mode On");
            alertDialog.setMessage("For security reasons, you need to turn off developer mode");
            alertDialog.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                }
            });

            alertDialog.show();
        } else if (isconnected()) {
            if (citynametv.getText().toString().equals("")) {
                if (isGooglePlayServicesAvailable(MainActivity.this)) {
                    easyWayLocation.startLocation();
                } else {

                    getLongLat();
                }
            }
        } else {
            home.setVisibility(View.GONE);
            sorry_layout.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);
            dialog = No_INTERNET(this);
            dialog.show();
            dialog.findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    onResume();
                }
            });
        }
    }

    public boolean isconnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void set_adapter() {


        //Today Forecast
        list = new ArrayList<>();
        adapter = new Today_Forecast_Adapter(this, list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        weatherRV.setLayoutManager(layoutManager);
        weatherRV.setAdapter(adapter);


        //Forecast Recyclerview
        forecast_list = new ArrayList<>();
        forecastAdapter = new Forecast_Adapter(forecast_list, this);
        LinearLayoutManager layoutManager_forecast = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        weather_forecast.setLayoutManager(layoutManager_forecast);
        weather_forecast.setAdapter(forecastAdapter);


    }


    public Dialog No_INTERNET(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.no_internet_connection);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void getWeatherInfo(String city, String latitude, String longitude) {

        //    Toast.makeText(this, "call", Toast.LENGTH_SHORT).show();
        if (city.equals("")) {
            url = "https://api.weatherapi.com/v1/forecast.json?key=21b8bfb760244248afa160032231801&q=" + latitude + "," + longitude + "&days=3&aqi=yes&alerts=yes";

        } else {
            citynametv.setText(city);
            url = "https://api.weatherapi.com/v1/forecast.json?key=21b8bfb760244248afa160032231801&q=" + city + "&days=3&aqi=yes&alerts=yes";

        }
        Log.d("TAG", "getWeatherInfo: " + url);
        loading.setVisibility(View.VISIBLE);
        bg_back.setImageResource(R.drawable.bg_gradient);
        home.setVisibility(View.GONE);
        sorry_layout.setVisibility(View.GONE);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                sorry_layout.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);
                list.clear();
                forecast_list.clear();

                try {

                    citynametv.setText(response.getJSONObject("location").getString("name"));
                    visivility_tv.setText(response.getJSONObject("current").getString("vis_km") + " km");
                    pressure_tv.setText(response.getJSONObject("current").getString("pressure_mb") + " hPa");
                    wind_tv.setText(response.getJSONObject("current").getString("wind_kph") + " km/h");
                    wind.setText(response.getJSONObject("current").getString("wind_dir") + " wind");
                    humidity_tv.setText(response.getJSONObject("current").getString("humidity") + " %");
                    feel_tv.setText(response.getJSONObject("current").getString("feelslike_c") + " °c");
                    String uv_index = response.getJSONObject("current").getString("uv");
                    if (uv_index.equals("0")) {
                        uv_tv.setText(uv_index + " - Very Low");
                    } else if (uv_index.equals("1") || uv_index.equals("2")) {
                        uv_tv.setText(uv_index + " - Low");
                    } else if (uv_index.equals("3") || uv_index.equals("4") || uv_index.equals("5")) {
                        uv_tv.setText(uv_index + " - Moderate");
                    } else if (uv_index.equals("6") || uv_index.equals("7")) {
                        uv_tv.setText(uv_index + " - High");
                    } else if (uv_index.equals("8") || uv_index.equals("9") || uv_index.equals("10")) {
                        uv_tv.setText(uv_index + " - Very High");
                    } else {
                        uv_tv.setText(uv_index + " - Extreme");
                    }
                    //Air_quality
                    String air_quality = response.getJSONObject("current").getJSONObject("air_quality").getString("us-epa-index");
                    if (air_quality.equals("1")) {
                        air_quality_tv.setText(air_quality + " - " + "Good");
                    } else if (air_quality.equals("2")) {
                        air_quality_tv.setText(air_quality + " - " + "Moderate");
                    } else if (air_quality.equals("3")) {
                        air_quality_tv.setText(air_quality + " - " + "Unhealthy for sensitive group");
                    } else if (air_quality.equals("4")) {
                        air_quality_tv.setText(air_quality + " - " + "Unhealthy");
                    } else if (air_quality.equals("5")) {
                        air_quality_tv.setText(air_quality + " - " + "Very Unhealthy");
                    } else if (air_quality.equals("6")) {
                        air_quality_tv.setText(air_quality + " - " + "Hazardous");
                    }
                    co_tv.setText(response.getJSONObject("current").getJSONObject("air_quality").getString("co"));
                    no2_tv.setText(response.getJSONObject("current").getJSONObject("air_quality").getString("no2"));
                    o3_tv.setText(response.getJSONObject("current").getJSONObject("air_quality").getString("o3"));
                    so2_tv.setText(response.getJSONObject("current").getJSONObject("air_quality").getString("so2"));
                    String tempreatur = response.getJSONObject("current").getString("temp_c");
                    temperature.setText(tempreatur + "°c");
                    String date_v = response.getJSONObject("current").getString("last_updated");
                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                    SimpleDateFormat output = new SimpleDateFormat("dd MMMM");
                    try {
                        Date to = input.parse(date_v);
                        date.setText(output.format(to));
                    } catch (ParseException e) {

                    }

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    if (isDay == 1) {

                        Glide.with(MainActivity.this).load(R.drawable.day_bg).into(bg_back);

                    } else {

                        Glide.with(MainActivity.this).load(R.drawable.night_bg).into(bg_back);

                    }
                    String conditions = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    condition.setText(conditions);


                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONObject min_max = forecastO.getJSONObject("day");
                    min_max_tv.setText("Min : " + min_max.getString("mintemp_c") + "°c" + "  Max : " + min_max.getString("maxtemp_c") + "°c");
                    sunrise_tv.setText(forecastO.getJSONObject("astro").getString("sunrise"));
                    sunset_tv.setText("Sunset : " + forecastO.getJSONObject("astro").getString("sunset"));
                    moonrise_tv.setText(forecastO.getJSONObject("astro").getString("moonrise"));
                    moonset_tv.setText("Moonset : " + forecastO.getJSONObject("astro").getString("moonset"));
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourobj = hourArray.getJSONObject(i);
                        String timee = hourobj.getString("time");
                        String temper = hourobj.getString("temp_c");
                        String img = hourobj.getJSONObject("condition").getString("icon");
                        String wind = hourobj.getString("wind_kph");
                        list.add(new today_forecast_data_model(timee, temper, img, wind));
                    }
                    adapter.notifyDataSetChanged();


                    JSONArray forecastArray = forecastObj.getJSONArray("forecastday");
                    SimpleDateFormat input_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat output_dateFormat = new SimpleDateFormat("dd MMM");
                    SimpleDateFormat output_week_DayFormat = new SimpleDateFormat("EEEE");
                    for (int i = 0; i < forecastArray.length(); i++) {
                        JSONObject hourobj = forecastArray.getJSONObject(i);
                        try {
                            Date to = input_dateFormat.parse(hourobj.getString("date"));
                            tv_date = output_dateFormat.format(to);
                            tv_week_day = output_week_DayFormat.format(to);
                        } catch (ParseException e) {

                        }

                        iv_icon = hourobj.getJSONObject("day").getJSONObject("condition").getString("icon");
                        tv_min = hourobj.getJSONObject("day").getString("mintemp_c");
                        tv_max = hourobj.getJSONObject("day").getString("maxtemp_c");
                        forecast_list.add(new Forecast_data_model(tv_date, tv_week_day, iv_icon, tv_min, tv_max));
                    }
                    forecastAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sorry_layout.setVisibility(View.VISIBLE);
                bg_back.setImageResource(R.drawable.bg_gradient);
                home.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "City not Found !", Toast.LENGTH_LONG).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                // googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }

            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_SETTING_REQUEST_CODE) {

            easyWayLocation.onActivityResult(resultCode);
        }
    }

    @Override
    public void locationOn() {
        loading.setVisibility(View.VISIBLE);
        bg_back.setImageResource(R.drawable.bg_gradient);
        Toast.makeText(this, "Location On", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentLocation(Location location) {
        location2 = location;


        if (location2 == null) {
            Toast.makeText(MainActivity.this, "Google not getting location. Please Refresh..", Toast.LENGTH_SHORT).show();
            loading.setVisibility(View.GONE);
            home.setVisibility(View.GONE);
            sorry_layout.setVisibility(View.VISIBLE);
            easyWayLocation.endUpdates();
        } else {


            if (ONE_TIME == true) {

                getWeatherInfo("", String.valueOf(location2.getLatitude()), String.valueOf(location2.getLongitude()));
                ONE_TIME = false;
            }
            easyWayLocation.endUpdates();


        }


    }

    @Override
    public void locationCancelled() {

        loading.setVisibility(View.GONE);
        home.setVisibility(View.GONE);
        sorry_layout.setVisibility(View.GONE);
        getWeatherInfo("Mumbai", "", "");
        Toast.makeText(this, "location Cancelled!! Data Get Defualt Set Location", Toast.LENGTH_LONG).show();

    }

    @Override
    public void locationData(LocationData locationData) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private void getLongLat() {


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
            location2 = gpsTracker.getLocation(LocationManager.GPS_PROVIDER);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (location2 != null) {

                        getWeatherInfo("", String.valueOf(location2.getLatitude()), String.valueOf(location2.getLongitude()));


                    } else {


                        Toast.makeText(MainActivity.this, "Google not getting location", Toast.LENGTH_SHORT).show();


                    }


                }
            }, 3000);


        } else {
            loading.setVisibility(View.GONE);
            showSettingsAlert("NETWORK");

        }


    }

    public void showSettingsAlert(String provider) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Your Device Location is Off");
        alertDialog.setMessage(" Device Location is not enabled! Want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }


}

