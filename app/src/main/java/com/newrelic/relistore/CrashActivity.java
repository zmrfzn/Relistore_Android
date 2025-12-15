package com.newrelic.relistore;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.newrelic.agent.android.NewRelic;
import android.util.Log;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets
                    .getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView timerText = findViewById(R.id.crashTimer);

        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Crashing in " + (millisUntilFinished / 1000 + 1) + "...");
            }

            public void onFinish() {
                timerText.setText("Crashing NOW!");
                Log.e("CrashActivity", "Executing crashNow()");
                // Challenge 5.1: Trigger Crash
                NewRelic.crashNow();
            }
        }.start();
        Log.i("CrashActivity", "Crash timer started...");
    }
}
