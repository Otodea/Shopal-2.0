package com.example.shopal.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.CartBody;
import com.example.shopal.model.local.POJO.CheckoutResponse;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.ShoppingCartAdapter;
import com.example.shopal.viewmodel.CartViewModel;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;
import static android.view.View.GONE;

/**
 * Shopping Cart Fragment
 */

public class ShoppingCartFragment extends Fragment implements ShoppingCartAdapter.ListItemClickListener {

    private static final String EMAIL = "param1";
    private static final String USER_TYPE = "param2";
    private static final String NAME = "param3";
    private static final String MOBILE_NUMBER = "param4";
    private static final String ADDRESS = "param5";



    @BindView(R.id.recycler_view_shopping_cart)
    public RecyclerView mRecyclerView;

    @BindView(R.id.empty_cart_dialog)
    public LinearLayout emptyCartDialog ;

    @BindView(R.id.checkout_layout)
    public CardView checkoutCardView;

    @BindView(R.id.checkout_button)
    public Button checkoutButton;

    @BindView(R.id.checkout_price)
    public TextView checkoutPrice;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @BindView(R.id.recommend_button)
    public Button reccomendButton;

    private ShoppingCartAdapter mShoppingCartAdapter;
    private CartDatabase mDb;


    private String email;
    private String userType;
    private String name;
    private String mobile;
    private String destinationAddress;
    private String address;

    List<ShoppingItem> shoppingItemList;


    public ShoppingCartFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(String email, String userType, String name, String mobile, String address) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER_TYPE, userType);
        args.putString(NAME, name);
        args.putString(MOBILE_NUMBER, mobile);
        args.putString(ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            email = getArguments().getString(EMAIL);
            userType = getArguments().getString(USER_TYPE);
            name = getArguments().getString(NAME);
            mobile = getArguments().getString(MOBILE_NUMBER);
            address = getArguments().getString(ADDRESS);


        }

        shoppingItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get Database instance
        mDb = CartDatabase.getInstance(getActivity());

        //Init
        setupViewModel();
        setupCheckOutButton();
        setupRecommendButton();
    }


    /**
     * Method to setup MainViewModel
     */
    private void setupViewModel() {


        CartViewModel viewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        //Observing changes to the database
        viewModel.getShoppingItems().observe(this, new Observer<List<ShoppingItem>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingItem> shoppingItems) {

                if (shoppingItems.size() == 0) {
                    checkoutCardView.setVisibility(GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    emptyCartDialog.setVisibility(View.VISIBLE);

                }else {

                    emptyCartDialog.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    checkoutCardView.setVisibility(View.VISIBLE);
                    mShoppingCartAdapter.setShoppingItemsData(shoppingItems);
                    updateCheckoutPrice();
                }
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


        //Touch helper to the RecyclerView to recognize when a user swipes to delete an item.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<ShoppingItem> shoppingItemList = mShoppingCartAdapter.getShoppingItemList();
                        ShoppingItem shoppingItem = shoppingItemList.get(position);
                        mDb.cartDao().deleteShoppingItem(shoppingItem);
                    }
                });

            }
        }).attachToRecyclerView(mRecyclerView);

    }

    /**
     * Method to setup recommendButton
     */
    public void setupRecommendButton(){
        reccomendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RecommendationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("userType", userType);
                intent.putExtra("name", name);
                intent.putExtra("mobile", mobile);
                intent.putExtra("destinationAddress", destinationAddress);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });
    }

    /**
     * Method to setup checkoutButton
     */
    public void setupCheckOutButton() {


        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creating Alert Dialog
                final AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.order_confirmation_dialog, null);
                d.setTitle("Order Confirmation");
                d.setMessage("Please confirm your order");
                d.setView(dialogView);

                //Getting the views
                final EditText destinationAddressEditText = (EditText) dialogView.findViewById(R.id.destination_address);
                final TextView priceTextView = (TextView) dialogView.findViewById(R.id.total_price);
                destinationAddressEditText.setText(address);
                priceTextView.setText(checkoutPrice.getText());

                d.setPositiveButton("Checkout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        destinationAddress = destinationAddressEditText.getText().toString().trim();

                        if (TextUtils.isEmpty(destinationAddress)) {

                            destinationAddressEditText.setError("Can't leave field empty.");
                            Toast.makeText(getActivity(), "Enter valid destination address", Toast.LENGTH_LONG).show();
                            return;
                        }

                        postOrderToServer();


                    }
                });

                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                     //Do nothing

                    }
                });

                //Show Dialog
                AlertDialog alertDialog = d.create();
                alertDialog.show();


            }
        });


    }


    public void postOrderToServer(){


        progressBar.setVisibility(View.VISIBLE);

        //Create Order Body
        Order order = new Order();
        order.setUserId(email);
        order.setDestinationAddress(destinationAddress);
        order.setName(name);
        order.setMobile(mobile);
        order.setShoppingItemList(mShoppingCartAdapter.getShoppingItemList());
        order.setStatus("Received");

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<Order> call = service.checkoutCart(order);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {



                if (response.isSuccessful()) {

                    //Clear Cart from local db
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            mDb.cartDao().deleteCart();
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Your Order has been placed.", Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(getActivity(), "Cannot place a new order when order is in progress.", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });



    }

    /**
     * Method to update checkout price
     */
    public void updateCheckoutPrice(){

        double checkoutPriceValue = mShoppingCartAdapter.getTotalCheckoutPrice();
        checkoutPrice.setText("$" + String.format("%.2f", checkoutPriceValue));
    }


    @Override
    public void onListItemClick(ShoppingItem shoppingItem) {

        //Do Nothing for now

    }

}
