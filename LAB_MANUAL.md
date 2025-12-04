# New Relic Mobile Agent Workshop - Lab Manual

**Goal**: Learn how to instrument an Android application with New Relic to gain visibility into performance, crashes, and user behavior.
**Duration**: 90-120 Minutes

## Prerequisites
Before starting the labs, ensure you have the following:
1.  **Android Studio** installed and configured.
2.  **New Relic Account** (Sign up at [newrelic.com](https://newrelic.com)).
3.  **Backend Server Running**:
    *   Open a terminal in the `server/` directory.
    *   Run `npm install` (if not already done).
    *   Run `npm start` to start the local server on port 3000.

---

## Lab 1: Agent Installation & Setup (15 Minutes)
**Scenario**: You have just joined the "Reli-Store" mobile team. The Product Manager complains that the app feels "slow" and users are reporting random issues, but there is no data to back this up. Your first task is to turn on the lights.

**Objective**: Connect the Reli-Store app to New Relic to establish a baseline of performance and stability.

### Challenge 1.0: The Guided Install
Before we touch the code, we need to generate the installation instructions and keys.
*   **Task**: Log in to New Relic and navigate to **Add Data** > **Mobile** > **Android**.
*   **Action**: Follow the "Guided Install" steps to generate your unique **Application Token**.
*   **Note**: Keep this token handy! You will need it for the next steps.
*   *Reference*: [New Relic Android Docs](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/get-started/introduction-new-relic-mobile-android/)

### Challenge 1.1: The Missing Plugin
The project is missing the necessary Gradle configuration to talk to New Relic.
*   **Task**: Modify `app/build.gradle` to apply the `newrelic` plugin and add the `android-agent` dependency.
*   *Hint*: Look for the `plugins` and `dependencies` blocks.

### Challenge 1.2: The Silent Start
The agent is installed but not running.
*   **Task**: Initialize the New Relic agent in the main entry point of the application (`MainActivity.java`).
*   **Requirement**: Enable logging so we can debug the agent itself.
*   *Hint*: `NewRelic.withApplicationToken(...).start(...)`

### Verification & Discovery
1.  **Run the App**: Launch the app on an emulator. Check the Logcat for "New Relic" to confirm it started.
2.  **New Relic Platform**:
    *   Go to **Mobile** > **(Your App Name)**.
    *   **Summary Page**: Do you see the "App Launches" count increment?
    *   **Service Maps**: Is the app talking to any external services?

---

## Lab 2: Breadcrumbs & Interactions (15 Minutes)
**Scenario**: Support tickets are coming in saying "I can't see the product details". Engineering can't reproduce it. You need to know *what* the user did right before the issue occurred.

**Objective**: Track the user's journey through the app using Breadcrumbs.

### Challenge 2.1: Leaving a Trail
We need to know when a user views a specific product.
*   **Task**: In `ProductDetailActivity.java`, record a breadcrumb whenever the page loads.
*   **Requirement**: Include the `product_name` and `product_id` as attributes so we know *which* product they viewed.
*   *Hint*: `NewRelic.recordBreadcrumb(...)`

### Verification & Discovery
1.  **Run the App**: Open a few different products.
2.  **New Relic Platform**:
    *   Go to **Data Explorer**.
    *   **Query**: `SELECT * FROM MobileBreadcrumb SINCE 30 minutes ago`
    *   **Analysis**: Can you reconstruct the user's path? (e.g., Home -> Product A -> Home -> Product B).

---

## Lab 3: Handled Exceptions & HTTP Errors (20 Minutes)
**Scenario**: Users are reporting that sometimes the product details don't load, but the app doesn't crash. It just shows a blank screen or an error message. These "silent failures" are hurting conversion.

**Objective**: Track non-fatal errors and network failures.

### Challenge 3.1: The Catch
The `FetchProductDetailsTask` in `ProductDetailActivity.java` has a `try-catch` block that swallows errors.
*   **Task**: Modify the `catch` block to report the exception to New Relic.
*   *Hint*: `NewRelic.recordHandledException(...)`

### Challenge 3.2: The Network Glitch (Simulation)
*   **Task**: Put your emulator in "Airplane Mode" or disconnect WiFi.
*   **Action**: Try to open a product detail page.
*   **Observation**: The app should show a Toast message, but now New Relic should also know about it.

### Verification & Discovery
1.  **New Relic Platform**:
    *   Go to **Handled Exceptions**.
    *   **Analysis**: Find the exception you just triggered. Look at the stack trace.
    *   Go to **HTTP Errors**: Did the network failure show up as a domain lookup error or connection timeout?

---

## Lab 4: Custom Metrics, Attributes & Events (20 Minutes)
**Scenario**: The Marketing team wants to know which product categories are most popular and how much revenue is sitting in carts. The standard "crash rate" metrics don't tell them this.

**Objective**: Instrument the app to track business KPIs (Key Performance Indicators).

### Challenge 4.1: The "Add to Cart" Event
We need to track every time a user shows intent to buy.
*   **Task**: In `ProductDetailActivity.java`, find the "Add to Cart" button listener.
*   **Action**: Record a custom event named `AddToCart`.
*   **Requirement**: Include `price`, `category`, and `product_name` as attributes.
*   *Hint*: `NewRelic.recordCustomEvent(...)`

### Challenge 4.2: Segmentation (User Tiers)
We want to compare the experience of "Premium" users vs "Standard" users.
*   **Task**: In `MainActivity.java`, set a session-level attribute called `user_tier`.
*   **Action**: Hardcode it to "Premium" for this workshop (or randomize it if you feel adventurous).
*   *Hint*: `NewRelic.setAttribute(...)`

### Verification & Discovery
1.  **Run the App**: Add multiple items to the cart.
2.  **New Relic Platform**:
    *   Go to **Dashboards** > **Create a dashboard**.
    *   **Widget 1 (Revenue)**: Create a Billboard showing the sum of prices in the cart.
        *   `SELECT sum(price) FROM AddToCart`
    *   **Widget 2 (Popularity)**: Create a Bar Chart showing count of adds by category.
        *   `SELECT count(*) FROM AddToCart FACET category`

---

## Lab 5: Crash Analysis (15 Minutes)
**Scenario**: Critical Alert! The app is crashing for users when they try to access the "Crash Test" feature (ironic, isn't it?). We need to find the root cause immediately.

**Objective**: Analyze a fatal crash, identify the line of code, and understand the context.

### Challenge 5.1: The Red Button
The developer left a "Crash Test" button in the production build (oops).
*   **Task**: In `CrashActivity.java`, uncomment the code that forces a crash.
*   *Hint*: `NewRelic.crashNow()`

### Verification & Discovery
1.  **Run the App**: Open the menu -> "Crash Test". Wait for the crash.
2.  **New Relic Platform**:
    *   Go to **Crash Analysis**.
    *   **Triage**: Click on the crash group.
    *   **Root Cause**: Look at the **Stack Trace**. Which line number failed?
    *   **Context**: Click the **Breadcrumbs** tab. Did you see the `ViewProduct` events leading up to this crash? This proves the user was browsing before they decided to crash the app.

---

## Wrap Up
**Discussion**:
*   How did "Handled Exceptions" differ from "Crashes"?
*   How can the "AddToCart" data help the business team?
*   Why are Breadcrumbs useful for debugging crashes?
