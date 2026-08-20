#!/bin/bash
echo "==================================================="
echo "       Push Code to GitHub                         "
echo "==================================================="
echo ""
echo "Committing all new changes..."
read -p "Enter your commit message (e.g., 'feat: added new login method' or 'fix: resolved timeout issue'): " commitMsg

if [ -z "$commitMsg" ]; then
    commitMsg="chore: update code"
fi

git add .
git commit -m "$commitMsg"
echo ""
echo "Pushing the code to GitHub..."
if ! git push origin main; then
    echo -e "\033[31mERROR: Git push failed. Please make sure you authenticate when prompted.\033[0m"
    exit 1
fi

echo ""
echo -e "\033[32m===================================================\033[0m"
echo -e "\033[32m SUCCESS! Code pushed to GitHub! \033[0m"
echo -e "\033[32m===================================================\033[0m"
echo ""
echo "A 'Release PR' will automatically be created on your GitHub repository by the Release Please bot."
echo "To publish a new version and generate the CHANGELOG:"
echo "  1. Go to your GitHub repository -> Pull Requests."
echo "  2. Merge the 'Release' pull request."
echo "  3. Once merged, GitHub will automatically create the release, write the CHANGELOG.md file, and deploy to Maven Central!"
echo ""
echo "Opening your browser to your GitHub Pull Requests page..."

# Open browser based on OS
if which xdg-open > /dev/null; then
  xdg-open "https://github.com/growthquantix/upstox-auth-pro-java/pulls"
elif which open > /dev/null; then
  open "https://github.com/growthquantix/upstox-auth-pro-java/pulls"
else
  echo "Please open https://github.com/growthquantix/upstox-auth-pro-java/pulls in your browser."
fi
