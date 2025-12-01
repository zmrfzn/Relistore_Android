package com.newrelic.relistore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.newrelic.relistore.model.Cart;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                // Enable Up button if not MainActivity
                if (!(this instanceof MainActivity)) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();

        if (actionView != null) {
            actionView.setOnClickListener(v -> {
                Log.i(TAG, "Navigating to Cart from Toolbar");
                startActivity(new Intent(this, CartActivity.class));
            });

            updateCartBadge(actionView);
        }
        return true;
    }

    protected void updateCartBadge(View actionView) {
        if (actionView == null)
            return;

        TextView badge = actionView.findViewById(R.id.cart_badge);
        if (badge != null) {
            int count = Cart.getInstance().getItems().size();
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.action_cart) {
            Log.i(TAG, "Navigating to Cart from Toolbar");
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu(); // Refresh badge
    }
}
