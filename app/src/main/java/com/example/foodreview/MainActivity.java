package com.example.foodreview;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodreview.ui.main.MainFragment;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// https://www.jsonschema2pojo.org/

public class MainActivity extends AppCompatActivity {

    private Button searchLocation;
    private Button currentLocation;
    private TextView loggedInText;

    LocationManager locationManager;
    LocationListener locationListener;
    double lat,lon;

    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;

    List<Place> placesList = new ArrayList<>();;
    List<Place> localPlaces = new ArrayList<>();;



    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        placesList.add(place);
                        Log.i(TAG, place.getName());


                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete");
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }


        searchLocation = findViewById(R.id.searchLocation);
        currentLocation = findViewById(R.id.currentLocation);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));



        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placesList.clear();
                Log.i(TAG, "Search btn clicked");
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(MainActivity.this);

                startAutocomplete.launch(intent);



            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (placesList != null) placesList.clear();
                placesList = localPlaces;

                for (Place test : localPlaces) {
                    Log.e(TAG, test.getName());
                }

                adapter = new RecyclerAdapter(placesList, MainActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });



        Places.initialize(getApplicationContext(), "AIzaSyAPgsR4B_WgxxvXLCi20ovyQN94dam5OHE");
        PlacesClient placesClient = Places.createClient(this);

        // https://github.com/googlemaps/android-places-demos
        // https://github.com/googlemaps/android-places-demos/blob/main/demo-java/app/src/main/java/com/example/placesdemo/CurrentPlaceActivity.java


//        postcodeInput = findViewById(R.id.inputPostcode);
        loggedInText = findViewById(R.id.loggedInText);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loggedInText.setText(String.format("Welcome, %s", user.getDisplayName()));
        } else {
            loggedInText.setText("You are not logged in");
        }

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

            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.TYPES);
            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);

            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Place place = placeLikelihood.getPlace();

                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                place.getName(),
                                placeLikelihood.getLikelihood()));

                        try {
                            if (place.getTypes().contains(Place.Type.RESTAURANT)) {
                                localPlaces.add(place);

                                Log.i(TAG, "THIS IS A RESTAURANT ");
                            } else {
                                Log.i(TAG, "NOT A RESTAURANT");
                            }
                        } catch(NullPointerException err) {
                            Log.e(TAG, err.getMessage());
                        }

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

/*

GET PHOTO

 // Get photo
                        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                        if (metadata == null || metadata.isEmpty()) {
                            Log.w(TAG, "No photo metadata.");
                        } else {
                            final PhotoMetadata photoMetadata = metadata.get(0);
                            // Get the attribution text.
                            final String attributions = photoMetadata.getAttributions();

                            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(500) // Optional.
                                    .setMaxHeight(300) // Optional.
                                    .build();
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                ImageView imageView = new ImageView(this);
                                imageView.setImageBitmap(bitmap);

                                Log.e(TAG, "found image");
                            }).addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    final ApiException apiException = (ApiException) exception;
                                    Log.e(TAG, "Place not found: " + exception.getMessage());
                                    final int statusCode = apiException.getStatusCode();
                                    Log.e(TAG, "some error");
                                    // TODO: Handle error with given status code.
                                }
                            });
                        }
 */