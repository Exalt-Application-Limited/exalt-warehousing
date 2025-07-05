import React, { Suspense, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { Box, CircularProgress } from '@mui/material';
import MainLayout from './layouts/MainLayout';
import AuthLayout from './layouts/AuthLayout';
import { useAuth } from './hooks/useAuth';
import { loadUser } from './store/slices/authSlice';
import ErrorBoundary from './components/common/ErrorBoundary';
import ProtectedRoute from './components/common/ProtectedRoute';

// Lazy load pages for better performance
const HomePage = React.lazy(() => import('./pages/HomePage'));
const SearchPage = React.lazy(() => import('./pages/SearchPage'));
const FacilityDetailPage = React.lazy(() => import('./pages/FacilityDetailPage'));
const FacilityComparePage = React.lazy(() => import('./pages/FacilityComparePage'));
const BookingPage = React.lazy(() => import('./pages/BookingPage'));
const SizeCalculatorPage = React.lazy(() => import('./pages/SizeCalculatorPage'));
const DashboardPage = React.lazy(() => import('./pages/DashboardPage'));
const BookingsPage = React.lazy(() => import('./pages/BookingsPage'));
const BookingDetailPage = React.lazy(() => import('./pages/BookingDetailPage'));
const ProfilePage = React.lazy(() => import('./pages/ProfilePage'));
const PaymentPage = React.lazy(() => import('./pages/PaymentPage'));
const SupportPage = React.lazy(() => import('./pages/SupportPage'));
const LoginPage = React.lazy(() => import('./pages/auth/LoginPage'));
const RegisterPage = React.lazy(() => import('./pages/auth/RegisterPage'));
const ForgotPasswordPage = React.lazy(() => import('./pages/auth/ForgotPasswordPage'));
const ResetPasswordPage = React.lazy(() => import('./pages/auth/ResetPasswordPage'));
const NotFoundPage = React.lazy(() => import('./pages/NotFoundPage'));

const LoadingFallback = () => (
  <Box display="flex" justifyContent="center" alignItems="center" minHeight="80vh">
    <CircularProgress />
  </Box>
);

function App() {
  const dispatch = useDispatch();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    // Load user data if token exists
    const token = localStorage.getItem(process.env.REACT_APP_TOKEN_STORAGE_KEY || 'exalt_warehouse_token');
    if (token) {
      dispatch(loadUser() as any);
    }
  }, [dispatch]);

  return (
    <ErrorBoundary>
      <Suspense fallback={<LoadingFallback />}>
        <Routes>
          {/* Public routes with MainLayout */}
          <Route path="/" element={<MainLayout />}>
            <Route index element={<HomePage />} />
            <Route path="search" element={<SearchPage />} />
            <Route path="facility/:facilityId" element={<FacilityDetailPage />} />
            <Route path="compare" element={<FacilityComparePage />} />
            <Route path="calculator" element={<SizeCalculatorPage />} />
            <Route path="support" element={<SupportPage />} />
            
            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="booking/:facilityId" element={<BookingPage />} />
              <Route path="dashboard" element={<DashboardPage />} />
              <Route path="dashboard/bookings" element={<BookingsPage />} />
              <Route path="dashboard/bookings/:bookingId" element={<BookingDetailPage />} />
              <Route path="dashboard/profile" element={<ProfilePage />} />
              <Route path="dashboard/payment" element={<PaymentPage />} />
            </Route>
          </Route>

          {/* Auth routes with AuthLayout */}
          <Route path="/auth" element={<AuthLayout />}>
            <Route path="login" element={<LoginPage />} />
            <Route path="register" element={<RegisterPage />} />
            <Route path="forgot-password" element={<ForgotPasswordPage />} />
            <Route path="reset-password/:token" element={<ResetPasswordPage />} />
          </Route>

          {/* Redirects */}
          <Route path="/login" element={<Navigate to="/auth/login" replace />} />
          <Route path="/register" element={<Navigate to="/auth/register" replace />} />

          {/* 404 Page */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Suspense>
    </ErrorBoundary>
  );
}

export default App;