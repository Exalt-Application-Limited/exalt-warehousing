# staff-mobile-app

## Overview
staff-mobile-app is a React Native mobile application for warehouse staff to manage daily warehouse operations, inventory tracking, and task assignments.

## Technology Stack
- React Native
- React 18.x
- Redux Toolkit
- React Navigation
- TypeScript
- Expo (if applicable)

## Prerequisites
- Node.js 18+
- npm or yarn
- React Native development environment setup
- Android Studio (for Android development)
- Xcode (for iOS development on macOS)

## Getting Started

### Installation
1. Clone the repository
2. Navigate to the app directory:
   ```bash
   cd staff-mobile-app
   ```

3. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   ```

4. Install iOS dependencies (macOS only):
   ```bash
   cd ios && pod install
   ```

### Running the App

#### Android
```bash
npm run android
# or
yarn android
```

#### iOS (macOS only)
```bash
npm run ios
# or
yarn ios
```

#### Metro Bundler
Start the Metro bundler separately:
```bash
npm start
# or
yarn start
```

## Development

### Code Style
```bash
npm run lint
```

### Testing
```bash
npm test
```

### Building for Production

#### Android
```bash
npm run build:android
```

The APK will be generated in `android/app/build/outputs/apk/release/`

#### iOS
Build through Xcode or use:
```bash
cd ios && xcodebuild -workspace [WorkspaceName].xcworkspace -scheme [SchemeName] -configuration Release
```

## Project Structure
```
src/
├── components/     # Reusable components
├── screens/        # Screen components
├── navigation/     # Navigation configuration
├── store/          # Redux store and slices
├── services/       # API services
├── utils/          # Utility functions
└── types/          # TypeScript type definitions
```

## Contributing
Please read the main project's contributing guidelines before submitting pull requests.

## License
This project is part of the Warehousing Domain Ecosystem and follows the same license terms.
