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
import com.newrelic.agent.android.util.NetworkFailure;


public class MainActivity extends AppCompatActivity {
 private Button crashme, getHttp, customattr1, customattr2, event, bread, handled, anr, newpage;
 private TextView resultText;
 public String url = "http://192.168.1.2:3001/webrequest";

   String token = "<YOUR TOKEN>";

    public class BubbleSortExample {
        public void bubbleSort(int[] arr) {
            int n = arr.length;
            int temp = 0;
            for (int i = 0; i < n; i++) {
                for (int j = 1; j < (n - i); j++) {
                    if (arr[j - 1] > arr[j]) {
                        //swap elements
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

        //NewRelic.setEventListener(this);
        NewRelic.enableFeature(FeatureFlag.NativeReporting);
        NewRelic.enableFeature(FeatureFlag.OfflineStorage);
        NewRelic.withApplicationToken(token).start(this.getApplicationContext());


        getHttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("demo", "onClick: ");
                new GetDataTask().execute();
                String sessionId = NewRelic.currentSessionId();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                int downSpeed = nc.getLinkDownstreamBandwidthKbps()/1000;
                int upSpeed = nc.getLinkUpstreamBandwidthKbps()/1000;
                System.out.println("downspeed:" + downSpeed);
                System.out.println("upspeed:" + upSpeed);
                NewRelic.noticeNetworkFailure(url, "GET", System.currentTimeMillis(), System.currentTimeMillis()+500, NetworkFailure.exceptionToNetworkFailure(new Exception()));


            }
        });

        crashme.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NewRelic.crashNow("This is crash demo");
                Log.i("demo", "I crashed it: ");
            }
        });

        customattr1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NewRelic.setInteractionName("Display My storeID");
                NewRelic.startInteraction("Display My storeID as custom attribute 1");
                Log.i("intract", "START Sending My storeID as custom attribute 1 using startInteract");
                NewRelic.setAttribute("storeId", "SampleStoreId000");
                customattr1.setOnClickListener(this::onClick);
                NewRelic.logInfo("Test for sending Custom Attribute 1: " + "storeId");
                NewRelic.endInteraction("Display My storeID as custom attribute 1");
                Log.i("intract", "END Sending My Custom Attribute 1 using startInteract ");
            }
        });

        customattr2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("demo", "Sending Custom Attribute 2: ");
                NewRelic.setAttribute("rate", 10000.99);
            }
        });

        event.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("make", "Ford");
                attributes.put("model", "ModelT");
                attributes.put("color", "Black");
                attributes.put("VIN", "123XYZ");
                attributes.put("maxSpeed", 12);

                NewRelic.recordCustomEvent("Car", attributes);

                Log.i("demo", "Sending Event: Car");

                Map<String, Object> userActionAttributes = new HashMap<String, Object>();
                userActionAttributes.put("name", "Purchase");
                userActionAttributes.put("sku", "12345LPD");
                userActionAttributes.put("quantity", 1);
                userActionAttributes.put("unitPrice", 99.99);
                userActionAttributes.put("total", 99.99);

                NewRelic.recordCustomEvent("UserAction", userActionAttributes);
                Log.i("demo", "Sending Event: UserAction");
            }

        });

        bread.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NewRelic.startInteraction("Sending Breadcrumb");
                Log.i("intract", "START sending breadcrumb");
                String sessionId = NewRelic.currentSessionId();
                Map<String, Object> attributes = new HashMap<String, Object>();
                attributes.put("button", "set breadcrumb");
                attributes.put("location", "Clicking button");
                NewRelic.logInfo("User clicked on set breadcrumb");
                NewRelic.recordBreadcrumb("Launch Start", attributes);
                NewRelic.logInfo("Breadcrumb sent successfully");
                Log.i("intract", "END sending Breadcrumb");
                NewRelic.endInteraction("Sending Breadcrumb");
                NewRelic.recordBreadcrumb("Launch Complete", attributes);

            }
        });

        handled.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Log.i("demo", "I caused handled exception: ");
                try {
                    int[] list = {1, 2, 3, 4, 5, 6};
                    System.out.println(list[10]);
                } catch (Exception e) {
                    System.out.println("Oops!");
                    NewRelic.recordHandledException(e);
                }
            }
        });

        anr.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Log.i("demo", "Application Not Responding: ");
                Random random = new Random();
                int ranarraylength = 1000000;
                int[] num = new int[ranarraylength];
                for(int x=0; x < ranarraylength; x++) {
                    int randomNumber = random.nextInt(999999999 - 1) + 1;
                    num[x] = randomNumber;
                }
                BubbleSortExample bubbleSort = new BubbleSortExample();
                bubbleSort.bubbleSort(num);
                Log.d("demo", "CauseANR finished");
            }
        });

        newpage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i("demo", "Go to next page: ");
                Intent intent = new Intent(MainActivity.this, secondinteraction.class);
                startActivity(intent);
            }
        });

    }
    private class GetDataTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            Log.i("newrelic", "doInBackground: ");
            OkHttpClient client = new OkHttpClient();
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return "failed";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.i("newrelic", s);
                resultText.setText(s);
            }
        }
    };

}