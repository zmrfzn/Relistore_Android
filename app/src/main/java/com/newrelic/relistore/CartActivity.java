package com.newrelic.relistore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import com.newrelic.relistore.model.Cart;
import com.newrelic.relistore.model.Product;
import java.util.ArrayList;
import java.util.List;

import com.newrelic.relistore.BaseActivity;

public class CartActivity extends BaseActivity {
    private static final String TAG = "CartActivity";

    private RecyclerView cartRecyclerView;
    private TextView cartTotal;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setupToolbar();

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets
                    .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cartTotal = findViewById(R.id.cartTotal);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateCartUI();

        btnCheckout.setOnClickListener(v -> {
            Log.i(TAG, "Starting checkout");
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
        });
    }

    private void updateCartUI() {
        List<Product> items = Cart.getInstance().getItems();
        if (adapter == null) {
            adapter = new CartAdapter(items, product -> {
                Cart.getInstance().remove(product);
                updateCartUI();
                invalidateOptionsMenu(); // Update badge
            });
            cartRecyclerView.setAdapter(adapter);
        } else {
            adapter.updateItems(items);
        }

        double total = Cart.getInstance().getTotal();
        cartTotal.setText(String.format("Total: $%.2f", total));

        Button btnCheckout = findViewById(R.id.btnCheckout);
        if (items.isEmpty()) {
            btnCheckout.setEnabled(false);
            btnCheckout.setAlpha(0.5f);
        } else {
            btnCheckout.setEnabled(true);
            btnCheckout.setAlpha(1.0f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartUI();
    }
}
