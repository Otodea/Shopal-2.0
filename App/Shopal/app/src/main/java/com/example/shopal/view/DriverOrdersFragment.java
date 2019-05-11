package com.example.shopal.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.database.CartDatabase;

import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.FragmentUtils;
import com.example.shopal.utils.OrderAdapter;
import com.example.shopal.utils.StoreAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DriverOrdersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OrderAdapter.ListItemClickListener {


    private static final String EMAIL = "param1";
    private static final String USER_TYPE = "param2";

    private String email;
    private String userType;



    @BindView(R.id.recycler_view_orders)
    public RecyclerView mRecyclerView;

    @BindView(R.id.swipe_to_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @BindView(R.id.no_order_dialog)
    public LinearLayout noOrderDialog ;


    private OrderAdapter mOrderAdapter;




    public DriverOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @param userType Parameter 2.
     * @return A new instance of fragment DriverOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverOrdersFragment newInstance(String email, String userType) {
        DriverOrdersFragment fragment = new DriverOrdersFragment();
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
        View view = inflater.inflate(R.layout.fragment_driver_orders, container, false);
        ButterKnife.bind(this, view);

        initRecyclerView();

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("TEST", "onResume");
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
                loadDataToRecyclerView();
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

        mOrderAdapter = new OrderAdapter(this, getActivity());
        mRecyclerView.setAdapter(mOrderAdapter);


    }

    public void loadDataToRecyclerView() {

        mSwipeRefreshLayout.setRefreshing(true);

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<List<Order>> call = service.getPendingOrders();

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {

                if (response.isSuccessful()) {

                    mSwipeRefreshLayout.setRefreshing(false);

                    mOrderAdapter.setOrdersData(response.body());

                    if(response.body().size() == 0)
                        noOrderDialog.setVisibility(View.VISIBLE);
                    else
                        noOrderDialog.setVisibility(View.GONE);

                } else {

                    Toast.makeText(getActivity(), "Failed to get Pending orders", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);

                }

            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {

                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


//
    }


    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        loadDataToRecyclerView();
    }
    @Override
    public void onListItemClick(Order order) {

        String userId = order.getUserId();
        String location = order.getDestinationAddress();
        String name = order.getName();
        String mobile = order.getMobile();

        // Launch Driver Order Activity
        Intent intent = new Intent(getActivity(), DriverOrderActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("location", location);
        intent.putExtra("name", name);
        intent.putExtra("mobile", mobile);
        intent.putExtra("driverId", email);

        startActivity(intent);

    }
}
