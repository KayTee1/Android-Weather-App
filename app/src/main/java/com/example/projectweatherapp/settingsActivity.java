package com.example.projectweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class settingsActivity extends AppCompatActivity {
    private String locationSelected = "Tampere";
    public void setLocationSelected(String locSelected){
        this.locationSelected = locSelected;
    }
    public String getLocationSelected(){
        return this.locationSelected;
    }

    private boolean preciseLocationEnabled = false;
    public void setPreciseLocationEnabled(boolean isEnabled){ this.preciseLocationEnabled = isEnabled;}
    public boolean getIsPreciseLocationEnabled(){ return this.preciseLocationEnabled; }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        setPreciseLocationEnabled(intent.getBooleanExtra("PRECISE_LOCATION_ENABLED", false));
        setLocationSelected(intent.getStringExtra("LOCATION_SELECTED"));

        if(getIsPreciseLocationEnabled()){
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch usePreciseLocation = findViewById(R.id.preciseLocation_switch);
            usePreciseLocation.setChecked(getIsPreciseLocationEnabled());
        }

        if(!getLocationSelected().isEmpty()){
            EditText locationEditText = findViewById(R.id.countrySelectorEditText);
            locationEditText.setText(getLocationSelected());
        }
    }

    public void backToMain(View view) {
        EditText locationSelector = findViewById(R.id.countrySelectorEditText);

        String selectedLocation = locationSelector.getText().toString().trim();
        if (!selectedLocation.isEmpty()) {
            setLocationSelected(selectedLocation);
        }

        Switch usePreciseLocation = findViewById(R.id.preciseLocation_switch);

        boolean isPreciseLocationEnabled = usePreciseLocation.isChecked();
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("LOCATION_SELECTED", getLocationSelected());

        intent.putExtra("PRECISE_LOCATION_ENABLED", isPreciseLocationEnabled);
        startActivity(intent);
    }

}