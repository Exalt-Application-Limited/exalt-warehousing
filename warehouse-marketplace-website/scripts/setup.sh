#!/bin/bash

# Exalt Warehouse Marketplace Website - Setup Script
# This script performs the initial setup for the warehouse marketplace website

set -e

echo "🏗️ Exalt Warehouse Marketplace Website - Initial Setup"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Ensure we're in the correct directory
if [ ! -f "package.json" ]; then
    print_error "package.json not found. Make sure you're in the warehouse-marketplace-website directory."
    exit 1
fi

# Check system requirements
print_status "Checking system requirements..."

# Check Node.js
if ! command -v node &> /dev/null; then
    print_error "Node.js is required but not installed."
    print_status "Please install Node.js 18+ from https://nodejs.org/"
    exit 1
fi

NODE_VERSION=$(node --version)
print_success "Node.js $NODE_VERSION is installed"

# Check npm
if ! command -v npm &> /dev/null; then
    print_error "npm is required but not installed."
    exit 1
fi

NPM_VERSION=$(npm --version)
print_success "npm $NPM_VERSION is installed"

# Check Docker (optional)
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_success "Docker is available: $DOCKER_VERSION"
else
    print_warning "Docker not found. Docker is optional but recommended for deployment."
fi

# Install dependencies
print_status "Installing project dependencies..."
npm install

print_success "Dependencies installed successfully"

# Setup environment
print_status "Setting up environment configuration..."

if [ ! -f ".env" ]; then
    cp .env.example .env
    print_success "Environment file created from template"
    print_warning "Please review and update .env file with your configuration"
    print_warning "Don't forget to add your Google Maps API key and Stripe public key"
else
    print_warning ".env file already exists"
fi

# Create necessary directories
print_status "Creating project directories..."

directories=(
    "public/images"
    "public/screenshots"
    "public/icons"
    "src/assets/images"
    "coverage"
    "dist"
    "logs"
    "docker"
)

for dir in "${directories[@]}"; do
    mkdir -p "$dir"
    print_status "Created directory: $dir"
done

# Create docker environment script
print_status "Creating Docker environment injection script..."
cat > docker/env.sh << 'EOF'
#!/bin/sh
# Replace environment variables in built files
find /usr/share/nginx/html -name "*.js" -exec sed -i "s|REACT_APP_API_BASE_URL_PLACEHOLDER|${REACT_APP_API_BASE_URL}|g" {} \;
find /usr/share/nginx/html -name "*.js" -exec sed -i "s|REACT_APP_GOOGLE_MAPS_API_KEY_PLACEHOLDER|${REACT_APP_GOOGLE_MAPS_API_KEY}|g" {} \;
EOF
chmod +x docker/env.sh

# Setup Git hooks (if in a git repository)
if [ -d ".git" ]; then
    print_status "Setting up Git hooks..."
    
    # Create pre-commit hook
    cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash
echo "Running pre-commit checks for warehouse marketplace..."

# Run linting
npm run lint
if [ $? -ne 0 ]; then
    echo "Linting failed. Please fix errors before committing."
    exit 1
fi

# Run type checking
npm run type-check
if [ $? -ne 0 ]; then
    echo "Type checking failed. Please fix type errors before committing."
    exit 1
fi

# Run tests
npm run test:ci
if [ $? -ne 0 ]; then
    echo "Tests failed. Please fix failing tests before committing."
    exit 1
fi

echo "Pre-commit checks passed!"
EOF
    
    chmod +x .git/hooks/pre-commit
    print_success "Git pre-commit hook installed"
fi

# Generate project documentation
print_status "Generating project documentation..."

# Create component documentation
mkdir -p docs
cat > docs/COMPONENTS.md << 'EOF'
# Warehouse Marketplace Website - Component Documentation

## Component Architecture

The warehouse marketplace website follows a domain-driven component architecture:

### Directory Structure

```
src/components/
├── common/              # Reusable common components
│   ├── ErrorBoundary.tsx
│   ├── LoadingSpinner.tsx
│   ├── ProtectedRoute.tsx
│   └── SEOHelmet.tsx
├── layout/              # Layout components
│   ├── Header.tsx
│   ├── Footer.tsx
│   ├── Navigation.tsx
│   └── MobileNavigation.tsx
├── search/              # Search functionality
│   ├── FacilitySearchForm.tsx
│   ├── SearchFilters.tsx
│   ├── SearchResults.tsx
│   ├── SearchMap.tsx
│   └── LocationPicker.tsx
├── facility/            # Facility-related components
│   ├── FacilityCard.tsx
│   ├── FacilityDetail.tsx
│   ├── FacilityGallery.tsx
│   ├── FacilityComparison.tsx
│   ├── UnitGrid.tsx
│   └── VirtualTour.tsx
├── booking/             # Booking process
│   ├── BookingForm.tsx
│   ├── UnitSelection.tsx
│   ├── PaymentForm.tsx
│   ├── RentalAgreement.tsx
│   └── BookingConfirmation.tsx
├── dashboard/           # Customer dashboard
│   ├── BookingHistory.tsx
│   ├── PaymentHistory.tsx
│   ├── AccountSettings.tsx
│   ├── AccessCredentials.tsx
│   └── SupportTickets.tsx
└── customer/            # Customer-specific features
    ├── ProfileForm.tsx
    ├── EmergencyContact.tsx
    ├── InsuranceOptions.tsx
    └── MoveInScheduler.tsx
```

## Component Guidelines

1. **TypeScript**: All components must use TypeScript with proper interfaces
2. **Material-UI**: Follow Material-UI design patterns and theming
3. **Responsive Design**: Mobile-first approach with breakpoint considerations
4. **Accessibility**: WCAG 2.1 AA compliance with proper ARIA labels
5. **Testing**: Each component should have corresponding unit tests
6. **Documentation**: Complex components should include JSDoc comments

## Key Components

### FacilitySearchForm
Main search interface with location input, filters, and map integration.

### FacilityCard
Displays facility information in grid/list view with key details and actions.

### BookingForm
Multi-step booking process with validation and payment integration.

### SearchMap
Interactive map with facility markers, clustering, and route planning.
EOF

print_success "Component documentation created"

# Create API documentation
cat > docs/API.md << 'EOF'
# API Integration Guide

## Service Endpoints

### Warehouse Marketplace API (Port 8083)
- **Base URL**: `${REACT_APP_WAREHOUSE_MARKETPLACE_API_URL}`
- **Purpose**: Facility search, discovery, and basic information

#### Key Endpoints:
- `GET /facilities/search` - Search facilities by location and filters
- `GET /facilities/{id}` - Get detailed facility information
- `GET /facilities/{id}/units` - Get available units for a facility
- `GET /facilities/featured` - Get featured facilities

### Storage Management API (Port 8084)
- **Base URL**: `${REACT_APP_STORAGE_MANAGEMENT_API_URL}`
- **Purpose**: Booking management and customer accounts

#### Key Endpoints:
- `POST /bookings` - Create new booking
- `GET /bookings/{id}` - Get booking details
- `PUT /bookings/{id}` - Update booking
- `GET /customers/{id}/bookings` - Get customer bookings

### Billing Service (Port 8200)
- **Base URL**: `${REACT_APP_BILLING_SERVICE_URL}`
- **Purpose**: Payment processing and financial transactions

#### Key Endpoints:
- `POST /payments/process` - Process payment
- `GET /payments/{id}` - Get payment details
- `POST /payments/setup-intent` - Create payment setup intent
- `GET /invoices/{bookingId}` - Get booking invoices

## Authentication

All API calls require JWT authentication:

```typescript
const authHeaders = {
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json',
};
```

## Error Handling

Standard error response format:

```json
{
  "error": {
    "code": "FACILITY_NOT_FOUND",
    "message": "The requested facility could not be found",
    "details": {}
  }
}
```
EOF

print_success "API documentation created"

# Run initial quality checks
print_status "Running quality checks..."

# Lint check
if npm run lint > /dev/null 2>&1; then
    print_success "Code linting passed"
else
    print_warning "Linting issues found. Run 'npm run lint:fix' to fix automatically."
fi

# Type check
if npm run type-check > /dev/null 2>&1; then
    print_success "TypeScript type checking passed"
else
    print_warning "TypeScript type errors found. Please review and fix."
fi

# Test run (skip if no tests yet)
print_status "Running initial test suite..."
if npm run test:ci > /dev/null 2>&1; then
    print_success "All tests passed"
else
    print_warning "Some tests failed or no tests found. This is normal for initial setup."
fi

print_success "🎉 Warehouse Marketplace Website setup completed successfully!"
print_status ""
print_status "Next steps:"
print_status "1. Review and update .env file with your API endpoints and keys"
print_status "2. Add Google Maps API key for location services"
print_status "3. Configure Stripe public key for payments"
print_status "4. Start development server: npm start"
print_status "5. Open http://localhost:3001 in your browser"
print_status ""
print_status "Useful commands:"
print_status "  npm start              - Start development server"
print_status "  npm test               - Run tests in watch mode"
print_status "  npm run build          - Build for production"
print_status "  npm run lint           - Check code quality"
print_status "  npm run type-check     - Check TypeScript types"
print_status "  docker-compose up      - Run with Docker"
print_status ""
print_status "Documentation:"
print_status "  docs/COMPONENTS.md     - Component architecture guide"
print_status "  docs/API.md           - API integration guide"
print_status "  README.md             - Full project documentation"
print_status ""