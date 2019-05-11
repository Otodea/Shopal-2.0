package com.example.shopal.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * A utility class to handle fragment transactions.
 */
public class FragmentUtils {

    // Private constructor to hide the implicit one
    private FragmentUtils() {

    }

    /**
     * Method that replaces a fragment in a particluar activity
     */
    public static void replaceFragment(AppCompatActivity activity, Fragment fragment, int id, boolean addToBackStack) {

        if (activity == null)
            return;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getCanonicalName());

        transaction.replace(id, fragment, fragment.getClass().getCanonicalName());
        transaction.commit();
    }


    /**
     * Method that adds a fragment in a particluar activity
     */
    public static void addFragment(AppCompatActivity activity, Fragment fragment, int id, boolean addToBackStack) {

        if (activity == null)
            return;

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();


        if (addToBackStack)
            transaction.addToBackStack(fragment.getClass().getCanonicalName());

        transaction.add(id, fragment, fragment.getClass().getCanonicalName());
        transaction.commit();
    }
}
