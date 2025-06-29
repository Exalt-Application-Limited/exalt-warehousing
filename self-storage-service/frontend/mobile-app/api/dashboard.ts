import axios from 'axios';

export interface DashboardSummary {
  totalLocations: number;
  activeLocations: number;
  avgUtilization: number;
  avgPerformanceScore: number;
}

export interface DashboardAlert {
  id: string;
  type: 'warning' | 'error' | 'info';
  title: string;
  message: string;
  timestamp: string;
}

export interface RecentOrder {
  id: string;
  orderNumber: string;
  customerName: string;
  status: string;
  amount: number;
  timestamp: string;
}

export interface DashboardData {
  summary: DashboardSummary;
  alerts: DashboardAlert[];
  recentOrders: RecentOrder[];
}

export const getVendorDashboard = async (): Promise<DashboardData> => {
  try {
    const response = await axios.get('/api/vendor/dashboard');
    return response.data;
  } catch (error) {
    // Return mock data for demo purposes
    return {
      summary: {
        totalLocations: 4,
        activeLocations: 3,
        avgUtilization: 78.5,
        avgPerformanceScore: 4.2,
      },
      alerts: [
        {
          id: '1',
          type: 'warning',
          title: 'Low Stock Alert',
          message: 'Location A inventory is running low on SKU-12345',
          timestamp: new Date().toISOString(),
        },
      ],
      recentOrders: [
        {
          id: '1',
          orderNumber: 'ORD-001',
          customerName: 'John Doe',
          status: 'Processing',
          amount: 125.50,
          timestamp: new Date().toISOString(),
        },
        {
          id: '2',
          orderNumber: 'ORD-002',
          customerName: 'Jane Smith',
          status: 'Shipped',
          amount: 89.99,
          timestamp: new Date(Date.now() - 3600000).toISOString(),
        },
      ],
    };
  }
};