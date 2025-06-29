// app.config.js - Expo configuration with updated port

export default {
  expo: {
    name: 'Vendor Storage',
    slug: 'vendor-storage-mobile',
    version: '1.0.0',
    orientation: 'portrait',
    icon: './assets/icon.png',
    userInterfaceStyle: 'automatic',
    splash: {
      image: './assets/splash.png',
      resizeMode: 'contain',
      backgroundColor: '#ffffff'
    },
    ios: {
      supportsTablet: true,
      bundleIdentifier: 'com.ecosystem.vendorstorage',
      buildNumber: '1.0.0'
    },
    android: {
      adaptiveIcon: {
        foregroundImage: './assets/adaptive-icon.png',
        backgroundColor: '#ffffff'
      },
      package: 'com.ecosystem.vendorstorage',
      versionCode: 1
    },
    extra: {
      // Updated to use port 8088
      apiBaseUrl: process.env.API_BASE_URL || 'http://localhost:8088/self-storage-service',
      wsUrl: process.env.WS_URL || 'ws://localhost:8088/self-storage-service',
      eas: {
        projectId: 'your-eas-project-id'
      }
    }
  }
};
