package com.example.shopal.view;

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
import android.widget.TextView;

import com.example.shopal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.shopal.utils.CustomViewPager;
import com.example.shopal.utils.ViewPagerAdapter;


public class UserNavigationFragment extends Fragment {

    private static final String EMAIL = "param1";
    private static final String USER_TYPE = "param2";
    private static final String NAME = "param3";
    private static final String MOBILE_NUMBER = "param4";
    private static final String ADDRESS = "param5";


    private String email;
    private String userType;
    private String name;
    private String mobile;
    private String address;


    @BindView(R.id.viewpager)
    public CustomViewPager viewPager;

    private ViewPagerAdapter viewPagerAdapter;

    @BindView(R.id.navigation)
    public BottomNavigationView navigation;


    public UserNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param email Parameter 1.
     * @param userType Parameter 2.
     * @return A new instance of fragment UserNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserNavigationFragment newInstance(String email, String userType, String name, String mobile, String address) {
        UserNavigationFragment fragment = new UserNavigationFragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(USER_TYPE, userType);
        args.putString(NAME, name);
        args.putString(MOBILE_NUMBER, mobile);
        args.putString(ADDRESS, address);
        fragment.setArguments(args);
        return fragment;
    }



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_store:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_cart:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_order:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_profile:
                    viewPager.setCurrentItem(3);
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
            name = getArguments().getString(NAME);
            mobile = getArguments().getString(MOBILE_NUMBER);
            address = getArguments().getString(ADDRESS);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_navigation, container, false);
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
        StoreFragment storeFragment = StoreFragment.newInstance(email, userType);
        ShoppingCartFragment shoppingCartFragment = ShoppingCartFragment.newInstance(email, userType, name, mobile, address);
        UserOrderFragment userOrderFragment = UserOrderFragment.newInstance(email, userType);
        UserFragment userFragment = UserFragment.newInstance(email, userType);


        //Add all fragments to viewpager adapter
        viewPagerAdapter.addFragment(storeFragment, "Store");
        viewPagerAdapter.addFragment(shoppingCartFragment, "Timeline");
        viewPagerAdapter.addFragment(userOrderFragment, "Order");
        viewPagerAdapter.addFragment(userFragment, "Profile");


        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(viewPagerAdapter);

    }


}
