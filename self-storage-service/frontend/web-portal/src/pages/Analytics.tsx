import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  ChartBarIcon,
  CubeIcon,
  TruckIcon,
  CurrencyDollarIcon,
  ArrowUpIcon,
  ArrowDownIcon
} from '@heroicons/react/24/outline';

interface AnalyticsData {
  totalRevenue: number;
  totalOrders: number;
  totalInventoryItems: number;
  averageOrderValue: number;
  revenueGrowth: number;
  ordersGrowth: number;
  inventoryTurnover: number;
  topProducts: ProductMetric[];
  revenueByMonth: MonthlyMetric[];
  ordersByStatus: StatusMetric[];
}

interface ProductMetric {
  productName: string;
  sku: string;
  totalSales: number;
  quantity: number;
}

interface MonthlyMetric {
  month: string;
  revenue: number;
  orders: number;
}

interface StatusMetric {
  status: string;
  count: number;
  percentage: number;
}

const Analytics: React.FC = () => {
  const [dateRange, setDateRange] = useState('30d');

  const { data: analytics, isLoading } = useQuery({
    queryKey: ['analytics', dateRange],
    queryFn: async () => {
      const response = await fetch(`/api/analytics?range=${dateRange}`);
      if (!response.ok) throw new Error('Failed to fetch analytics');
      return response.json();
    }
  });

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const formatPercentage = (value: number) => {
    const isPositive = value >= 0;
    return (
      <span className={`inline-flex items-center text-sm ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
        {isPositive ? (
          <ArrowUpIcon className="h-4 w-4 mr-1" />
        ) : (
          <ArrowDownIcon className="h-4 w-4 mr-1" />
        )}
        {Math.abs(value).toFixed(1)}%
      </span>
    );
  };

  const StatCard = ({ title, value, icon: Icon, growth }: {
    title: string;
    value: string | number;
    icon: React.ComponentType<any>;
    growth?: number;
  }) => (
    <div className="bg-white overflow-hidden shadow rounded-lg">
      <div className="p-5">
        <div className="flex items-center">
          <div className="flex-shrink-0">
            <Icon className="h-6 w-6 text-gray-400" />
          </div>
          <div className="ml-5 w-0 flex-1">
            <dl>
              <dt className="text-sm font-medium text-gray-500 truncate">{title}</dt>
              <dd className="flex items-baseline">
                <div className="text-2xl font-semibold text-gray-900">{value}</div>
                {growth !== undefined && (
                  <div className="ml-2 flex items-baseline text-sm font-semibold">
                    {formatPercentage(growth)}
                  </div>
                )}
              </dd>
            </dl>
          </div>
        </div>
      </div>
    </div>
  );

  if (isLoading) {
    return (
      <div className="p-6">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded w-48 mb-6"></div>
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {Array(4).fill(0).map((_, i) => (
              <div key={i} className="bg-white overflow-hidden shadow rounded-lg p-5">
                <div className="h-6 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div className="h-8 bg-gray-200 rounded w-1/2"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="p-6">
      <div className="sm:flex sm:items-center">
        <div className="sm:flex-auto">
          <h1 className="text-2xl font-semibold text-gray-900">Analytics Dashboard</h1>
          <p className="mt-2 text-sm text-gray-700">
            Track your self-storage business performance and key metrics
          </p>
        </div>
        <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <select
            value={dateRange}
            onChange={(e) => setDateRange(e.target.value)}
            className="block w-full pl-3 pr-10 py-2 text-base border border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 rounded-md"
          >
            <option value="7d">Last 7 days</option>
            <option value="30d">Last 30 days</option>
            <option value="90d">Last 90 days</option>
            <option value="1y">Last year</option>
          </select>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="mt-6 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total Revenue"
          value={formatCurrency(analytics?.totalRevenue || 0)}
          icon={CurrencyDollarIcon}
          growth={analytics?.revenueGrowth}
        />
        <StatCard
          title="Total Orders"
          value={analytics?.totalOrders?.toLocaleString() || '0'}
          icon={TruckIcon}
          growth={analytics?.ordersGrowth}
        />
        <StatCard
          title="Inventory Items"
          value={analytics?.totalInventoryItems?.toLocaleString() || '0'}
          icon={CubeIcon}
        />
        <StatCard
          title="Avg Order Value"
          value={formatCurrency(analytics?.averageOrderValue || 0)}
          icon={ChartBarIcon}
        />
      </div>

      {/* Charts Section */}
      <div className="mt-8 grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Revenue Trend */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Revenue Trend</h3>
          <div className="h-64 flex items-center justify-center border-2 border-dashed border-gray-300 rounded-lg">
            <div className="text-center">
              <ChartBarIcon className="mx-auto h-12 w-12 text-gray-400" />
              <p className="mt-2 text-sm text-gray-500">Revenue chart visualization</p>
              <p className="text-xs text-gray-400">Chart integration pending</p>
            </div>
          </div>
        </div>

        {/* Order Status Distribution */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Order Status Distribution</h3>
          <div className="space-y-3">
            {analytics?.ordersByStatus?.map((status: StatusMetric) => (
              <div key={status.status} className="flex items-center justify-between">
                <div className="flex items-center">
                  <div className="w-3 h-3 rounded-full bg-indigo-500 mr-3"></div>
                  <span className="text-sm font-medium text-gray-900">{status.status}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <span className="text-sm text-gray-600">{status.count}</span>
                  <span className="text-sm text-gray-500">({status.percentage.toFixed(1)}%)</span>
                </div>
              </div>
            )) || (
              <div className="text-center py-8 text-gray-500">
                No order data available
              </div>
            )}
          </div>
        </div>

        {/* Top Products */}
        <div className="bg-white shadow rounded-lg p-6 lg:col-span-2">
          <h3 className="text-lg font-medium text-gray-900 mb-4">Top Performing Products</h3>
          <div className="overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Product
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    SKU
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Quantity Sold
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total Sales
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {analytics?.topProducts?.map((product: ProductMetric, index: number) => (
                  <tr key={product.sku} className={index % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {product.productName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {product.sku}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {product.quantity.toLocaleString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatCurrency(product.totalSales)}
                    </td>
                  </tr>
                )) || (
                  <tr>
                    <td colSpan={4} className="px-6 py-8 text-center text-gray-500">
                      No product data available
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Additional Metrics */}
      <div className="mt-8 bg-white shadow rounded-lg p-6">
        <h3 className="text-lg font-medium text-gray-900 mb-4">Key Performance Indicators</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-3">
          <div className="text-center">
            <div className="text-2xl font-bold text-indigo-600">
              {analytics?.inventoryTurnover?.toFixed(1) || '0.0'}x
            </div>
            <div className="text-sm text-gray-500">Inventory Turnover</div>
            <div className="text-xs text-gray-400 mt-1">
              How quickly inventory is sold and replaced
            </div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-600">
              {formatCurrency(analytics?.averageOrderValue || 0)}
            </div>
            <div className="text-sm text-gray-500">Average Order Value</div>
            <div className="text-xs text-gray-400 mt-1">
              Average revenue per order
            </div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-purple-600">
              {((analytics?.totalOrders || 0) / 30).toFixed(1)}
            </div>
            <div className="text-sm text-gray-500">Orders per Day</div>
            <div className="text-xs text-gray-400 mt-1">
              Average daily order volume
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Analytics;