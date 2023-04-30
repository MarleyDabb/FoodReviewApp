package com.example.foodreview;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodreview.ui.main.MainFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// https://www.jsonschema2pojo.org/

public class MainActivity extends AppCompatActivity {

    private Button search;
    private EditText postcodeInput;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat,lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }

        Places.initialize(getApplicationContext(), "AIzaSyAPgsR4B_WgxxvXLCi20ovyQN94dam5OHE");
        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);


        // https://github.com/googlemaps/android-places-demos
        // https://github.com/googlemaps/android-places-demos/blob/main/demo-java/app/src/main/java/com/example/placesdemo/CurrentPlaceActivity.java

        System.out.println(placesClient);

        search = findViewById(R.id.search);
        postcodeInput = findViewById(R.id.inputPostcode);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("Lat : ", String.valueOf(lat));
                Log.e("Lon : ", String.valueOf(lon));



            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, locationListener);
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID);;

//            String placeId = "ChIJGZCZYVWTfkgRnPrB4yCfg28";
//            final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
//
//            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
//                Place place = response.getPlace();
//                Log.i(TAG, "Place found: " + place.getName());
//            }).addOnFailureListener((exception) -> {
//                if (exception instanceof ApiException) {
//                    final ApiException apiException = (ApiException) exception;
//                    Log.e(TAG, "Place not found: " + exception.getMessage());
//                    final int statusCode = apiException.getStatusCode();
//                    // TODO: Handle error with given status code.
//                }
//            });

            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            Log.e(TAG, "hello 132");

            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        Log.d(TAG,  placeLikelihood.getPlace().getServesDinner().toString());
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode() + apiException.getMessage());
                    }
                }
            });

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && permissions.length > 0 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 50, locationListener);

        }
    }
}