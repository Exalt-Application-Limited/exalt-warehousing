# Gogidix Warehouse Marketplace Website

A modern React-based storage marketplace web application for the Gogidix Warehousing ecosystem, enabling customers to discover, compare, and book storage units online.

## 🏗️ Architecture Overview

The Warehouse Marketplace Website is designed as a comprehensive B2C platform that integrates with the Gogidix warehousing microservices ecosystem to provide:

- **Storage Discovery**: Location-based search with advanced filtering
- **Facility Comparison**: Side-by-side comparison of storage facilities
- **Online Booking**: Complete rental agreement and payment processing
- **Customer Dashboard**: Account management and booking tracking
- **Size Calculator**: Interactive tool to determine storage needs

## 🚀 Features

### Core Functionality
- **Intelligent Search**: GPS-powered location detection with radius-based filtering
- **Advanced Filtering**: Size, price, amenities, security features, and availability
- **Interactive Maps**: Leaflet-based mapping with facility markers and directions
- **Virtual Tours**: 360° facility and unit previews
- **Real-time Availability**: Live unit availability with instant booking
- **Price Comparison**: Dynamic pricing with discount calculations
- **Mobile-First Design**: Responsive PWA with offline capabilities

### Business Features
- **Storage Size Calculator**: Interactive tool for determining storage needs
- **Moving Services Integration**: Truck rentals, packing supplies, and assistance
- **Insurance Options**: Multiple coverage levels and providers
- **Business Storage**: Specialized solutions for commercial customers
- **Facility Reviews**: Verified customer reviews and ratings
- **Promotional Pricing**: Dynamic discounts and seasonal offers

### Technical Features
- **Performance Optimized**: Code splitting, lazy loading, image optimization
- **SEO Optimized**: Server-side rendering, schema markup, meta tags
- **Accessibility**: WCAG 2.1 AA compliant with screen reader support
- **Security**: CSP headers, input validation, secure authentication
- **Analytics**: Comprehensive tracking and conversion optimization

## 🛠️ Technology Stack

### Frontend Core
- **React** 18.2.0 - Modern UI library with concurrent features
- **TypeScript** 4.9.5 - Type-safe development
- **Material-UI** 5.x - Enterprise-grade component library
- **Redux Toolkit** - Predictable state management
- **RTK Query** - Efficient data fetching and caching

### Mapping & Location
- **React Leaflet** 4.2.1 - Interactive maps and geolocation
- **Google Maps API** - Geocoding and place search
- **Mapbox** - Satellite imagery and routing

### Development Tools
- **ESLint** - Code quality and consistency
- **Prettier** - Code formatting
- **Jest** - Unit and integration testing
- **React Testing Library** - Component testing
- **Storybook** - Component development and documentation

### Deployment
- **Docker** - Containerized deployment
- **Nginx** - High-performance web server
- **Kubernetes** - Container orchestration with auto-scaling
- **GitHub Actions** - CI/CD pipeline

## 📦 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Docker (for containerization)
- Kubernetes cluster (for deployment)

### Local Development

```bash
# Clone and install
git clone <repository-url>
cd warehouse-marketplace-website
npm install

# Environment setup
cp .env.example .env
# Edit .env with your configuration

# Start development server
npm start
```

### Docker Deployment

```bash
# Build and run container
docker build -t gogidix-warehouse-marketplace .
docker run -p 3001:80 gogidix-warehouse-marketplace

# Using docker-compose
docker-compose up
```

### Kubernetes Deployment

```bash
# Deploy to cluster
kubectl apply -f k8s/

# Check deployment
kubectl get pods -n gogidix-production -l app=warehouse-marketplace-website
```

## 🏗️ Project Structure

```
src/
├── components/
│   ├── common/              # Reusable UI components
│   ├── layout/              # Layout components (Header, Footer, Navigation)
│   ├── search/              # Search functionality components
│   │   ├── FacilitySearchForm.tsx
│   │   ├── SearchFilters.tsx
│   │   ├── SearchResults.tsx
│   │   └── SearchMap.tsx
│   ├── facility/            # Facility-related components
│   │   ├── FacilityCard.tsx
│   │   ├── FacilityDetail.tsx
│   │   ├── FacilityGallery.tsx
│   │   └── FacilityComparison.tsx
│   ├── booking/             # Booking process components
│   │   ├── BookingForm.tsx
│   │   ├── PaymentForm.tsx
│   │   ├── RentalAgreement.tsx
│   │   └── BookingConfirmation.tsx
│   ├── dashboard/           # Customer dashboard components
│   │   ├── BookingHistory.tsx
│   │   ├── PaymentHistory.tsx
│   │   ├── AccountSettings.tsx
│   │   └── SupportTickets.tsx
│   └── customer/            # Customer-specific components
├── pages/                   # Page components
│   ├── HomePage.tsx
│   ├── SearchPage.tsx
│   ├── FacilityDetailPage.tsx
│   ├── BookingPage.tsx
│   ├── DashboardPage.tsx
│   └── auth/
├── services/                # API integration
│   ├── facilityService.ts
│   ├── bookingService.ts
│   ├── paymentService.ts
│   └── authService.ts
├── store/                   # Redux store
│   ├── api/                 # RTK Query API definitions
│   └── slices/              # Redux slices
├── types/                   # TypeScript definitions
│   ├── facility.ts
│   ├── booking.ts
│   ├── auth.ts
│   └── common.ts
├── utils/                   # Utility functions
├── hooks/                   # Custom React hooks
└── styles/                  # Themes and global styles
```

## 🔌 API Integration

The website integrates with multiple warehousing microservices:

### Core Services
- **Warehouse Marketplace API** (Port 8083) - Facility search and discovery
- **Storage Management API** (Port 8084) - Booking and rental management
- **Billing Service** (Port 8200) - Payment processing and invoicing

### Supporting Services
- **Inventory Service** - Personal inventory tracking
- **Analytics Service** - Usage analytics and recommendations
- **Auth Service** - Authentication and authorization
- **Notification Service** - Email, SMS, and push notifications

### API Configuration

```typescript
// API endpoints configuration
const API_ENDPOINTS = {
  WAREHOUSE_MARKETPLACE: process.env.REACT_APP_WAREHOUSE_MARKETPLACE_API_URL,
  STORAGE_MANAGEMENT: process.env.REACT_APP_STORAGE_MANAGEMENT_API_URL,
  BILLING: process.env.REACT_APP_BILLING_SERVICE_URL,
  INVENTORY: process.env.REACT_APP_INVENTORY_SERVICE_URL,
  ANALYTICS: process.env.REACT_APP_ANALYTICS_SERVICE_URL,
};
```

## 🗺️ Mapping & Geolocation

### Map Integration
```typescript
// Leaflet map configuration
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';

const FacilityMap = ({ facilities, center, zoom }) => {
  return (
    <MapContainer center={center} zoom={zoom}>
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      {facilities.map(facility => (
        <Marker key={facility.id} position={[facility.lat, facility.lng]}>
          <Popup>{facility.name}</Popup>
        </Marker>
      ))}
    </MapContainer>
  );
};
```

### Location Services
```typescript
// Geolocation hook
const useGeolocation = () => {
  const [location, setLocation] = useState(null);
  
  useEffect(() => {
    navigator.geolocation.getCurrentPosition(
      position => setLocation({
        lat: position.coords.latitude,
        lng: position.coords.longitude
      }),
      error => console.error('Geolocation error:', error)
    );
  }, []);
  
  return location;
};
```

## 💳 Payment Integration

### Stripe Integration
```typescript
// Payment form component
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';

const PaymentForm = ({ booking, onSuccess }) => {
  const stripe = useStripe();
  const elements = useElements();
  
  const handleSubmit = async (event) => {
    event.preventDefault();
    
    const result = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
        billing_details: { name: booking.customer.name }
      }
    });
    
    if (result.error) {
      console.error(result.error.message);
    } else {
      onSuccess(result.paymentIntent);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      <button type="submit" disabled={!stripe}>Pay Now</button>
    </form>
  );
};
```

## 🧪 Testing

### Unit Testing
```bash
# Run tests
npm test

# Run with coverage
npm run test:coverage

# Run in CI mode
npm run test:ci
```

### Integration Testing
```typescript
// Example facility search test
describe('FacilitySearch', () => {
  it('should search facilities by location', async () => {
    render(<FacilitySearchForm />);
    
    const locationInput = screen.getByPlaceholderText('Enter city or ZIP code');
    const searchButton = screen.getByText('Search');
    
    fireEvent.change(locationInput, { target: { value: 'New York, NY' } });
    fireEvent.click(searchButton);
    
    await waitFor(() => {
      expect(screen.getByText('Storage facilities in New York, NY')).toBeInTheDocument();
    });
  });
});
```

## 🚀 Performance

### Optimization Strategies
- **Code Splitting**: Route-based and component-based splitting
- **Image Optimization**: WebP format with fallbacks, lazy loading
- **Caching**: Redux state persistence, API response caching
- **Bundle Analysis**: Webpack bundle analyzer integration

### Performance Metrics
- **Lighthouse Score**: 95+ (Performance, Accessibility, Best Practices, SEO)
- **First Contentful Paint**: < 1.2s
- **Time to Interactive**: < 2.5s
- **Cumulative Layout Shift**: < 0.1

## 🔒 Security

### Security Measures
- **Content Security Policy**: Comprehensive CSP headers
- **Input Validation**: Client and server-side validation
- **Authentication**: JWT tokens with refresh mechanism
- **HTTPS Enforcement**: SSL/TLS termination at ingress level
- **Data Sanitization**: XSS prevention and SQL injection protection

### Security Headers
```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer-when-downgrade" always;
add_header Content-Security-Policy "default-src 'self'" always;
```

## 🌍 Internationalization

### Multi-language Support
```typescript
// i18n configuration
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

i18n.use(initReactI18next).init({
  resources: {
    en: { translation: require('./locales/en.json') },
    es: { translation: require('./locales/es.json') },
    fr: { translation: require('./locales/fr.json') },
  },
  lng: 'en',
  fallbackLng: 'en',
});
```

## 📊 Analytics

### Tracking Implementation
```typescript
// Google Analytics 4 integration
import { gtag } from 'ga-gtag';

// Track facility search
const trackFacilitySearch = (location, filters) => {
  gtag('event', 'search', {
    event_category: 'facility',
    event_label: location,
    custom_parameters: filters
  });
};

// Track booking conversion
const trackBookingComplete = (facilityId, unitSize, price) => {
  gtag('event', 'purchase', {
    event_category: 'booking',
    value: price,
    currency: 'USD',
    items: [{ item_id: facilityId, item_name: unitSize }]
  });
};
```

## 🚀 Deployment

### Environment Configuration

| Environment | Domain | Purpose |
|-------------|--------|---------|
| Development | localhost:3001 | Local development |
| Staging | staging-storage.exaltapp.com | QA testing |
| Production | storage.exaltapp.com | Live customer traffic |

### Deployment Pipeline
1. **Build**: TypeScript compilation and bundling
2. **Test**: Unit and integration test execution
3. **Security Scan**: Dependency vulnerability scanning
4. **Docker Build**: Multi-stage container creation
5. **Deploy**: Kubernetes rolling deployment
6. **Health Check**: Automated health verification

## 📞 Support

### Development Team
- **Frontend Team**: frontend@exaltapp.com
- **Backend Integration**: api@exaltapp.com
- **DevOps**: devops@exaltapp.com

### Documentation
- **API Documentation**: https://docs.exaltapp.com/warehousing
- **Design System**: https://design.exaltapp.com
- **Deployment Guide**: https://deploy.exaltapp.com

## 📄 License

This project is proprietary software owned by Gogidix Application Limited. All rights reserved.