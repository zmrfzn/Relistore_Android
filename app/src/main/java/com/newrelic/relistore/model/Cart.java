package com.newrelic.relistore.model;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static final String TAG = "Cart";
    private static Cart instance;
    private List<Product> items;

    private Cart() {
        items = new ArrayList<>();
    }

    public static synchronized Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public void add(Product product) {
        Log.i(TAG, "Adding product to cart: " + product.getName());
        items.add(product);
    }

    public void remove(Product product) {
        Log.i(TAG, "Removing product from cart: " + product.getName());
        items.remove(product);
    }

    public void clear() {
        Log.i(TAG, "Clearing cart");
        items.clear();
    }

    public List<Product> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotal() {
        double total = 0;
        for (Product p : items) {
            total += p.getPrice();
        }
        return total;
    }
}
