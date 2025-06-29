import React from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  BuildingOfficeIcon,
  CheckCircleIcon,
  ChartPieIcon,
  StarIcon,
  ExclamationTriangleIcon,
  TruckIcon
} from '@heroicons/react/24/outline';

// Components
import LoadingSpinner from '@components/ui/LoadingSpinner';
import ErrorAlert from '@components/ui/ErrorAlert';
import StatCard from '@components/dashboard/StatCard';
import RecentOrdersTable from '@components/dashboard/RecentOrdersTable';
import AlertsPanel from '@components/dashboard/AlertsPanel';
import PerformanceChart from '@components/dashboard/PerformanceChart';
import UtilizationChart from '@components/dashboard/UtilizationChart';

// API
import { getVendorDashboard } from '@api/dashboard';

// Types
import type { VendorDashboardData } from '@types/dashboard';

const Dashboard: React.FC = () => {
  const {
    data: dashboardData,
    isLoading,
    error,
    refetch
  } = useQuery({
    queryKey: ['dashboard'],
    queryFn: getVendorDashboard,
    refetchInterval: 30000, // Refetch every 30 seconds
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error) {
    return (
      <ErrorAlert
        title="Failed to load dashboard"
        message="Unable to fetch dashboard data. Please try again."
        onRetry={refetch}
      />
    );
  }

  if (!dashboardData) {
    return null;
  }

  const {
    summary,
    recentOrders,
    alerts,
    performanceMetrics,
    utilizationData
  } = dashboardData;

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="bg-white shadow rounded-lg p-6">
        <div className="md:flex md:items-center md:justify-between">
          <div className="flex-1 min-w-0">
            <h1 className="text-2xl font-bold leading-7 text-gray-900 sm:text-3xl sm:truncate">
              Storage Dashboard
            </h1>
            <p className="mt-1 text-sm text-gray-500">
              Welcome back! Here's what's happening with your storage locations.
            </p>
          </div>
          <div className="mt-4 flex md:mt-0 md:ml-4">
            <button
              type="button"
              className="inline-flex items-center px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              onClick={() => refetch()}
            >
              <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              Refresh
            </button>
          </div>
        </div>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total Locations"
          value={summary.totalLocations}
          icon={BuildingOfficeIcon}
          color="blue"
          trend={summary.locationsTrend}
        />
        <StatCard
          title="Active Locations"
          value={summary.activeLocations}
          icon={CheckCircleIcon}
          color="green"
          trend={summary.activeTrend}
        />
        <StatCard
          title="Avg Utilization"
          value={`${summary.avgUtilization.toFixed(1)}%`}
          icon={ChartPieIcon}
          color="yellow"
          trend={summary.utilizationTrend}
        />
        <StatCard
          title="Performance Score"
          value={summary.avgPerformanceScore.toFixed(1)}
          icon={StarIcon}
          color="purple"
          trend={summary.performanceTrend}
        />
      </div>

      {/* Alerts Section */}
      {alerts && alerts.length > 0 && (
        <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 rounded-lg">
          <div className="flex">
            <div className="flex-shrink-0">
              <ExclamationTriangleIcon className="h-5 w-5 text-yellow-400" />
            </div>
            <div className="ml-3">
              <p className="text-sm text-yellow-700">
                You have {alerts.length} location{alerts.length > 1 ? 's' : ''} requiring attention.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Orders */}
        <div className="lg:col-span-2">
          <div className="bg-white shadow rounded-lg">
            <div className="px-6 py-4 border-b border-gray-200">
              <div className="flex items-center justify-between">
                <h3 className="text-lg font-medium text-gray-900 flex items-center">
                  <TruckIcon className="h-5 w-5 mr-2" />
                  Recent Orders
                </h3>
                <a
                  href="/orders"
                  className="text-sm text-blue-600 hover:text-blue-500"
                >
                  View all
                </a>
              </div>
            </div>
            <RecentOrdersTable orders={recentOrders} />
          </div>
        </div>

        {/* Alerts Panel */}
        <div className="lg:col-span-1">
          <AlertsPanel alerts={alerts} />
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Performance Chart */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Performance Trends
          </h3>
          <PerformanceChart data={performanceMetrics} />
        </div>

        {/* Utilization Chart */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Storage Utilization
          </h3>
          <UtilizationChart data={utilizationData} />
        </div>
      </div>

      {/* Quick Actions */}
      <div className="bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <button className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
            <BuildingOfficeIcon className="h-5 w-5 mr-2" />
            Add Location
          </button>
          <button className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
            <TruckIcon className="h-5 w-5 mr-2" />
            Process Order
          </button>
          <button className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
            <ChartPieIcon className="h-5 w-5 mr-2" />
            View Analytics
          </button>
          <button className="flex items-center justify-center px-4 py-3 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
            <ExclamationTriangleIcon className="h-5 w-5 mr-2" />
            Review Alerts
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;