package com.example.shopal.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.shopal.R;

import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.utils.AppExecutors;
import com.example.shopal.utils.FragmentUtils;



public class MainActivity extends AppCompatActivity {


    SharedPreferences sp;
    private CartDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Database instance
        mDb = CartDatabase.getInstance(this);

        sp = getSharedPreferences("login",MODE_PRIVATE);

        //If logged in , go to Navigation directly
        if(sp.getBoolean("logged",false)){

            String userType =  sp.getString("userType","");

            if(userType.equals("User")) {
                FragmentUtils.replaceFragment(this, UserNavigationFragment.newInstance(
                        sp.getString("email", ""),
                        userType,
                        sp.getString("name", ""),
                        sp.getString("mobile", ""),
                        sp.getString("address", "")
                        ), R.id.fragment_container, false);
                                   }else{

                FragmentUtils.replaceFragment(this, DriverNavigationFragment.newInstance(
                        sp.getString("email", ""),
                        userType), R.id.fragment_container, false);

            }
        }else
            FragmentUtils.replaceFragment(this, LoginFragment.newInstance("test", "test"), R.id.fragment_container, false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.login, menu);

        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {

            //Setting logged in flag to false
            sp = getSharedPreferences("login",MODE_PRIVATE);
            sp.edit().putBoolean("logged",false).apply();

            //Clear Cart from local db
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {

                    mDb.cartDao().deleteCart();
                }
            });


            Intent intent = getIntent();
            finish();
            startActivity(intent);

            return true;
        }

        if (id == R.id.about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }








}
