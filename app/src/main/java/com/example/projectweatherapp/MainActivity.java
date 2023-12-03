package com.example.projectweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    //default location = Tampere
    private String selectedLocation = "Tampere";
    public void setSelectedLocation(String loc){
        this.selectedLocation = loc;
    }
    public String getSelectedLocation(){
        return this.selectedLocation;
    }

    private boolean preciseLocationEnabled = false;
    public void setPreciseLocationEnabled(boolean isEnabled){ this.preciseLocationEnabled = isEnabled;}
    public boolean getIsPreciseLocationEnabled(){ return this.preciseLocationEnabled; }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        String locationFromSettings = intent.getStringExtra("LOCATION_SELECTED");
        boolean preciseLocationEnabled = intent.getBooleanExtra("PRECISE_LOCATION_ENABLED", false);


        if(locationFromSettings != null){
            setSelectedLocation(locationFromSettings);
        }
        setPreciseLocationEnabled(preciseLocationEnabled);
        if(preciseLocationEnabled){
            startGPS(null);
        }
        updateWeather(null);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        selectedLocation = savedInstanceState.getString("LOCATION_SELECTED", "Tampere");
        setSelectedLocation(selectedLocation);
        TextView currentLocation = findViewById(R.id.selectedLocation);
        currentLocation.setText(getSelectedLocation());

        preciseLocationEnabled = savedInstanceState.getBoolean("PRECISE_LOCATION_ENABLED", false);
        setPreciseLocationEnabled(preciseLocationEnabled);

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("LOCATION_SELECTED", getSelectedLocation());
        bundle.putBoolean("PRECISE_LOCATION_ENABLED", getIsPreciseLocationEnabled());
    }

    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    @SuppressLint("DefaultLocale")
    public void updateWeather(View view) {

        String API_KEY=" API_KEY HERE ";
        String WEATHER_URL;

        if (!preciseLocationEnabled){
            String location = getSelectedLocation();
            WEATHER_URL = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", location, API_KEY);
        }
        else{
            double lat = getLatitude();
            double lng = getLongitude();

            WEATHER_URL = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%.2f&lon=%.2f&appid=%s&units=metric", lat, lng, API_KEY);
        }

        StringRequest request = new StringRequest(Request.Method.GET, WEATHER_URL, this::parseWeatherJsonAndUpdateUi, error -> Toast.makeText(this, "Internet Error", Toast.LENGTH_LONG).show());
        Volley.newRequestQueue(this).add(request);
    }

    public void startGPS(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        String bestProvider = locationManager.getBestProvider(criteria, true);

        if (bestProvider != null) {
            locationManager.requestLocationUpdates(bestProvider, 10000, 10, location -> {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                setLongitude(longitude);
                setLatitude(latitude);
            });
        } else {
            Toast.makeText(this, "No suitable location provider found", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    private void parseWeatherJsonAndUpdateUi(String response){
        try {
            JSONObject weatherJSON = new JSONObject(response);
            String weather = weatherJSON.getJSONArray("weather").getJSONObject(0).getString("main");
            String location = weatherJSON.getString("name");
            double temperature = weatherJSON.getJSONObject("main").getDouble("temp");
            double windSpeed = weatherJSON.getJSONObject("wind").getDouble("speed");

            TextView weatherTextView = findViewById(R.id.descriptionValueTextView);
            TextView currentLocationTextView = findViewById(R.id.selectedLocation);
            TextView temperatureTextView = findViewById(R.id.temperatureValueTextView);
            TextView windSpeedTextView = findViewById(R.id.windspeedValueTextView);

            weatherTextView.setText(weather);
            currentLocationTextView.setText(location);
            temperatureTextView.setText(String.format(" %.1f C", temperature));
            windSpeedTextView.setText(String.format(" %.1f m/s", windSpeed));

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, settingsActivity.class);
        intent.putExtra("PRECISE_LOCATION_ENABLED", getIsPreciseLocationEnabled());
        intent.putExtra("LOCATION_SELECTED", getSelectedLocation());
        startActivity(intent);
    }
}