package com.example.shopal.model.remote;


import android.util.Log;

import com.example.shopal.model.local.POJO.CartBody;
import com.example.shopal.model.local.POJO.CheckoutResponse;
import com.example.shopal.model.local.POJO.LoginBody;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.POJO.OrderBody;
import com.example.shopal.model.local.POJO.User;
import com.example.shopal.model.local.entities.ShoppingItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/*
 Define all API Calls
*/
public interface ApiService {


    //Get all items from stores
    @GET("/item/get-items")
    Call<List<ShoppingItem>> getAllShoppingItems();

    //Post cart
    @POST("/list/add-list")
    Call<Order> checkoutCart(@Body Order order);

    //Get Recommendations
    @POST("/list/recommendations")
    Call<Order> recommendItems(@Body Order order);

    //Get current Order
    @GET("/list/get-list")
    Call<Order> getCurrentOrder(@Query("_id") String user_id);

    //Get current Order
    @GET("/list/get-listofdriver")
    Call<Order> getDriverOrder(@Query("driver_id") String driverId);

    //Login
    @POST("/user/login")
    Call<User> login(@Body LoginBody loginBody);

    //Login
    @POST("/user/add-user")
    Call<User> signUp(@Body User user);

    //Get pending orders
    @GET("/list/no-drivers")
    Call<List<Order>> getPendingOrders();

    //Accept order
    @POST("list/update-driver")
    Call<Order> acceptOrder(@Body OrderBody orderBody);

    //Delete order
    @POST("list/delete-order")
    Call<Order> deleteOrder(@Body OrderBody orderBody);

    //Update order status
    @POST("list/update-order-status")
    Call<Order> updateOrderStatus(@Body OrderBody orderBody);

    //Login
    @GET("/user/get-user")
    Call<User> getUser(@Query("_id") String email);
}




