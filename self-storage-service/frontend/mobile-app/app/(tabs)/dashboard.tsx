import React, { useState } from 'react';
import {
  StyleSheet,
  Text,
  View,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
  Alert,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useQuery } from '@tanstack/react-query';
import { StatusBar } from 'expo-status-bar';

// Components
import LoadingSpinner from '../../components/ui/LoadingSpinner';
import ErrorView from '../../components/ui/ErrorView';
import StatCard from '../../components/dashboard/StatCard';
import RecentOrdersList from '../../components/dashboard/RecentOrdersList';
import QuickActionsPanel from '../../components/dashboard/QuickActionsPanel';
import AlertsPanel from '../../components/dashboard/AlertsPanel';

// API
import { getVendorDashboard } from '../../api/dashboard';

// Store
import { useAuthStore } from '../../store/authStore';

// Types
import type { FC } from 'react';

const DashboardScreen: FC = () => {
  const [refreshing, setRefreshing] = useState(false);
  const { user } = useAuthStore();

  const {
    data: dashboardData,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['dashboard'],
    queryFn: getVendorDashboard,
    refetchInterval: 30000, // Refetch every 30 seconds
  });

  const onRefresh = async () => {
    setRefreshing(true);
    try {
      await refetch();
    } catch (error) {
      Alert.alert('Error', 'Failed to refresh dashboard data');
    } finally {
      setRefreshing(false);
    }
  };

  if (isLoading && !dashboardData) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.loadingContainer}>
          <LoadingSpinner size="large" />
          <Text style={styles.loadingText}>Loading dashboard...</Text>
        </View>
      </SafeAreaView>
    );
  }

  if (error && !dashboardData) {
    return (
      <SafeAreaView style={styles.container}>
        <ErrorView
          title="Failed to load dashboard"
          message="Unable to fetch dashboard data. Please try again."
          onRetry={refetch}
        />
      </SafeAreaView>
    );
  }

  const { summary, recentOrders, alerts } = dashboardData || {};

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      
      <ScrollView
        style={styles.scrollView}
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
        showsVerticalScrollIndicator={false}
      >
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.greeting}>
            Good {getTimeOfDay()}, {user?.name || 'Vendor'}!
          </Text>
          <Text style={styles.subtitle}>
            Here's your storage overview
          </Text>
        </View>

        {/* Statistics Cards */}
        {summary && (
          <View style={styles.statsContainer}>
            <View style={styles.statsRow}>
              <StatCard
                title="Total Locations"
                value={summary.totalLocations.toString()}
                icon="🏢"
                color="#2563eb"
                style={styles.statCard}
              />
              <StatCard
                title="Active"
                value={summary.activeLocations.toString()}
                icon="✅"
                color="#10b981"
                style={styles.statCard}
              />
            </View>
            <View style={styles.statsRow}>
              <StatCard
                title="Utilization"
                value={`${summary.avgUtilization.toFixed(1)}%`}
                icon="📊"
                color="#f59e0b"
                style={styles.statCard}
              />
              <StatCard
                title="Performance"
                value={summary.avgPerformanceScore.toFixed(1)}
                icon="⭐"
                color="#8b5cf6"
                style={styles.statCard}
              />
            </View>
          </View>
        )}

        {/* Alerts */}
        {alerts && alerts.length > 0 && (
          <AlertsPanel alerts={alerts} style={styles.section} />
        )}

        {/* Quick Actions */}
        <QuickActionsPanel style={styles.section} />

        {/* Recent Orders */}
        {recentOrders && recentOrders.length > 0 && (
          <View style={styles.section}>
            <View style={styles.sectionHeader}>
              <Text style={styles.sectionTitle}>Recent Orders</Text>
              <TouchableOpacity>
                <Text style={styles.sectionLink}>View All</Text>
              </TouchableOpacity>
            </View>
            <RecentOrdersList orders={recentOrders} />
          </View>
        )}

        {/* Performance Summary */}
        <View style={styles.section}>
          <Text style={styles.sectionTitle}>Today's Performance</Text>
          <View style={styles.performanceGrid}>
            <View style={styles.performanceItem}>
              <Text style={styles.performanceValue}>12</Text>
              <Text style={styles.performanceLabel}>Orders Processed</Text>
            </View>
            <View style={styles.performanceItem}>
              <Text style={styles.performanceValue}>18.5h</Text>
              <Text style={styles.performanceLabel}>Avg Processing</Text>
            </View>
            <View style={styles.performanceItem}>
              <Text style={styles.performanceValue}>96.2%</Text>
              <Text style={styles.performanceLabel}>Success Rate</Text>
            </View>
          </View>
        </View>

        {/* Bottom spacing */}
        <View style={styles.bottomSpacing} />
      </ScrollView>
    </SafeAreaView>
  );
};

const getTimeOfDay = (): string => {
  const hour = new Date().getHours();
  if (hour < 12) return 'morning';
  if (hour < 17) return 'afternoon';
  return 'evening';
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fafb',
  },
  scrollView: {
    flex: 1,
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    marginTop: 16,
    fontSize: 16,
    color: '#6b7280',
  },
  header: {
    padding: 20,
    paddingBottom: 16,
  },
  greeting: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#1f2937',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 16,
    color: '#6b7280',
  },
  statsContainer: {
    paddingHorizontal: 20,
    marginBottom: 20,
  },
  statsRow: {
    flexDirection: 'row',
    marginBottom: 12,
  },
  statCard: {
    flex: 1,
    marginHorizontal: 6,
  },
  section: {
    marginBottom: 24,
  },
  sectionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    marginBottom: 12,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#1f2937',
  },
  sectionLink: {
    fontSize: 14,
    color: '#2563eb',
    fontWeight: '500',
  },
  performanceGrid: {
    flexDirection: 'row',
    paddingHorizontal: 20,
  },
  performanceItem: {
    flex: 1,
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    marginHorizontal: 4,
    shadowColor: '#000000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  performanceValue: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#1f2937',
    marginBottom: 4,
  },
  performanceLabel: {
    fontSize: 12,
    color: '#6b7280',
    textAlign: 'center',
  },
  bottomSpacing: {
    height: 20,
  },
});

export default DashboardScreen;