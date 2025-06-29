# Vendor Self-Storage Frontend Applications

This directory contains the frontend applications for the Vendor Self-Storage service, providing both web and mobile interfaces for vendors to manage their storage locations and order fulfillment.

## 📁 Project Structure

```
frontend/
├── web-portal/          # React web application
│   ├── src/
│   │   ├── components/  # Reusable UI components
│   │   ├── pages/       # Main application pages
│   │   ├── hooks/       # Custom React hooks
│   │   ├── api/         # API client and services
│   │   ├── store/       # State management (Zustand)
│   │   ├── types/       # TypeScript type definitions
│   │   ├── utils/       # Utility functions
│   │   └── assets/      # Static assets
│   ├── public/          # Public static files
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
│
├── mobile-app/          # React Native (Expo) application
│   ├── app/             # Expo Router pages
│   │   ├── (tabs)/      # Tab navigation pages
│   │   ├── auth/        # Authentication screens
│   │   └── onboarding/  # Onboarding flow
│   ├── components/      # Reusable components
│   ├── api/             # API client
│   ├── store/           # State management
│   ├── utils/           # Utility functions
│   ├── assets/          # Images, icons, etc.
│   ├── app.json         # Expo configuration
│   └── package.json
│
└── shared/              # Shared code between web and mobile
    ├── types/           # Common TypeScript types
    ├── api/             # Shared API definitions
    └── utils/           # Common utility functions
```

## 🌐 Web Portal

### Features
- **Dashboard**: Overview of storage locations, orders, and performance metrics
- **Location Management**: Add, edit, and monitor storage locations
- **Order Processing**: View, process, and fulfill orders with label printing
- **Analytics**: Performance tracking and reporting
- **Settings**: Account and notification preferences

### Technology Stack
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **State Management**: Zustand + React Query
- **Routing**: React Router v6
- **Forms**: React Hook Form + Zod validation
- **Charts**: Recharts
- **Icons**: Heroicons

### Getting Started

```bash
cd web-portal

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Run tests
npm test

# Type checking
npm run type-check
```

### Environment Variables

Create a `.env.local` file:

```env
VITE_API_BASE_URL=http://localhost:8085
VITE_WS_URL=ws://localhost:8085
VITE_APP_NAME=Vendor Storage Portal
VITE_APP_VERSION=1.0.0
```

## 📱 Mobile App

### Features
- **Dashboard**: Mobile-optimized overview and quick actions
- **QR Code Scanner**: Scan order codes for quick processing
- **Camera Integration**: Take photos of packages
- **Label Printing**: Generate and print labels via Bluetooth printers
- **Push Notifications**: Real-time order and alert notifications
- **Offline Support**: Basic functionality when offline
- **Location Services**: GPS verification for storage addresses

### Technology Stack
- **Framework**: React Native (Expo)
- **Navigation**: Expo Router
- **State Management**: Zustand + React Query
- **Forms**: React Hook Form + Zod validation
- **UI Components**: React Native Paper
- **Camera**: Expo Camera + Barcode Scanner
- **Printing**: Expo Print + Bluetooth integration
- **Maps**: React Native Maps

### Getting Started

```bash
cd mobile-app

# Install dependencies
npm install

# Start Expo development server
npm start

# Run on iOS simulator
npm run ios

# Run on Android emulator
npm run android

# Build for production
npm run build:android
npm run build:ios
```

### Prerequisites

1. **Expo CLI**: `npm install -g @expo/cli`
2. **EAS CLI**: `npm install -g eas-cli`
3. **iOS**: Xcode and iOS Simulator
4. **Android**: Android Studio and emulator

### Environment Configuration

Create an `app.config.js` file:

```javascript
export default {
  expo: {
    name: 'Vendor Storage',
    slug: 'vendor-storage-mobile',
    extra: {
      apiBaseUrl: process.env.API_BASE_URL || 'http://localhost:8085',
      wsUrl: process.env.WS_URL || 'ws://localhost:8085',
    },
  },
};
```

## 🔗 API Integration

Both applications connect to the Self-Storage Service backend:

### API Endpoints
- **Base URL**: `http://localhost:8085/api/v1/vendor-storage`
- **WebSocket**: `ws://localhost:8085/vendor-portal/ws`

### Authentication
- **Type**: JWT Bearer tokens
- **Storage**: 
  - Web: localStorage
  - Mobile: Expo SecureStore

### API Client Example

```typescript
// api/client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.VITE_API_BASE_URL + '/api/v1/vendor-storage',
  timeout: 10000,
});

// Add auth interceptor
apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default apiClient;
```

## 🧪 Testing

### Web Portal Testing

```bash
# Unit tests
npm test

# Coverage report
npm run test:coverage

# UI tests (recommended: Playwright)
npm run test:e2e
```

### Mobile App Testing

```bash
# Unit tests
npm test

# Component tests
npm run test:watch

# E2E tests (Detox recommended)
npm run test:e2e
```

## 📦 Deployment

### Web Portal Deployment

```bash
# Build optimized production bundle
npm run build

# Preview production build
npm run preview

# Deploy to hosting service (Vercel, Netlify, etc.)
```

### Mobile App Deployment

```bash
# Configure EAS
eas login
eas build:configure

# Build for app stores
eas build --platform ios
eas build --platform android

# Submit to app stores
eas submit --platform ios
eas submit --platform android
```

## 🔧 Development Guidelines

### Code Structure
- **Components**: Use functional components with hooks
- **State**: Zustand for global state, React Query for server state
- **Styling**: Tailwind CSS classes (web), StyleSheet API (mobile)
- **Types**: Comprehensive TypeScript coverage

### Best Practices
- **Performance**: Lazy loading, code splitting, image optimization
- **Accessibility**: ARIA labels, semantic HTML, high contrast support
- **Responsive Design**: Mobile-first approach for web
- **Error Handling**: Comprehensive error boundaries and user feedback
- **Security**: Input validation, secure storage, HTTPS enforcement

### Shared Dependencies

```bash
# Install shared utilities
npm install @vendor-storage/shared

# Use shared types
import type { VendorStorageLocation } from '@vendor-storage/shared/types';
```

## 🚀 Features Roadmap

### Phase 1 (Current)
- ✅ Basic dashboard and location management
- ✅ Order processing and label printing
- ✅ QR code scanning
- ✅ Real-time updates

### Phase 2 (Planned)
- 🔄 Advanced analytics and reporting
- 🔄 Bulk operations
- 🔄 Inventory management integration
- 🔄 Multi-language support

### Phase 3 (Future)
- 📋 Offline-first mobile app
- 📋 Advanced notification system
- 📋 Integration with external printer APIs
- 📋 Voice commands and accessibility

## 📄 License

This project is part of the Ecosystem Platform and follows the same licensing terms.

## 🤝 Contributing

1. Follow the existing code style and patterns
2. Add tests for new features
3. Update documentation as needed
4. Submit pull requests for review

## 📞 Support

For technical support or questions:
- **Documentation**: Check the main project README
- **Issues**: Create GitHub issues for bugs or feature requests
- **Discussion**: Use GitHub Discussions for general questions