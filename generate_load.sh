#!/bin/bash

# Package name of the app
PACKAGE="com.newrelic.relistore"

# Number of events to send (Default: 1000)
EVENTS=${1:-1000}

echo "🚀 Starting Load Test for $PACKAGE with $EVENTS events..."
echo "⚠️  Ensure your emulator is running and the app is installed."

# Run ADB Monkey
# -p: Package name
# -v: Verbose output
# --throttle 100: 100ms delay between events (makes it more realistic/watchable)
# --pct-touch 40: 40% touch events
# --pct-motion 25: 25% motion events
# --pct-syskeys 10: 10% system keys (Back, Home, etc - risky but good for stress)
# --pct-appswitch 10: 10% app switching
adb shell monkey -p $PACKAGE --throttle 100 -v $EVENTS

echo "✅ Load Test Complete!"
