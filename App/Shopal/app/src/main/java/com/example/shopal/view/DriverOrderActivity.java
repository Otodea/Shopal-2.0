package com.example.shopal.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.shopal.R;
import com.example.shopal.model.local.POJO.CartBody;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.POJO.OrderBody;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.DriverTrackingService;
import com.example.shopal.utils.ShoppingCartAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverOrderActivity extends AppCompatActivity implements ShoppingCartAdapter.ListItemClickListener{


    private String userId;
    private String driverId;
    private String location;
    private String name;
    private String mobile;
    private static final int PERMISSIONS_REQUEST = 1;


    @BindView(R.id.recycler_view_shopping_cart)
    public RecyclerView mRecyclerView;

    @BindView(R.id.accept_layout)
    public CardView acceptCardView;

    @BindView(R.id.accept_button)
    public Button acceptButton;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    public @BindView(R.id.destination_address)
    TextView destinationAddressTextView;
    public @BindView(R.id.name)
    TextView nameTextView;
    public @BindView(R.id.mobile)
    TextView mobileTextView;
    public @BindView(R.id.name_image_view)
    ImageView nameImageView;

    private ShoppingCartAdapter mShoppingCartAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order);

        ButterKnife.bind(this);

        Intent intent = getIntent();

        userId = intent.getStringExtra("userId");
        location = intent.getStringExtra("location");
        name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        driverId = intent.getStringExtra("driverId");

        initRecyclerView();
        setupAcceptButton();
        populateUI();

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d("Tracking Service", "launching service");
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

    }


    /**
     * Method to initialize recyclerView
     */
    public void initRecyclerView() {

        //RecyclerViewDefinition
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //Improving performance
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mShoppingCartAdapter = new ShoppingCartAdapter(this, this);
        mRecyclerView.setAdapter(mShoppingCartAdapter);


    }

    private void startTrackerService() {
        Log.d("Tracking Service", "above service");

        Intent startIntent = new Intent(this, DriverTrackingService.class);
        startIntent.putExtra("driverId", driverId);
        Log.d("Tracking Service", "starting service");
        startService(startIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }


    public void populateUI() {


        //Setting the Order Info
        destinationAddressTextView.setText(location);
        nameTextView.setText(name);
        mobileTextView.setText(mobile);

        TextDrawable drawable = TextDrawable.builder()
                .buildRoundRect( name.substring(0,1) ,
                        ContextCompat.getColor(this, R.color.colorPrimary), 100);
        nameImageView.setImageDrawable(drawable);


        //Getting Cart Info from Server
        progressBar.setVisibility(View.VISIBLE);

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<Order> call = service.getCurrentOrder(userId);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);

                    Order order = response.body();
                    mShoppingCartAdapter.setShoppingItemsData(order.getShoppingItemList());

                }
                else{

                    Toast.makeText(getApplicationContext(), "You have an order already in progress!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });




    }


    /**
     * Method to setup checkoutButton
     */
    public void setupAcceptButton() {


        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progressBar.setVisibility(View.VISIBLE);

                //Create Order Body

                /*Create handle for the RetrofitInstance interface*/
                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

                OrderBody orderBody = new OrderBody();
                orderBody.setId(userId);
                orderBody.setDriverId(driverId);
                orderBody.setStatus("Received");

                Call<Order> call = service.acceptOrder(orderBody);

                call.enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {


                        if (response.isSuccessful()) {

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Your have accepted the order", Toast.LENGTH_LONG).show();

                            finish();


                        } else {

                            Toast.makeText(getApplicationContext(), "You already have an order in progress", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }

                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {

                        Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);

                    }
                });


            }
        });


    }


    @Override
    public void onListItemClick(ShoppingItem shoppingItem) {

        //Do Nothing for now


    }
}
