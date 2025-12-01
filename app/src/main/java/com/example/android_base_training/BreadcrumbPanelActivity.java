package com.example.android_base_training;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.agent.android.NewRelic;

public class BreadcrumbPanelActivity extends AppCompatActivity {
    
    private Button closePanelButton;
    private Button addBreadcrumbButton;
    private TextView breadcrumbInfoText;
    private TextView breadcrumbStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breadcrumb_panel);

        // Initialize views
        closePanelButton = findViewById(R.id.closePanelButton);
        addBreadcrumbButton = findViewById(R.id.addBreadcrumbButton);
        breadcrumbInfoText = findViewById(R.id.breadcrumbInfoText);
        breadcrumbStatusText = findViewById(R.id.breadcrumbStatusText);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Breadcrumb Panel");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize breadcrumb info
        breadcrumbInfoText.setText("This panel demonstrates New Relic breadcrumb functionality.\n\n" +
                "Breadcrumbs help track user interactions and application events for debugging and analytics.");

        // Set up close panel button
        closePanelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Record breadcrumb for panel close action
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("action", "close_panel");
                attributes.put("panel_type", "breadcrumb_panel");
                attributes.put("session_id", NewRelic.currentSessionId());
                
                NewRelic.recordBreadcrumb("Panel Closed", attributes);
                Log.i("BreadcrumbPanel", "Panel closed by user");
                
                // Close the activity
                finish();
            }
        });

        // Set up add breadcrumb button
        addBreadcrumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCustomBreadcrumb();
            }
        });

        // Record breadcrumb for panel opening
        Map<String, Object> openAttributes = new HashMap<>();
        openAttributes.put("action", "open_panel");
        openAttributes.put("panel_type", "breadcrumb_panel");
        openAttributes.put("timestamp", System.currentTimeMillis());
        
        NewRelic.recordBreadcrumb("Breadcrumb Panel Opened", openAttributes);
        Log.i("BreadcrumbPanel", "Breadcrumb panel opened");
    }

    private void addCustomBreadcrumb() {
        NewRelic.startInteraction("Adding Custom Breadcrumb");
        
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("user_action", "manual_breadcrumb");
        attributes.put("location", "breadcrumb_panel");
        attributes.put("timestamp", System.currentTimeMillis());
        attributes.put("session_id", NewRelic.currentSessionId());
        
        NewRelic.recordBreadcrumb("Custom Breadcrumb Added", attributes);
        
        // Update status text
        breadcrumbStatusText.setText("✓ Custom breadcrumb added successfully!\nTimestamp: " + 
                System.currentTimeMillis());
        
        Log.i("BreadcrumbPanel", "Custom breadcrumb added by user");
        NewRelic.endInteraction("Adding Custom Breadcrumb");
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle back button in action bar
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("action", "navigate_back");
        attributes.put("panel_type", "breadcrumb_panel");
        
        NewRelic.recordBreadcrumb("Panel Navigation Back", attributes);
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Record breadcrumb for panel destruction
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("action", "panel_destroyed");
        attributes.put("panel_type", "breadcrumb_panel");
        
        NewRelic.recordBreadcrumb("Breadcrumb Panel Destroyed", attributes);
        Log.i("BreadcrumbPanel", "Breadcrumb panel destroyed");
    }
}
