package com.example.foodreview;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodreview.models.ReviewData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantView extends AppCompatActivity {

    Button btn;
    EditText input;
    ListView listView;
    List<ReviewData> reviews = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    DatabaseReference reference = database.getReference();

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;

    TextView rName;
    Button goBack;

    String name;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.get("placeName").toString();
            id = extras.get("placeId").toString();

            Log.e(TAG, name);
        } else {
            Toast.makeText(RestaurantView.this, "Error going to this view", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(RestaurantView.this, MainActivity.class);
            startActivity(i);
            finish();
        }


        btn = findViewById(R.id.reviewSubmit);
        input = findViewById(R.id.reviewInput);
        rName = findViewById(R.id.rName);
        goBack = findViewById(R.id.goBack);
        recyclerView = findViewById(R.id.reviewRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(RestaurantView.this));

        goBack.setOnClickListener(view -> {
            Intent i = new Intent( RestaurantView.this, MainActivity.class);
            startActivity(i);
        });

        rName.setText(name);

        fetchReviews();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            btn.setOnClickListener(view -> {
                String reviewText = input.getText().toString();

                Map<String, Object> review = new HashMap<>();
                review.put("user", user.getDisplayName());
                review.put("text", reviewText);
                review.put("placeId", id);

                db.collection("reviews")
                        .add(review)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(RestaurantView.this, "Successfully added your review", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                fetchReviews();
                                input.setText("");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RestaurantView.this, "There was an error adding your review", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            });

        } else {
            btn.setVisibility(View.GONE);
            input.setVisibility(View.GONE);
        }

    }

    private void fetchReviews() {
        db.collection("reviews").whereEqualTo("placeId", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String user = (String) document.get("user").toString();
                                String text = (String) document.get("text").toString();
                                reviews.add(new ReviewData(user, text, id));
                            }

                            adapter = new ReviewAdapter(reviews, RestaurantView.this);
                            recyclerView.setAdapter(adapter);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}