package com.example.shopal.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.NumberPicker;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.example.shopal.R;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.remote.ApiService;
import com.example.shopal.model.remote.RetrofitClientInstance;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.StoreAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;


public class StoreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, StoreAdapter.GridItemClickListener {

    private static final int NUMCOLS = 2;

    @BindView(R.id.recycler_view_store)
    public RecyclerView mRecyclerView;

    @BindView(R.id.swipe_to_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private StoreAdapter mStoreAdapter;

    List<ShoppingItem> shoppingItemList;
    private CartDatabase mDb;

    public StoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoreFragment newInstance(String param1, String param2) {
        StoreFragment fragment = new StoreFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();

        SearchView searchView = (SearchView)view.findViewById(R.id.search_view);
        searchView.setActivated(true);
        searchView.setQueryHint("Search for groceries here");
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mStoreAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get Database instance
        mDb = CartDatabase.getInstance(getActivity());

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
     * Method to init recycler view
     */
    public void initRecyclerView() {

        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUMCOLS));

        //Improve performance
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        mStoreAdapter = new StoreAdapter(this, getActivity());
        mRecyclerView.setAdapter(mStoreAdapter);
    }

    public void loadDataToRecyclerView() {

        mSwipeRefreshLayout.setRefreshing(true);

        /*Create handle for the RetrofitInstance interface*/
        ApiService service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);

        Call<List<ShoppingItem>> call = service.getAllShoppingItems();

        call.enqueue(new Callback<List<ShoppingItem>>() {
            @Override
            public void onResponse(Call<List<ShoppingItem>> call, Response<List<ShoppingItem>> response) {

                if (response.isSuccessful()) {

                    mSwipeRefreshLayout.setRefreshing(false);
                    mStoreAdapter.setStoreData(response.body());

                } else {

                    Toast.makeText(getActivity(), "Store Empty", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);

                }

            }

            @Override
            public void onFailure(Call<List<ShoppingItem>> call, Throwable t) {

                Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

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
    public void onListItemClick(ShoppingItem shoppingItem, final View v) {

        final ShoppingItem shoppingItem1 = shoppingItem;
        final AlertDialog.Builder d = new AlertDialog.Builder(getActivity());
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
}
