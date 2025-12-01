package com.example.android_base_training;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.newrelic.agent.android.FeatureFlag;
import com.newrelic.agent.android.NewRelic;
import com.newrelic.agent.android.logging.LogLevel;
import com.newrelic.agent.android.util.NetworkFailure;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    private Button crashme, getHttp, customattr1, customattr2, event, bread, handled, anr, newpage;
    private TextView resultText;
    public String url = "http://3.230.230.121/mobile1/webrequest";

    public final Handler handler = new Handler();

    String token = "<Redacted>-NRMA";

    public class BubbleSortExample {
        public void bubbleSort(int[] arr) {
            int n = arr.length;
            int temp = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    if (arr[j - 1] > arr[j]) {
                        // swap elements
                        temp = arr[j - 1];
                        arr[j - 1] = arr[j];
                        arr[j] = temp;
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getHttp = (Button) findViewById(R.id.getHttp);
        crashme = findViewById(R.id.crashButton);
        customattr1 = findViewById(R.id.customAttribute1);
        customattr2 = findViewById(R.id.customAttribute2);
        event = findViewById(R.id.customEvent);
        bread = findViewById(R.id.breadCrumb);
        handled = findViewById(R.id.handledException);
        anr = findViewById(R.id.appNotResponding);
        newpage = findViewById(R.id.nextPage);
        resultText = findViewById(R.id.resultText);

        // NewRelic.setEventListener(this);
        NewRelic.enableFeature(FeatureFlag.NativeReporting);
        NewRelic.enableFeature(FeatureFlag.OfflineStorage);
        NewRelic.withApplicationToken(token).start(this.getApplicationContext());

        getHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("demo", "onClick: HTTP Request");
                NewRelic.log(LogLevel.INFO, "onClick: HTTP Request");
                // Show loading state
                resultText.setText("🔄 Sending HTTP request...");
                getHttp.setEnabled(false);

                // Start New Relic interaction
                NewRelic.startInteraction("HTTP Request Demo");

                new GetDataTask().execute();

                // Get network info safely
                String sessionId = NewRelic.currentSessionId();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (cm != null) {
                    NetworkInfo netInfo = cm.getActiveNetworkInfo();
                    if (netInfo != null && netInfo.isConnected()) {
                        NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                        if (nc != null) {
                            int downSpeed = nc.getLinkDownstreamBandwidthKbps() / 1000;
                            int upSpeed = nc.getLinkUpstreamBandwidthKbps() / 1000;

                            Log.i("NetworkInfo", "Download speed: " + downSpeed + " Mbps");
                            Log.i("NetworkInfo", "Upload speed: " + upSpeed + " Mbps");

                            // Record network capabilities as custom attributes
                            NewRelic.setAttribute("network_download_speed_mbps", downSpeed);
                            NewRelic.setAttribute("network_upload_speed_mbps", upSpeed);
                        }
                    } else {
                        Log.w("NetworkInfo", "No active network connection");
                        NewRelic.setAttribute("network_status", "disconnected");
                        resultText.setText("❌ No network connection available");
                        getHttp.setEnabled(true);
                        return;
                    }
                }

                // Record network failure demo (as per original code)
                NewRelic.noticeNetworkFailure(url, "GET", System.currentTimeMillis(),
                        System.currentTimeMillis() + 500, NetworkFailure.exceptionToNetworkFailure(new Exception()));
            }
        });

        crashme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resultText.setText("💥 " + getString(R.string.crash_msg) + "\n\n" +
                        "⚠️ This will force close the app\n" +
                        "📊 Crash will be recorded in New Relic");

                crashme.setEnabled(false);

                // Record crash event attributes
                NewRelic.setAttribute("crash_trigger", "user_initiated");
                NewRelic.setAttribute("crash_source", "demo_button");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("demo", "Initiating crash demo");
                        NewRelic.crashNow("This is a crash demo triggered by user");
                    }
                }, 5000);
            }
        });

        customattr1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NewRelic.setInteractionName("Display My storeID");
                NewRelic.startInteraction("Display My storeID as custom attribute 1");
                Log.i("intract", "START Sending My storeID as custom attribute 1 using startInteract");

                String storeId = "SampleStoreId000";
                NewRelic.setAttribute("storeId", storeId);
                NewRelic.logInfo("Test for sending Custom Attribute 1: " + "storeId");

                resultText.setText("✅ Custom Attribute Set!\n\n" +
                        "🏪 Store ID: " + storeId + "\n" +
                        "📊 Attribute recorded in New Relic");

                NewRelic.endInteraction("Display My storeID as custom attribute 1");
                Log.i("intract", "END Sending My Custom Attribute 1 using startInteract ");
            }
        });

        customattr2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("demo", "Sending Custom Attribute 2: ");

                double rate = 10000.99;
                NewRelic.setAttribute("rate", rate);

                resultText.setText("✅ Custom Attribute Set!\n\n" +
                        "💰 Rate: $" + String.format("%.2f", rate) + "\n" +
                        "📊 Attribute recorded in New Relic");
            }
        });

        event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NewRelic.startInteraction("Recording Custom Events");

                // Car event
                Map<String, Object> carAttributes = new HashMap<String, Object>();
                carAttributes.put("make", "Ford");
                carAttributes.put("model", "ModelT");
                carAttributes.put("color", "Black");
                carAttributes.put("VIN", "123XYZ");
                carAttributes.put("maxSpeed", 12);

                NewRelic.recordCustomEvent("Car", carAttributes);
                Log.i("demo", "Sending Event: Car");

                // User action event
                Map<String, Object> userActionAttributes = new HashMap<String, Object>();
                userActionAttributes.put("name", "Purchase");
                userActionAttributes.put("sku", "12345LPD");
                userActionAttributes.put("quantity", 1);
                userActionAttributes.put("unitPrice", 99.99);
                userActionAttributes.put("total", 99.99);

                NewRelic.recordCustomEvent("UserAction", userActionAttributes);
                Log.i("demo", "Sending Event: UserAction");

                resultText.setText("✅ Custom Events Recorded!\n\n" +
                        "🚗 Car Event: Ford ModelT\n" +
                        "🛒 User Action: Purchase ($99.99)\n" +
                        "📊 Events sent to New Relic");

                NewRelic.endInteraction("Recording Custom Events");
            }

        });

        bread.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Record breadcrumb for opening panel
                NewRelic.startInteraction("Opening Breadcrumb Panel");
                Log.i("intract", "START opening breadcrumb panel");

                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("button", "set breadcrumb");
                attributes.put("action", "open_panel");
                attributes.put("location", "MainActivity");

                NewRelic.recordBreadcrumb("Breadcrumb Panel Launch", attributes);
                NewRelic.logInfo("Opening breadcrumb panel");

                // Open the breadcrumb panel activity
                Intent intent = new Intent(MainActivity.this, BreadcrumbPanelActivity.class);
                startActivity(intent);

                Log.i("intract", "END opening breadcrumb panel");
                NewRelic.endInteraction("Opening Breadcrumb Panel");
            }
        });

        handled.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("demo", "Testing handled exception");

                try {
                    // Simulate an array out of bounds exception
                    int[] list = { 1, 2, 3, 4, 5, 6 };
                    int value = list[10]; // This will throw IndexOutOfBoundsException

                } catch (Exception e) {
                    Log.w("demo", "Caught exception: " + e.getMessage());

                    resultText.setText("✅ Exception Handled Successfully!\n\n" +
                            "⚠️ Exception Type: " + e.getClass().getSimpleName() + "\n" +
                            "📝 Message: " + e.getMessage() + "\n" +
                            "📊 Exception recorded in New Relic");

                    // Record the handled exception in New Relic
                    NewRelic.recordHandledException(e);
                }
            }
        });

        anr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resultText.setText("🐌 Starting ANR Simulation...\n\nThis will freeze the UI for a few seconds");
                anr.setEnabled(false);

                Log.i("demo", "Application Not Responding simulation started");

                // Use a handler to update UI before starting the blocking operation
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NewRelic.startInteraction("ANR Simulation");

                        Random random = new Random();
                        int arrayLength = 500000; // Reduced size for faster demo
                        int[] numbers = new int[arrayLength];

                        // Fill array with random numbers
                        for (int i = 0; i < arrayLength; i++) {
                            numbers[i] = random.nextInt(999999);
                        }

                        // Perform bubble sort (intentionally inefficient)
                        BubbleSortExample bubbleSort = new BubbleSortExample();
                        long startTime = System.currentTimeMillis();
                        bubbleSort.bubbleSort(numbers);
                        long duration = System.currentTimeMillis() - startTime;

                        Log.d("demo", "ANR simulation completed in " + duration + "ms");

                        resultText.setText("✅ ANR Simulation Complete!\n\n" +
                                "⏱️ Duration: " + duration + "ms\n" +
                                "📊 Sorted " + arrayLength + " numbers\n" +
                                "🎯 ANR event recorded in New Relic");

                        anr.setEnabled(true);
                        NewRelic.endInteraction("ANR Simulation");
                    }
                }, 100);
            }
        });

        newpage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("demo", "Navigating to next page");

                // Record navigation breadcrumb
                Map<String, Object> navAttributes = new HashMap<>();
                navAttributes.put("from_screen", "MainActivity");
                navAttributes.put("to_screen", "SecondInteraction");
                navAttributes.put("navigation_trigger", "user_button");

                NewRelic.recordBreadcrumb("Screen Navigation", navAttributes);
                resultText.setText("🧭 Navigating to next page...");

                Intent intent = new Intent(MainActivity.this, secondinteraction.class);
                startActivity(intent);
            }
        });

    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {
        private long startTime;
        private boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startTime = System.currentTimeMillis();
            Log.i("newrelic", "Starting HTTP request to: " + url);
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("newrelic", "doInBackground: Making HTTP request");
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("User-Agent", "NewRelic-Demo-App")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    isSuccess = true;
                    String responseBody = response.body().string();

                    // Record successful network request
                    NewRelic.setAttribute("http_response_code", response.code());
                    NewRelic.setAttribute("http_response_size", responseBody.length());

                    return responseBody;
                } else {
                    Log.w("newrelic", "HTTP request failed with code: " + response.code());
                    return "HTTP Error: " + response.code() + " " + response.message();
                }

            } catch (IOException e) {
                Log.e("newrelic", "Network error: " + e.getMessage());
                NewRelic.recordHandledException(e);
                e.printStackTrace();
                return "Network Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            long duration = System.currentTimeMillis() - startTime;
            Log.i("newrelic", "HTTP request completed in " + duration + "ms");

            // Re-enable the button
            getHttp.setEnabled(true);

            if (result != null) {
                if (isSuccess) {
                    resultText.setText("✅ HTTP Request Successful!\n\n" +
                            "Duration: " + duration + "ms\n" +
                            "Response: " + (result.length() > 100 ? result.substring(0, 100) + "..." : result));

                    // Record success metrics
                    NewRelic.setAttribute("http_request_duration_ms", duration);
                    NewRelic.setAttribute("http_request_status", "success");
                } else {
                    resultText.setText("❌ HTTP Request Failed!\n\n" + result);
                    NewRelic.setAttribute("http_request_status", "failed");
                }

                Log.i("newrelic", "Response: " + result);
            } else {
                resultText.setText("❌ No response received");
                NewRelic.setAttribute("http_request_status", "no_response");
            }

            // End New Relic interaction
            NewRelic.endInteraction("HTTP Request Demo");
        }
    };

}