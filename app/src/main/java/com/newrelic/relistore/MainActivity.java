package com.newrelic.relistore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.newrelic.relistore.data.DataRepository;
import com.newrelic.relistore.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.NewRelic;
import java.util.List;

import com.newrelic.relistore.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    // private FloatingActionButton fabCart;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private String token = "REPLACE_WITH_YOUR_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets
                    .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize New Relic
        NewRelic.enableFeature(FeatureFlag.NativeReporting);
        NewRelic.enableFeature(FeatureFlag.OfflineStorage);
        NewRelic.withApplicationToken(token)
                .withLoggingEnabled(true)
                .start(this.getApplicationContext());

        // Generate and set Session ID
        String sessionId = java.util.UUID.randomUUID().toString();
        NewRelic.setAttribute("userSessionId", sessionId);
        Log.i(TAG, "Session ID set: " + sessionId);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        // fabCart removed
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setupToolbar();
        setupDrawer();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // fabCart listener removed

        loadProducts();
    }

    private void loadProducts() {
        new FetchProductsTask().execute();
    }

    private class FetchProductsTask extends AsyncTask<Void, Void, List<Product>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            try {
                return DataRepository.getInstance().getProducts();
            } catch (Exception e) {
                Log.e(TAG, "Error fetching products", e);
                NewRelic.recordHandledException(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Product> products) {
            progressBar.setVisibility(View.GONE);
            if (products != null) {
                ProductAdapter adapter = new ProductAdapter(products, product -> {
                    Log.i(TAG, "Product clicked: " + product.getName());
                    Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(MainActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Do nothing, already here
            } else if (id == R.id.nav_crash) {
                startActivity(new Intent(MainActivity.this, CrashActivity.class));
            } else if (id == R.id.nav_anr) {
                triggerANR();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void triggerANR() {
        Toast.makeText(this, "Triggering ANR (Bubble Sort)...", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Triggering ANR with Bubble Sort...");

        // Heavy computation on main thread
        int[] array = new int[500000];
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }

        // Bubble Sort
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }

        Log.i(TAG, "ANR Bubble Sort Complete (if you see this, increase array size)");
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}