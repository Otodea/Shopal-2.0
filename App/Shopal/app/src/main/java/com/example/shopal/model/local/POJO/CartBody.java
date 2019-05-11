package com.example.shopal.model.local.POJO;

import com.example.shopal.model.local.entities.ShoppingItem;
import com.example.shopal.view.ShoppingCartFragment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CartBody {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("items")
    @Expose
    private List<ShoppingItem> shoppingItemList;
    @SerializedName("status")
    @Expose
    private String status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<ShoppingItem> getShoppingItemList() {
        return shoppingItemList;
    }

    public void setShoppingItemList(List<ShoppingItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
