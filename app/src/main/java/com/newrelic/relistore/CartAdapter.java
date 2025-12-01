package com.newrelic.relistore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.newrelic.relistore.model.Product;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Product> items;
    private OnItemRemoveListener listener;

    public interface OnItemRemoveListener {
        void onItemRemove(Product product);
    }

    public CartAdapter(List<Product> items, OnItemRemoveListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = items.get(position);
        holder.name.setText(product.getName());
        holder.price.setText(String.format("$%.2f", product.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.image);

        holder.btnRemove.setOnClickListener(v -> listener.onItemRemove(product));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Product> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView price;
        public ImageView image;
        public ImageButton btnRemove;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.cartItemName);
            price = view.findViewById(R.id.cartItemPrice);
            image = view.findViewById(R.id.cartItemImage);
            btnRemove = view.findViewById(R.id.btnRemove);
        }
    }
}
