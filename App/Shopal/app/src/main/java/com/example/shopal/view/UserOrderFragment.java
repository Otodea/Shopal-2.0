package com.example.shopal.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.FragmentUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    @BindView(R.id.swipe_to_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.no_order_dialog)
    public LinearLayout noOrderDialog;

    @BindView(R.id.order_summary_layout)
    public CardView orderSummaryCardView;

    @BindView(R.id.progress_layout)
    public CardView progressCardView;

    @BindView(R.id.cart_txt)
    public TextView cartTextView;

    @BindView(R.id.checkout_price)
    public TextView checkoutPriceTextView;


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

    @BindView(R.id.map_button)
    public Button mapButton;

    private String driverId;

    public UserOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserOrderFragment newInstance(String param1, String param2) {
        UserOrderFragment fragment = new UserOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_order, container, false);
        ButterKnife.bind(this, view);

        mapButton.setVisibility(View.INVISIBLE);

        driverId = null;

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = mParam1;

                // Launch Map Tracking Activity
                Intent intent = new Intent(getActivity(), MapLauncherActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("driverId", driverId);

                startActivity(intent);
            }
        });
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                fetchOrderSummaryAndUpdateUI();
            }
        });
    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        // Fetching data from server
        fetchOrderSummaryAndUpdateUI();
    }

    private void fetchOrderSummaryAndUpdateUI() {


        mSwipeRefreshLayout.setRefreshing(true);

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        //TODO change mParam1 to correct name
        String userId = mParam1;
        Call<Order> call = service.getCurrentOrder(userId);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    noOrderDialog.setVisibility(View.GONE);
                    orderSummaryCardView.setVisibility(View.VISIBLE);
                    progressCardView.setVisibility(View.VISIBLE);

                    Order order = response.body();

                    Log.d("Order", response.raw().toString());

                    String status = order.getStatus();
                    driverId = order.getDriverId();

                    //Setting the cart textView and the total checkout price
                    cartTextView.setText(getCartString(order.getShoppingItemList()));
                    checkoutPriceTextView.setText(String.format("%.2f", getTotalCheckoutPrice(order.getShoppingItemList())));


                    if (status == null) {

                        mapButton.setVisibility(View.INVISIBLE);
                        oneProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    } else if (status.equals("Received")) {

                        mapButton.setVisibility(View.INVISIBLE);

                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);




                    } else if (status.equals("Delivering")) {

                        mapButton.setVisibility(View.VISIBLE);
                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    } else if (status.equals("Completed")) {

                        mapButton.setVisibility(View.VISIBLE);

                        oneProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_primary);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.BOLD);

                    } else {

                        mapButton.setVisibility(View.INVISIBLE);

                        oneProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        oneProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        twoProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        twoProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);

                        threeProgressIcon.setBackgroundResource(R.drawable.circle_grey);
                        threeProgressText.setTypeface(oneProgressText.getTypeface(), Typeface.NORMAL);


                    }


                } else { //Order Not found

                    mSwipeRefreshLayout.setRefreshing(false);
                    noOrderDialog.setVisibility(View.VISIBLE);
                    orderSummaryCardView.setVisibility(View.GONE);
                    progressCardView.setVisibility(View.GONE);

                }

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mSwipeRefreshLayout.setRefreshing(false);

    }


    /**
     * Method calculates the the total checkout price
     *
     * @param shoppingItemList
     * @return
     */
    public double getTotalCheckoutPrice(List<ShoppingItem> shoppingItemList) {

        double price = 0.0;

        for (ShoppingItem shoppingItem : shoppingItemList) {

            price += shoppingItem.getPrice() * shoppingItem.getQuantity();
        }

        return price;

    }

    /**
     * Method that returns the cart as a string
     *
     * @param shoppingItemList
     * @return
     */
    public String getCartString(List<ShoppingItem> shoppingItemList) {

        String res = "";
        String str;

        for (ShoppingItem shoppingItem : shoppingItemList) {

            str = shoppingItem.getQuantity() + "x " + shoppingItem.getItemName() + ", ";
            res += str;
        }

        return res;

    }

}
