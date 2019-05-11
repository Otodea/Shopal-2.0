package com.example.shopal.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.local.database.CartDatabase;

import java.util.List;

public class CartViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = CartViewModel.class.getSimpleName();

    private LiveData<List<ShoppingItem>> shoppingItems;

    public CartViewModel(Application application) {
        super(application);
        CartDatabase database = CartDatabase.getInstance(this.getApplication());
        shoppingItems = database.cartDao().loadAllShoppingItems();
    }

    public LiveData<List<ShoppingItem>> getShoppingItems() {
        return shoppingItems;
    }
}