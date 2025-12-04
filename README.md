# Reli-Store: New Relic Android Workshop

Welcome to the **Reli-Store** Android application! This project is designed for a hands-on workshop to learn how to instrument mobile applications with the New Relic Mobile Agent.

## 🎯 Workshop Goal
Learn how to gain deep visibility into your mobile app's performance, stability, and user behavior using New Relic. You will start with an un-instrumented app and progressively add features to track crashes, network errors, and business KPIs.

## 📚 Workshop Documentation
*   **[Participant Guide](PARTICIPANT_GUIDE.md)**: Start here! Setup instructions and workshop overview.
*   **[Lab Manual](LAB_MANUAL.md)**: Step-by-step instructions for each challenge.

## 🧩 Workshop Labs
*   **Lab 1**: Agent Installation & Setup
*   **Lab 2**: Breadcrumbs & User Interactions
*   **Lab 3**: Handled Exceptions & Network Errors
*   **Lab 4**: Custom Metrics & Business Events
*   **Lab 5**: Crash Analysis

## 🛠️ Prerequisites
1.  **Android Studio**: Latest stable version.
2.  **Node.js**: v18 LTS or later Required to run the local backend server.
3.  **New Relic Account**: [Sign up for free](https://newrelic.com/signup).

## 🚀 Quick Start
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/zmrfzn/Relistore_Android.git
    cd Relistore_Android
    git checkout workshop
    ```

2.  **Start the Backend Server** (Required):
    The app needs this server to fetch product data.
    ```bash
    cd server
    npm install
    npm start
    ```
    *Ensure the server is running on port 3000.*

3.  **Open in Android Studio**:
    *   Open the `Relistore_Android` folder.
    *   Sync Gradle and run the app on an emulator.


## 🤝 Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## 📄 License
[MIT](https://choosealicense.com/licenses/mit/)
