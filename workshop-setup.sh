#!/bin/bash
# workshop-setup.sh - Setup script for workshop participants

REPO_URL="https://github.com/zmrfzn/relistore_android.git"
WORKSHOP_BRANCH="workshop"

echo "🎓 Setting up workshop environment..."

# Clone only the workshop branch
git clone --single-branch --branch $WORKSHOP_BRANCH $REPO_URL
cd relistore_android

# Restrict fetch to only workshop branch
git config remote.origin.fetch "+refs/heads/$WORKSHOP_BRANCH:refs/remotes/origin/$WORKSHOP_BRANCH"

# Create post-checkout hook to prevent switching to solution
mkdir -p .git/hooks
cat > .git/hooks/post-checkout << 'EOF'
#!/bin/bash
CURRENT_BRANCH=$(git symbolic-ref HEAD 2>/dev/null | sed -e 's,.*/\(.*\),\1,')

if [ "$CURRENT_BRANCH" = "main" ] || [ "$CURRENT_BRANCH" = "solution" ]; then
    echo "❌ Access to solution branch is restricted during workshop!"
    echo "Please work on the 'workshop' branch."
    git checkout workshop 2>/dev/null || git checkout -
    exit 1
fi
EOF

chmod +x .git/hooks/post-checkout



echo "✅ Workshop environment ready!"
echo "📁 You're on the 'workshop' branch - good luck!"
echo "👉 Run 'cd relistore_android' to enter the project directory"
echo ""
echo "💡 Tip: Focus on solving the challenges yourself first."
echo "   Solutions will be provided at the end of the session."