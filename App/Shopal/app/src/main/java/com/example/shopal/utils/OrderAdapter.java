package com.example.shopal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.shopal.R;
import com.example.shopal.model.local.POJO.Order;
import com.example.shopal.model.local.entities.ShoppingItem;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderAdapterViewHolder> {


    //Data store
    List<Order> orderList;

    Context context;



    //Handling Clicks
    public interface ListItemClickListener {
        void onListItemClick(Order order);
    }

    private ListItemClickListener mOnclickListener;

    public OrderAdapter(ListItemClickListener listener, Context context) {
        mOnclickListener = listener;
        this.context = context;
    }

    //ViewHolder Class
    public class OrderAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Views
        public @BindView(R.id.destination_address)
        TextView destinationAddressTextView;
        public @BindView(R.id.name)
        TextView nameTextView;
        public @BindView(R.id.mobile)
        TextView mobileTextView;
        public @BindView(R.id.name_image_view)
        ImageView nameImageView;
        public Order order;

        public OrderAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            mOnclickListener.onListItemClick(orderList.get(position));
        }
    }


    @NonNull
    @Override
    public OrderAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.order_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new OrderAdapterViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull OrderAdapterViewHolder holder, int position) {

        holder.order = orderList.get(position);
        holder.destinationAddressTextView.setText(orderList.get(position).getDestinationAddress());
        holder.nameTextView.setText(orderList.get(position).getName());
        holder.mobileTextView.setText(orderList.get(position).getMobile());


        if(holder.order.getName() != null) {
            //Circular Icon
            TextDrawable drawable = TextDrawable.builder()
                    .buildRoundRect(holder.order.getName().substring(0, 1),
                            ContextCompat.getColor(context, R.color.colorPrimary), 100);
            holder.nameImageView.setImageDrawable(drawable);

        }

    }
    @Override
    public int getItemCount() {

        if (orderList == null)
            return 0;
        else
            return orderList.size();
    }


    public List<Order> getOrderList () {
        return orderList;

    }

    public void setOrdersData(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }


}
