package com.newrelic.relistore;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.newrelic.relistore.data.DataRepository;
import com.newrelic.relistore.model.Cart;
import com.newrelic.agent.android.NewRelic;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.newrelic.relistore.BaseActivity;

public class CheckoutActivity extends BaseActivity {
    private static final String TAG = "CheckoutActivity";
    private ProgressBar checkoutProgress;
    private TextView checkoutStatus;
    private Button btnHome;
    private RadioGroup paymentMethodGroup;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        setupToolbar();

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.checkoutProgress),
                (v, insets) -> {
                    androidx.core.graphics.Insets systemBars = insets
                            .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        checkoutProgress = findViewById(R.id.checkoutProgress);
        checkoutStatus = findViewById(R.id.checkoutStatus);
        btnHome = findViewById(R.id.btnHome);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        btnPay = findViewById(R.id.btnPay);

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
            finish();
        });

        btnPay.setOnClickListener(v -> processCheckout());
    }

    private void processCheckout() {
        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedId);
        String paymentMethod = selectedRadioButton.getText().toString();

        // Set random user attributes
        String[] tiers = { "Gold", "Silver", "Bronze" };
        Random random = new Random();
        String userTier = tiers[random.nextInt(tiers.length)];
        boolean loggedIn = random.nextBoolean();

        NewRelic.setAttribute("userTier", userTier);
        NewRelic.setAttribute("paymentMethod", paymentMethod); // Capture selected method
        NewRelic.setAttribute("loggedIn", String.valueOf(loggedIn));

        Log.i(TAG, "Attributes set: Tier=" + userTier + ", Method=" + paymentMethod + ", LoggedIn=" + loggedIn);

        // Disable inputs
        btnPay.setEnabled(false);
        for (int i = 0; i < paymentMethodGroup.getChildCount(); i++) {
            paymentMethodGroup.getChildAt(i).setEnabled(false);
        }

        checkoutProgress.setVisibility(View.VISIBLE);
        checkoutStatus.setText("Processing Payment...");
        checkoutStatus.setVisibility(View.VISIBLE);

        new CheckoutTask(paymentMethod).execute();
    }

    private class CheckoutTask extends AsyncTask<Void, Void, Boolean> {
        private Exception exception;
        private String paymentMethod;

        public CheckoutTask(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "Starting checkout interaction");
            NewRelic.startInteraction("Checkout Process");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                return DataRepository.getInstance().checkout(paymentMethod);
            } catch (Exception e) {
                Log.e(TAG, "Checkout failed", e);
                this.exception = e;
                NewRelic.recordHandledException(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            checkoutProgress.setVisibility(View.GONE);

            if (success) {
                Log.i(TAG, "Checkout successful");
                checkoutStatus.setText("✅ Payment Successful!\n\nThank you for your purchase.");

                // Record PaymentSuccess event
                Map<String, Object> eventAttributes = new HashMap<>();
                eventAttributes.put("amount", Cart.getInstance().getTotal());
                eventAttributes.put("itemCount", Cart.getInstance().getItems().size());
                eventAttributes.put("transactionId", "TXN-" + System.currentTimeMillis());
                NewRelic.recordCustomEvent("PaymentSuccess", eventAttributes);

                Cart.getInstance().clear();
            } else {
                Log.e(TAG, "Checkout failed: " + (exception != null ? exception.getMessage() : "Unknown error"));
                checkoutStatus.setText("❌ Payment Failed\n\n" +
                        (exception != null ? exception.getMessage() : "Please try again."));

                // Re-enable inputs for retry
                btnPay.setEnabled(true);
                for (int i = 0; i < paymentMethodGroup.getChildCount(); i++) {
                    paymentMethodGroup.getChildAt(i).setEnabled(true);
                }

                // Record PaymentFailure event
                Map<String, Object> eventAttributes = new HashMap<>();
                eventAttributes.put("amount", Cart.getInstance().getTotal());
                eventAttributes.put("reason", exception != null ? exception.getMessage() : "Unknown");
                NewRelic.recordCustomEvent("PaymentFailure", eventAttributes);
            }

            btnHome.setVisibility(View.VISIBLE);
            NewRelic.endInteraction("Checkout Process");
        }
    }
}
