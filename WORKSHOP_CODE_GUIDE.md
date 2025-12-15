# Workshop Code Guide: "Start State"

This guide lists the code that should be **commented out** or **removed** to create the starting state for the workshop. Participants will uncomment/add this code during the labs.

## 1. `app/build.gradle`
**Action**: Comment out the plugin and dependency.
```gradle
// apply plugin: 'newrelic'
// implementation("com.newrelic.agent.android:android-agent:7.6.13")
```

## 2. `app/src/main/java/com/newrelic/relistore/MainActivity.java`
**Action**: Comment out initialization and imports.
```java
// import com.newrelic.agent.android.NewRelic;
// import com.newrelic.agent.android.FeatureFlag;

// NewRelic.enableFeature(FeatureFlag.NativeReporting);
// NewRelic.enableFeature(FeatureFlag.OfflineStorage);
// NewRelic.withApplicationToken("YOUR_TOKEN")
//         .withLoggingEnabled(true)
//         .start(this.getApplicationContext());

// NewRelic.setAttribute("userSessionId", sessionId);
```

## 3. `app/src/main/java/com/newrelic/relistore/ProductDetailActivity.java`
**Action**: Comment out Breadcrumbs, Custom Events, and Handled Exceptions.
```java
// NewRelic.recordBreadcrumb("ViewProduct", attributes);

// NewRelic.recordCustomEvent("AddToCart", eventAttributes);

// NewRelic.recordHandledException(exception);
```

## 4. `app/src/main/java/com/newrelic/relistore/CrashActivity.java`
**Action**: Comment out the crash command.
```java
// NewRelic.crashNow();
```

---
**Note**: The imports for `com.newrelic.agent.android.*` will need to be commented out in all files if the dependency is removed from `build.gradle`, otherwise the project won't compile.
**Alternative**: Keep the dependency in `build.gradle` but comment out the `apply plugin` and the actual API calls. This saves time on Gradle sync/downloading.
