package com.example.shopal.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.StoreAdapter;
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

public class RecommendationActivity extends AppCompatActivity implements StoreAdapter.GridItemClickListener {

    private static final int NUMCOLS = 1;

    @BindView(R.id.recycler_view_store)
    public RecyclerView mRecyclerView;

    private StoreAdapter mRecommendAdapter;

    List<ShoppingItem> reccomendItemList;
    List<ShoppingItem> cart;
    private CartDatabase mDb;


    private String email;
    private String userType;
    private String name;
    private String mobile;
    private String destinationAddress;
    private String address;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        userType = intent.getStringExtra("userType");
        name = intent.getStringExtra("name");
        mobile = intent.getStringExtra("mobile");
        destinationAddress = intent.getStringExtra("destinationAddress");
        address = intent.getStringExtra("address");

        reccomendItemList = new ArrayList<>();
        cart = new ArrayList<>();
        mDb = CartDatabase.getInstance(this);


        ButterKnife.bind(this);
        initRecyclerView();
        setupViewModel();
    }




    @Override
    public void onListItemClick(ShoppingItem shoppingItem, final View v) {

        final ShoppingItem shoppingItem1 = shoppingItem;
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle(shoppingItem.getItemName());
        d.setMessage("$" + Double.toString(shoppingItem.getPrice()));
        d.setView(dialogView);
        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(1000);
        numberPicker.setMinValue(0);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mDb.cartDao().loadShoppingItemById(shoppingItem1.getId()) != null) {
                    numberPicker.setValue(shoppingItem1.getQuantity());
                }else {
                    numberPicker.setValue(1);
                }
            }
        });



        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");

            }
        });
        d.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: " + numberPicker.getValue());

                int numPickerValue = numberPicker.getValue();
                shoppingItem1.setQuantity(numPickerValue);
                if (numPickerValue != 0) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.cartDao().insertShoppingItem(shoppingItem1);
                        }
                    });
                } else {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.cartDao().deleteShoppingItem(shoppingItem1);
                        }
                    });
                }

                CheckedTextView check = (CheckedTextView) v.findViewById(R.id.itemSelected);
                if(shoppingItem1.getQuantity() > 0) {
                    check.setVisibility(View.VISIBLE);
                    check.setChecked(true);
                    check.setCheckMarkDrawable(R.drawable.ic_check_primary_24dp);
                } else {
                    check.setChecked(false);
                    check.setVisibility(View.INVISIBLE);
                }


            }
        });
        d.setNegativeButton("Remove All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shoppingItem1.setQuantity(0);
                numberPicker.setValue(1);
                CheckedTextView check = (CheckedTextView) v.findViewById(R.id.itemSelected);
                check.setChecked(false);
                check.setVisibility(View.INVISIBLE);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDb.cartDao().deleteShoppingItem(shoppingItem1);
                    }
                });


            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    private void setupViewModel() {


        CartViewModel viewModel = ViewModelProviders.of(this).get(CartViewModel.class);

        //Observing changes to the database
        viewModel.getShoppingItems().observe(this, new Observer<List<ShoppingItem>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingItem> shoppingItems) {

                if (shoppingItems.size() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                }else {
                    cart = shoppingItems;
                    mRecyclerView.setVisibility(View.VISIBLE);
                    postOrderToServer();

                }
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
        order.setShoppingItemList(cart);
        order.setStatus("Received");

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<Order> call = service.recommendItems(order);

        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mRecommendAdapter.setStoreData(response.body().getShoppingItemList());
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "No Recommended Items", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "Check your internet connection", Toast.LENGTH_LONG).show();
            }
        });



    }

    public void initRecyclerView() {

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMCOLS));

        //Improve performance
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mRecommendAdapter = new StoreAdapter(this, this);
        mRecyclerView.setAdapter(mRecommendAdapter);
    }
}
