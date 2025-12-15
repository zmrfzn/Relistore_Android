package com.newrelic.relistore.data;

import android.util.Log;
import com.newrelic.relistore.model.Product;
import com.newrelic.relistore.model.Cart;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DataRepository {
    private static final String TAG = "DataRepository";
    private static final String BASE_URL = "http://10.0.2.2:3000"; // Host localhost from emulator
    private static DataRepository instance;
    private OkHttpClient client;

    private DataRepository() {
        client = new OkHttpClient();
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    public List<Product> getProducts() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/products")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            String jsonData = response.body().string();
            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<List<Product>>() {
            }.getType();
            return new com.google.gson.Gson().fromJson(jsonData, listType);
        }
    }

    public Product getProduct(String id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/products/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                Log.e(TAG, "Failed to fetch product " + id + ": " + response.code() + " - " + errorBody);
                throw new IOException("Server Error: " + response.code());
            }

            String jsonData = response.body().string();
            return new com.google.gson.Gson().fromJson(jsonData, Product.class);
        }
    }

    public boolean checkout(String paymentMethod) throws Exception {
        Log.i(TAG, "Initiating checkout with method: " + paymentMethod);

        // Simulate failure rates based on payment method
        double failureRate = 0.30; // 30% for Credit Card
        if ("PayPal".equalsIgnoreCase(paymentMethod) || "Apple Pay".equalsIgnoreCase(paymentMethod)) {
            failureRate = 0.80; // 80% for others
        }

        if (Math.random() < failureRate) {
            Log.e(TAG, "Checkout failed (simulated) for " + paymentMethod);
            throw new IOException("Payment Declined (" + paymentMethod + ")");
        }

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("total", Cart.getInstance().getTotal());

        // Simple cart representation for logging
        JSONArray cartItems = new JSONArray();
        for (Product p : Cart.getInstance().getItems()) {
            JSONObject item = new JSONObject();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("price", p.getPrice());
            cartItems.put(item);
        }
        jsonBody.put("cart", cartItems);

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/checkout")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.e(TAG, "Checkout failed: " + response.code());
                throw new IOException("Checkout failed: " + response.code());
            }
            Log.i(TAG, "Checkout successful");
            return true;
        }
    }
}
