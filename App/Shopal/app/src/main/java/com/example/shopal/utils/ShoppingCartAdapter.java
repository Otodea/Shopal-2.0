package com.example.shopal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shopal.R;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ShoppingCartAdapterViewHolder> {


    //Data store
    List<ShoppingItem> shoppingItemList;

    Context context;

    //Handling Clicks
    public interface ListItemClickListener {

        void onListItemClick(ShoppingItem shoppingItem);

    }

    private ListItemClickListener mOnclickListener;

    public ShoppingCartAdapter(ListItemClickListener listener, Context context) {

        mOnclickListener = listener;
        this.context = context;


    }

    //ViewHolder Class
    public class ShoppingCartAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Views
        public @BindView(R.id.item_name)
        TextView itemNameTextView;
        public @BindView(R.id.price)
        TextView priceTextView;
        public @BindView(R.id.quantity)
        TextView quantityTextView;
        public @BindView(R.id.coverImage)
        ImageView coverImage;


        public ShoppingItem shoppingItem;

        public ShoppingCartAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            mOnclickListener.onListItemClick(shoppingItemList.get(position));

        }


    }


    @NonNull
    @Override
    public ShoppingCartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.shopping_cart_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ShoppingCartAdapterViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ShoppingCartAdapterViewHolder holder, int position) {

        holder.shoppingItem = shoppingItemList.get(position);
        holder.itemNameTextView.setText(shoppingItemList.get(position).getItemName());
        holder.priceTextView.setText(Double.toString(shoppingItemList.get(position).getPrice()));

        if(shoppingItemList.get(position).getQuantity()!= null)
         holder.quantityTextView.setText(Integer.toString(shoppingItemList.get(position).getQuantity()));

        //Image View Logic
        if (!TextUtils.isEmpty(shoppingItemList.get(position).getImageUrl())) {

            Picasso picasso = new Picasso.Builder(context).
                    downloader(new OkHttp3Downloader(context))
                    .build();
            picasso.load(shoppingItemList.get(position).getImageUrl())
                    .placeholder((R.drawable.ic_shopping_cart_black_24dp))
                    .fit()
                    .error(R.drawable.ic_shopping_cart_black_24dp)
                    .into(holder.coverImage);
        }
    }
    @Override
    public int getItemCount() {

        if (shoppingItemList == null)
            return 0;
        else
            return shoppingItemList.size();
    }


    public List<ShoppingItem> getShoppingItemList () {
        return shoppingItemList;

    }

    public void setShoppingItemsData(List<ShoppingItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
        notifyDataSetChanged();
    }


    public double getTotalCheckoutPrice(){

        double price = 0.0;

        for(ShoppingItem shoppingItem : shoppingItemList ){

            price += shoppingItem.getPrice() * shoppingItem.getQuantity();
        }

        return price;

    }
}