package com.example.shopal.model.local.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.shopal.model.local.entities.ShoppingItem;

import java.util.List;

@Dao
public interface CartDao {

    //Query to load thw whole cart
    @Query("SELECT * FROM cart")
    LiveData<List<ShoppingItem>> loadAllShoppingItems();

    //Query to insert shopping item to db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertShoppingItem(ShoppingItem shoppingItem);

    //Query to update shopping item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateUser(ShoppingItem shoppingItem);

    //Query to delete item
    @Delete
    void deleteShoppingItem(ShoppingItem shoppingItem);

    @Query("SELECT * FROM cart where id = :id")
    ShoppingItem loadShoppingItemById(String id);

    //Query to clear whole cart
    @Query("DELETE FROM cart")
    void deleteCart();

}