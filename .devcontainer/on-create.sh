## Install additional apt packages
sudo apt-get update \
    && sudo apt-get install -y dos2unix libsecret-1-0 xdg-utils \
    && sudo apt clean -y \
    && sudo rm -rf /var/lib/apt/lists/*

## Configure git
git config --global pull.rebase false
git config --global core.autocrlf input

## GitHub Copilot CLI ##
npm install -g @githubnext/github-copilot-cli
eval "$(github-copilot-cli alias -- "$0")"

## GitHub Copilot CLI ##
echo '
# Add GitHub Copilot CLI alias
alias ghcp='github-copilot-cli'
eval "$(github-copilot-cli alias -- "$0")"
' >> $HOME/.zshrc
echo '
# Add GitHub Copilot CLI alias
alias ghcp='github-copilot-cli'
eval "$(github-copilot-cli alias -- "$0")"
' >> $HOME/.bashrc

## AZURE BICEP CLI ##
az bicep install

## AZURE DEV CLI ##
curl -fsSL https://aka.ms/install-azd.sh | bash