package com.example.shopal.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.FragmentUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapLauncherActivity extends AppCompatActivity {

    private String userId;
    private String driverId;


    private GoogleMap googleMap;
    private MapView mapView;
    private Bundle bundle;
    private static final String TAG = "Maps Fragment";
    private MarkerOptions trackerOptions;
    private Marker tracker;


    @BindView(R.id.one_progress_icon)
    public TextView oneProgressIcon;

    @BindView(R.id.one_progress_text)
    public TextView oneProgressText;

    @BindView(R.id.two_progress_icon)
    public TextView twoProgressIcon;

    @BindView(R.id.two_progress_text)
    public TextView twoProgressText;

    @BindView(R.id.three_progress_icon)
    public TextView threeProgressIcon;

    @BindView(R.id.three_progress_text)
    public TextView threeProgressText;

    Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        userId = intent.getStringExtra("userId");
        driverId = intent.getStringExtra("driverId").replaceAll("@", "").replaceAll("[.]","");

        RelativeLayout bottomSheet = findViewById(R.id.map_bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                fetchOrderSummaryAndUpdateUI();
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMaxZoomPreference(18);
                loginToFirebase();
            }
        });

    }


    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        // Authenticate with Firebase and subscribe to updates
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates();
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        tracker = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void subscribeToUpdates() {

        Log.d("Driver", driverId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                if(dataSnapshot.getKey().equals(driverId))
                    setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                if(dataSnapshot.getKey().equals(driverId))
                    setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the

        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

        Log.d("Driver",dataSnapshot.toString());
        dataSnapshot.hasChild(driverId);
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);

        if(tracker == null) {
            trackerOptions = new MarkerOptions();
            trackerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_shopping_cart_black_24dp));
            trackerOptions.position(location);
            tracker = googleMap.addMarker(trackerOptions);
        }

        tracker.setPosition(location);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(tracker.getPosition());

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void fetchOrderSummaryAndUpdateUI() {

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<Order> call = service.getCurrentOrder(userId);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()) {



                    Order order = response.body();

                    Log.d("Order", response.raw().toString());

                    String status = order.getStatus();


                    if(status == null){


                        oneProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    }else if (status.equals("Received")) {



                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    } else if (status.equals("Delivering")) {


                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    } else if(status.equals("Completed")){

                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                    }
                    else{

                        oneProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                    }


                }

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
            }
        });

    }

}
