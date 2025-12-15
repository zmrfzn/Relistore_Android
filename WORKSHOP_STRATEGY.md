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

## Preparation
To run this workshop, the codebase must be in a "clean" state where the New Relic code is present but commented out (or removed).

### Instructor Prep:
1.  **Clone Repo**: `git clone https://github.com/zmrfzn/Android_App.git`
2.  **Checkout Branch**: `workshop`
3.  **Distribute**: Share the repo with participants.

### Server Setup (Required)
The app relies on a local backend server to fetch product data.
1.  **Navigate**: Open a terminal in the `server/` directory.
2.  **Install**: Run `npm install` to install dependencies.
3.  **Start**: Run `npm start` (or `node index.js`).
4.  **Verify**: Ensure the server is running on port 3000.

### Participant Prerequisites:
*   Android Studio installed.
*   Node.js installed (for the server).
*   New Relic Account (Free tier is fine).
*   Emulator or Physical Device.
