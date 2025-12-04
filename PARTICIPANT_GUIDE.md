# New Relic Mobile Agent Workshop - Participant Guide

Welcome to the **Reli-Store** workshop! In this session, you will play the role of a Mobile Engineer/SRE tasked with fixing and optimizing a retail Android application using New Relic.

## 📚 Resources
*   **[Lab Manual](LAB_MANUAL.md)**: Your step-by-step guide for the challenges.
*   **[New Relic Documentation](https://docs.newrelic.com/docs/mobile-monitoring/new-relic-mobile-android/get-started/introduction-new-relic-mobile-android/)**: Official reference docs.

# Workshop Strategy: New Relic Mobile Agent

**Approach**: Progressive Instrumentation (Greenfield Style)
**Duration**: 90-120 Minutes
**Goal**: Participants start with an un-instrumented app (or partially instrumented) and progressively add New Relic features to gain visibility.

## Workshop Flow

| Time | Topic | Activity | Verification |
| :--- | :--- | :--- | :--- |
| **0-15m** | **Lab 1: Setup** | Add Agent to `build.gradle`, Initialize in `MainActivity`. | View "Summary" page in New Relic. |
| **15-30m** | **Lab 2: Interactions** | Add `recordBreadcrumb` in `ProductDetailActivity`. | View "Breadcrumbs" in Session Timeline. |
| **30-50m** | **Lab 3: Errors** | Add `recordHandledException` in `FetchProductDetailsTask`. Simulate HTTP error. | View "Handled Exceptions" & "Network" pages. |
| **50-65m** | **Lab 4: Custom Data** | Add `recordCustomEvent` (AddToCart) & `setAttribute`. | Query in "Data Explorer" & Build Dashboard. |
| **65-85m** | **Lab 5: Crashes** | Trigger `crashNow()` in `CrashActivity`. | Analyze stack trace in "Crash Analysis". |
| **85-90m** | **Wrap Up** | Q&A, Share resources. | - |



## 🛠️ Setup Checklist
Before we begin, ensure you have:
1.  **Android Studio** installed.
2.  **Node.js** installed (for the backend server).
3.  **New Relic Account** (Log in and have it ready).


### Prep:
1.  **Clone Repo**: `git clone https://github.com/zmrfzn/Android_App.git`
2.  **Checkout Branch**: `workshop`

### Server Setup (Required)
The app relies on a local backend server to fetch product data.
1.  **Navigate**: Open a terminal in the `server/` directory.
2.  **Install**: Run `npm install` to install dependencies.
3.  **Start**: Run `npm start` (or `node index.js`).
4.  **Verify**: Ensure the server is running on port 3000.

### 🚀 Getting Started
1.  **Start the Server**:
    ```bash
    cd server
    npm install
    npm start
    ```
2.  **Open the Project**: Open the `Android_App` folder in Android Studio.
3.  **Sync Gradle**: Ensure the project builds (it might have some warnings, that's expected!).

### 🧩 The Challenges
The codebase has been prepared with specific "Challenges" for you to solve. Look for comments like `// Challenge X.X` in the code to find where you need to work.

Good luck!
