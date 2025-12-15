package com.newrelic.relistore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.newrelic.relistore.model.Cart;
import com.newrelic.relistore.model.Product;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.relistore.data.DataRepository;
import android.os.AsyncTask;
import java.util.HashMap;
import java.util.Map;

import com.newrelic.relistore.BaseActivity;

public class ProductDetailActivity extends BaseActivity {
    private static final String TAG = "ProductDetailActivity";
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setupToolbar();

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detailImage), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets
                    .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        product = (Product) getIntent().getSerializableExtra("product");
        if (product == null) {
            finish();
            return;
        }

        TextView name = findViewById(R.id.detailName);
        TextView price = findViewById(R.id.detailPrice);
        TextView category = findViewById(R.id.detailCategory);
        TextView description = findViewById(R.id.detailDescription);
        ImageView image = findViewById(R.id.detailImage);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        name.setText(product.getName());
        price.setText(String.format("$%.2f", product.getPrice()));
        category.setText(product.getCategory());
        description.setText(product.getDescription());

        Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(image);

        // Challenge 2.1: Record Breadcrumb
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("product_id", product.getId());
        attributes.put("product_name", product.getName());
        NewRelic.recordBreadcrumb("ViewProduct", attributes);

        // Fetch full details from server (to demonstrate error cascading)
        new FetchProductDetailsTask().execute(product.getId());

        btnAddToCart.setOnClickListener(v -> {
            Log.i(TAG, "Adding to cart: " + product.getName());
            Cart.getInstance().add(product);

            // Challenge 4.1: Record Custom Event (AddToCart)
            Map<String, Object> eventAttributes = new HashMap<>();
            eventAttributes.put("product_id", product.getId());
            eventAttributes.put("product_name", product.getName());
            eventAttributes.put("price", product.getPrice());
            eventAttributes.put("category", product.getCategory());
            NewRelic.recordCustomEvent("AddToCart", eventAttributes);

            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        });
    }

    private class FetchProductDetailsTask extends AsyncTask<String, Void, Product> {
        private Exception exception;

        @Override
        protected Product doInBackground(String... ids) {
            try {
                return DataRepository.getInstance().getProduct(ids[0]);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product result) {
            if (result != null) {
                // Update UI with fresh data if needed
                Log.i(TAG, "Product details fetched successfully");
            } else {
                Log.e(TAG, "Error fetching product details", exception);
                // Challenge 3.1: Record Handled Exception
                NewRelic.recordHandledException(exception);
                Toast.makeText(ProductDetailActivity.this, "Error loading details: " + exception.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
