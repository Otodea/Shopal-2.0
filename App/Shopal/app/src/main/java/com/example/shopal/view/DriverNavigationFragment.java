package com.example.shopal.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.shopal.R;
import com.example.shopal.model.local.POJO.User;
import com.example.shopal.utils.CustomViewPager;
import com.example.shopal.utils.ViewPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DriverNavigationFragment extends Fragment {

    private static final String EMAIL = "param1";
    private static final String USER_TYPE = "param2";

    private String email;
    private String userType;

    @BindView(R.id.viewpager)
    public CustomViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.navigation)
    public BottomNavigationView navigation;

    public DriverNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @param userType Parameter 2.
     * @return A new instance of fragment DriverNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverNavigationFragment newInstance(String email, String userType) {
        DriverNavigationFragment fragment = new DriverNavigationFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_orders:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_order:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_profile:
                    viewPager.setCurrentItem(2);
                    return true;

            }
            return false;
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        if (getArguments() != null) {
            email = getArguments().getString(EMAIL);
            userType = getArguments().getString(USER_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =  inflater.inflate(R.layout.fragment_driver_navigation, container, false);
       ButterKnife.bind(this,view);

       navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViewPagerAdapter();
    }

    /**
     * Method that sets up ViewPagerAdapter
     */
    private void setupViewPagerAdapter() {
        viewPagerAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());

        //Creating the fragment instances
        DriverOrdersFragment driverOrdersFragment = DriverOrdersFragment.newInstance(email, userType);
        DriverOrderFragment driverOrderSummaryFragment = DriverOrderFragment.newInstance(email, userType);
        UserFragment userFragment = UserFragment.newInstance(email, userType);


        //Add all fragments to viewpager adapter
        viewPagerAdapter.addFragment(driverOrdersFragment, "Orders");
        viewPagerAdapter.addFragment(driverOrderSummaryFragment, "Order Summary");
        viewPagerAdapter.addFragment(userFragment, "Profile");


        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

    }

}
