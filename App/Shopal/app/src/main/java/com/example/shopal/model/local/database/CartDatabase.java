package com.example.shopal.model.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.model.local.dao.CartDao;

@Database(entities = {ShoppingItem.class}, version = 2, exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "cart";
    private static CartDatabase sInstance;

    public static CartDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        CartDatabase.class, CartDatabase.DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract CartDao cartDao();

}