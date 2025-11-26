#!/bin/bash
set -e

echo "🚀 ExpenseNoteApp Development Environment Setup"
echo ""

# Update system packages
echo "📦 Updating system packages..."
apt-get update
apt-get install -y postgresql-client git curl

# Setup Maven
echo "🔨 Setting up Maven..."
apt-get install -y maven

# Setup Node.js (optional, if not installed)
echo "⚙️ Setting up Node.js..."
npm install -g npm@latest

# Clone workspace settings
echo "📋 Configuring VS Code settings..."
mkdir -p ~/.vscode

# Setup PostgreSQL connection
echo "🐘 Configuring PostgreSQL client..."
mkdir -p ~/.postgres
echo "host=postgres\nuser=postgres\npassword=postgres\nport=5432\n" > ~/.postgres/.pgpass
chmod 600 ~/.postgres/.pgpass

# Install global npm packages
echo "📚 Installing global npm tools..."
npm install -g \
  @angular/cli@latest \
  typescript@latest \
  prettier@latest \
  eslint@latest

# Maven setup
echo "🏗️ Configuring Maven..."
mkdir -p ~/.m2

# Download Maven dependencies (optional, can be slow)
# echo "⬇️ Downloading Maven dependencies (this may take a while)..."
# cd /workspace/backend-springboot && mvn dependency:resolve

echo ""
echo "✅ Development environment setup complete!"
echo ""
echo "📖 Next steps:"
echo "   1. Open a terminal and run: cd backend-springboot && mvn spring-boot:run"
echo "   2. Open another terminal: cd frontend && npm install && npm run dev"
echo "   3. Access frontend: http://localhost:3000"
echo "   4. Access backend: http://localhost:8080"
echo ""
echo "💡 Tips:"
echo "   - Use 'mvn clean compile' to check for errors"
echo "   - Use 'npm run build' to build frontend"
echo "   - Use 'docker-compose up' for containerized environment"
echo ""
