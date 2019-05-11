package com.example.shopal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.shopal.R;
import com.example.shopal.model.local.database.CartDatabase;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreAdapterViewHolder> implements Filterable {

    List<ShoppingItem> shoppingItemList;
    List<ShoppingItem> shoppingItemListFiltered;
    private CartDatabase mDb;

    Context context;

    public interface GridItemClickListener {
        void onListItemClick(ShoppingItem shoppingItem, View v);
    }

    private GridItemClickListener mOnclickListener;

    public StoreAdapter(GridItemClickListener listener, Context context) {

        mOnclickListener = listener;
        this.context = context;
    }

    //ViewHolder Class
    public class StoreAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Views
        public @BindView(R.id.grid_name)
        TextView nameTextView;
        public @BindView(R.id.grid_image)
        ImageView itemImageView;
        public @BindView(R.id.price)
        TextView priceTextView;
        public @BindView(R.id.itemSelected)
        CheckedTextView checkmarkView;


        public ShoppingItem shoppingItem;

        public StoreAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            mOnclickListener.onListItemClick(shoppingItemListFiltered.get(position), v);
        }

    }



    @NonNull
    @Override
    public StoreAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.store_recylcerview_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new StoreAdapterViewHolder(view);

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                List<ShoppingItem> filtered = new ArrayList<ShoppingItem>();

                if (query.isEmpty()) {
                    filtered = shoppingItemList;
                } else {
                    for (ShoppingItem item : shoppingItemList) {
                        if (item.getItemName().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                shoppingItemListFiltered = (ArrayList<ShoppingItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public void onBindViewHolder(@NonNull final StoreAdapterViewHolder holder, int position) {

        holder.shoppingItem = shoppingItemListFiltered.get(position);
        holder.nameTextView.setText(shoppingItemListFiltered.get(position).getItemName());
        holder.priceTextView.setText(Double.toString(shoppingItemListFiltered.get(position).getPrice()));
        mDb = CartDatabase.getInstance(context);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(mDb.cartDao().loadShoppingItemById(holder.shoppingItem.getId()) != null) {
                    holder.checkmarkView.setChecked(true);
                    holder.checkmarkView.setVisibility(View.VISIBLE);
                    holder.checkmarkView.setCheckMarkDrawable(R.drawable.ic_check_primary_24dp);
                } else {
                    holder.checkmarkView.setChecked(false);
                    holder.checkmarkView.setVisibility(View.INVISIBLE);
                }
            }
        });


        //Image View Logic
        if (!TextUtils.isEmpty(shoppingItemListFiltered.get(position).getImageUrl())) {

            Picasso picasso = new Picasso.Builder(context).
                    downloader(new OkHttp3Downloader(context))
                    .build();
            picasso.load(shoppingItemListFiltered.get(position).getImageUrl())
                    .placeholder(R.drawable.ic_shopping_cart_black_24dp)
                    .fit()
                    .error(R.drawable.ic_shopping_cart_black_24dp)
                    .into(holder.itemImageView);
        }

    }

    @Override
    public int getItemCount() {

        if (shoppingItemListFiltered == null)
            return 0;
        else
            return shoppingItemListFiltered.size();
    }

    public void setStoreData(List<ShoppingItem> shoppingItemList) {
        this.shoppingItemList = shoppingItemList;
        this.shoppingItemListFiltered =shoppingItemList;
        notifyDataSetChanged();
    }

}
