import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { SnackbarProvider } from 'notistack';
import { Provider } from 'react-redux';

import { store } from './store';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import Layout from './components/layout/Layout';

// Pages
import Login from './pages/auth/Login';
import Dashboard from './pages/dashboard/Dashboard';
import WarehouseManagement from './pages/warehouses/WarehouseManagement';
import InventoryOverview from './pages/inventory/InventoryOverview';
import StaffManagement from './pages/staff/StaffManagement';
import Analytics from './pages/analytics/Analytics';
import Settings from './pages/settings/Settings';

const theme = createTheme({
  palette: {
    primary: {
      main: '#2E7D32',
    },
    secondary: {
      main: '#FF9800',
    },
    background: {
      default: '#f8f9fa',
    },
  },
  typography: {
    h4: {
      fontWeight: 600,
    },
    h5: {
      fontWeight: 600,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          textTransform: 'none',
          fontWeight: 600,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});

function App() {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <SnackbarProvider maxSnack={3}>
          <AuthProvider>
            <Router>
              <Box sx={{ display: 'flex' }}>
                <Routes>
                  {/* Public Routes */}
                  <Route path="/login" element={<Login />} />
                  
                  {/* Protected Routes */}
                  <Route path="/" element={
                    <ProtectedRoute>
                      <Layout>
                        <Dashboard />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/dashboard" element={
                    <ProtectedRoute>
                      <Layout>
                        <Dashboard />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/warehouses" element={
                    <ProtectedRoute>
                      <Layout>
                        <WarehouseManagement />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/inventory" element={
                    <ProtectedRoute>
                      <Layout>
                        <InventoryOverview />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/staff" element={
                    <ProtectedRoute>
                      <Layout>
                        <StaffManagement />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/analytics" element={
                    <ProtectedRoute>
                      <Layout>
                        <Analytics />
                      </Layout>
                    </ProtectedRoute>
                  } />
                  
                  <Route path="/settings" element={
                    <ProtectedRoute>
                      <Layout>
                        <Settings />
                      </Layout>
                    </ProtectedRoute>
                  } />
                </Routes>
              </Box>
            </Router>
          </AuthProvider>
        </SnackbarProvider>
      </ThemeProvider>
    </Provider>
  );
}

export default App;