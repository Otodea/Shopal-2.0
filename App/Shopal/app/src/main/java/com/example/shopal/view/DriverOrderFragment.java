package com.example.shopal.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.shopal.R;
import com.example.shopal.model.local.POJO.CartBody;
import com.example.shopal.model.local.POJO.LoginBody;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.POJO.OrderBody;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.FragmentUtils;
import com.example.shopal.utils.ShoppingCartAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ShoppingCartAdapter.ListItemClickListener{


    private static final String EMAIL = "param0";
    private static final String USER_TYPE = "param1";


    private String email;
    private String userType;
    private String userId;

    @BindView(R.id.recycler_view_shopping_cart)
    public RecyclerView mRecyclerView;


    @BindView(R.id.no_order_dialog)
    public LinearLayout noOrderDialog ;

    @BindView(R.id.order_info)
    public CardView orderInfoCardView;

    @BindView(R.id.cart_label)
    public CardView cartLabelCardView;


    @BindView(R.id.accept_layout)
    public CardView acceptCardView;

    @BindView(R.id.delivering_button)
    public Button deliveringButton;

    @BindView(R.id.reached_button)
    public Button reachedButton;

    @BindView(R.id.done_button)
    public Button doneButton;

    @BindView(R.id.swipe_to_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    public @BindView(R.id.destination_address)
    TextView destinationAddressTextView;
    public @BindView(R.id.name)
    TextView nameTextView;
    public @BindView(R.id.mobile)
    TextView mobileTextView;
    public @BindView(R.id.name_image_view)
    ImageView nameImageView;

    private ShoppingCartAdapter mShoppingCartAdapter;

    public DriverOrderFragment() {
        // Required empty public constructor
    }


    public static DriverOrderFragment newInstance(String email, String userType) {
        DriverOrderFragment fragment = new DriverOrderFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER_TYPE, userType);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            userType = getArguments().getString(USER_TYPE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_order, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupDeliveringButton();
        setupReachedButton();
        setupDoneButton();

        mSwipeRefreshLayout.setOnRefreshListener(this);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                populateUI();
            }
        });


    }


    /**
     * Method to initialize recyclerView
     */
    public void initRecyclerView() {

        //RecyclerViewDefinition
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //Improving performance
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mShoppingCartAdapter = new ShoppingCartAdapter(this, getActivity());
        mRecyclerView.setAdapter(mShoppingCartAdapter);


    }


    public void populateUI() {


        mSwipeRefreshLayout.setRefreshing(true);

       /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
        Call<Order> call = service.getDriverOrder(email);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    noOrderDialog.setVisibility(View.GONE);
                    orderInfoCardView.setVisibility(View.VISIBLE);
                    cartLabelCardView.setVisibility(View.VISIBLE);
                    acceptCardView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);



                    Order order = response.body();

                    //Setting the Order Info
                    userId = order.getUserId();
                    destinationAddressTextView.setText(order.getDestinationAddress());
                    nameTextView.setText(order.getName());
                    mobileTextView.setText(order.getMobile());

                    TextDrawable drawable = TextDrawable.builder()
                            .buildRoundRect( order.getName().substring(0,1) ,
                                    ContextCompat.getColor(getActivity(), R.color.colorPrimary), 100);
                    nameImageView.setImageDrawable(drawable);

                    mShoppingCartAdapter.setShoppingItemsData(order.getShoppingItemList());



                }
                else{
                    noOrderDialog.setVisibility(View.VISIBLE);
                    orderInfoCardView.setVisibility(View.GONE);
                    cartLabelCardView.setVisibility(View.GONE);
                    acceptCardView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);


                }

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });




    }



    /**
     * Method to setup delivering button
     */
    public void setupDeliveringButton() {



        deliveringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reachedButton.setClickable(true);
                // Launch Driver Order Activity
                deliveringButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.lightGrey));
                Intent intent = new Intent(getActivity(), DriverBarcodeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);

            }
        });

    }


    /**
     * Method to setup delivering button
     */
    public void setupReachedButton() {

        reachedButton.setClickable(false);
        reachedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doneButton.setClickable(true);
                mSwipeRefreshLayout.setRefreshing(true);

                /*Create handle for the RetrofitInstance interface*/
                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

                OrderBody orderBody = new OrderBody();
                orderBody.setDriverId(email);
                orderBody.setStatus("Completed");


                Call<Order> call = service.updateOrderStatus(orderBody);

                call.enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {


                        if (response.isSuccessful()) {

                            mSwipeRefreshLayout.setRefreshing(false);
                            reachedButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.lightGrey));
                            reachedButton.setClickable(false);



                        } else {

                            Toast.makeText(getActivity(), "Couldn't complete order", Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);

                        }

                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {

                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(true);

                    }
                });

            }
        });


    }



    /**
     * Method to setup done button
     */
    public void setupDoneButton() {

        doneButton.setClickable(false);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSwipeRefreshLayout.setRefreshing(true);

                /*Create handle for the RetrofitInstance interface*/
                ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

                OrderBody orderBody = new OrderBody();
                orderBody.setId(userId);
                doneButton.setBackgroundTintList(ContextCompat.getColorStateList(getActivity(), R.color.lightGrey));

                Call<Order> call = service.deleteOrder(orderBody);

                call.enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {


                        if (response.isSuccessful()) {

                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), "Congrats! on delivering the order", Toast.LENGTH_LONG).show();
                            populateUI();



                        } else {

                            Toast.makeText(getActivity(), "Couldn't complete order", Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);

                        }

                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {

                        Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(true);

                    }
                });

            }
        });

    }




    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        populateUI();
    }


    @Override
    public void onListItemClick(ShoppingItem shoppingItem) {

        //Do Nothing for now


    }

}
