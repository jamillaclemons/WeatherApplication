package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import data.Channel;
import data.Item;
import service.WeatherServiceCallback;
import service.YahooWeatherService;

public class MainActivity extends AppCompatActivity implements WeatherServiceCallback {

    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextview;

    private YahooWeatherService service;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
       locationTextview = (TextView) findViewById(R.id.locationTextView);

       service = new YahooWeatherService(this);
       dialog = new ProgressDialog(this);
       dialog.setMessage("Loading...");
       dialog.show();


       service .refreshWeather("Austin,TX");
    }

    @Override
    public void serviceSuccess(Channel channel) {
        dialog.hide();

        final Item item = channel.getItem();

        int resourceId = getResources().getIdentifier("drawable/icon_" + item.getCondition().getCode(),null, getPackageName());

        // "GENERATE" getDrawable(resorceId, null);  DELETE null and comma FIRST
        // How it should look:  @SuppresWarnings("deprecation") Drawable weatherIconDrawable = getResources().getDrawable(resourceId);

        Drawable weatherIconDrawable = getResources().getDrawable(resourceId, null);

        weatherIconImageView.setImageDrawable(weatherIconDrawable);

        temperatureTextView.setText(item.getCondition().getTemperature() + "\u00B0" + channel.getUnits().getTemperature());
        conditionTextView.setText(item.getCondition().getDescription());
        locationTextview.setText(service.getLocation());


    }



    @Override
    public void serviceFailure(Exception exception) {
        dialog.hide();
        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();


    }
}
