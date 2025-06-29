import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip
} from '@mui/material';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer
} from 'recharts';
import {
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Assessment as AssessmentIcon,
  Speed as SpeedIcon,
  LocalShipping as ShippingIcon,
  Inventory as InventoryIcon
} from '@mui/icons-material';
import { useQuery } from 'react-query';

// Mock analytics service - replace with actual API calls
const analyticsService = {
  getDashboardData: async (warehouseId = 'all', period = '30d') => {
    // Simulate API call
    return {
      kpis: {
        totalOrders: 2847,
        orderTrend: 12.5,
        avgProcessingTime: 4.2,
        processingTrend: -8.3,
        fulfillmentRate: 98.5,
        fulfillmentTrend: 2.1,
        inventoryTurnover: 6.8,
        turnoverTrend: 15.2
      },
      orderVolume: [
        { date: '2024-01-01', orders: 180, fulfilled: 175 },
        { date: '2024-01-02', orders: 220, fulfilled: 218 },
        { date: '2024-01-03', orders: 195, fulfilled: 192 },
        { date: '2024-01-04', orders: 240, fulfilled: 235 },
        { date: '2024-01-05', orders: 210, fulfilled: 208 },
        { date: '2024-01-06', orders: 185, fulfilled: 180 },
        { date: '2024-01-07', orders: 160, fulfilled: 158 }
      ],
      warehousePerformance: [
        { name: 'Main Distribution', efficiency: 95, accuracy: 99.2, speed: 4.1 },
        { name: 'East Coast', efficiency: 92, accuracy: 98.8, speed: 4.5 },
        { name: 'West Coast', efficiency: 88, accuracy: 97.9, speed: 5.2 },
        { name: 'Central Hub', efficiency: 94, accuracy: 99.1, speed: 4.3 }
      ],
      inventoryStatus: [
        { name: 'Available', value: 65, color: '#4caf50' },
        { name: 'Reserved', value: 20, color: '#ff9800' },
        { name: 'Low Stock', value: 10, color: '#f44336' },
        { name: 'Out of Stock', value: 5, color: '#9e9e9e' }
      ],
      topPerformers: [
        { name: 'John Smith', role: 'Picker', performance: 98.5, orders: 145 },
        { name: 'Sarah Johnson', role: 'Packer', performance: 97.2, orders: 128 },
        { name: 'Mike Davis', role: 'Picker', performance: 96.8, orders: 132 },
        { name: 'Lisa Brown', role: 'Supervisor', performance: 95.9, orders: 89 }
      ]
    };
  }
};

const Analytics = () => {
  const [selectedWarehouse, setSelectedWarehouse] = useState('all');
  const [selectedPeriod, setSelectedPeriod] = useState('30d');

  const { data: analyticsData, isLoading } = useQuery(
    ['analytics', selectedWarehouse, selectedPeriod],
    () => analyticsService.getDashboardData(selectedWarehouse, selectedPeriod)
  );

  const warehouses = [
    { id: 'all', name: 'All Warehouses' },
    { id: 'wh-001', name: 'Main Distribution Center' },
    { id: 'wh-002', name: 'East Coast Fulfillment' },
    { id: 'wh-003', name: 'West Coast Hub' },
    { id: 'wh-004', name: 'Central Distribution' }
  ];

  const periods = [
    { value: '7d', label: 'Last 7 Days' },
    { value: '30d', label: 'Last 30 Days' },
    { value: '90d', label: 'Last 90 Days' },
    { value: '1y', label: 'Last Year' }
  ];

  const formatTrend = (value) => {
    const isPositive = value > 0;
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', color: isPositive ? 'success.main' : 'error.main' }}>
        {isPositive ? <TrendingUpIcon fontSize="small" /> : <TrendingDownIcon fontSize="small" />}
        <Typography variant="body2" sx={{ ml: 0.5 }}>
          {Math.abs(value)}%
        </Typography>
      </Box>
    );
  };

  if (isLoading) {
    return <Typography>Loading analytics...</Typography>;
  }

  const { kpis, orderVolume, warehousePerformance, inventoryStatus, topPerformers } = analyticsData;

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Warehouse Analytics
        </Typography>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <FormControl size="small" sx={{ minWidth: 200 }}>
            <InputLabel>Warehouse</InputLabel>
            <Select
              value={selectedWarehouse}
              label="Warehouse"
              onChange={(e) => setSelectedWarehouse(e.target.value)}
            >
              {warehouses.map((wh) => (
                <MenuItem key={wh.id} value={wh.id}>
                  {wh.name}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          <FormControl size="small" sx={{ minWidth: 150 }}>
            <InputLabel>Period</InputLabel>
            <Select
              value={selectedPeriod}
              label="Period"
              onChange={(e) => setSelectedPeriod(e.target.value)}
            >
              {periods.map((period) => (
                <MenuItem key={period.value} value={period.value}>
                  {period.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      </Box>

      {/* KPI Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Orders
                  </Typography>
                  <Typography variant="h5">
                    {kpis.totalOrders.toLocaleString()}
                  </Typography>
                  {formatTrend(kpis.orderTrend)}
                </Box>
                <AssessmentIcon sx={{ fontSize: 40, color: 'primary.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Avg Processing Time
                  </Typography>
                  <Typography variant="h5">
                    {kpis.avgProcessingTime}h
                  </Typography>
                  {formatTrend(kpis.processingTrend)}
                </Box>
                <SpeedIcon sx={{ fontSize: 40, color: 'info.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Fulfillment Rate
                  </Typography>
                  <Typography variant="h5">
                    {kpis.fulfillmentRate}%
                  </Typography>
                  {formatTrend(kpis.fulfillmentTrend)}
                </Box>
                <ShippingIcon sx={{ fontSize: 40, color: 'success.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Inventory Turnover
                  </Typography>
                  <Typography variant="h5">
                    {kpis.inventoryTurnover}
                  </Typography>
                  {formatTrend(kpis.turnoverTrend)}
                </Box>
                <InventoryIcon sx={{ fontSize: 40, color: 'warning.main' }} />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts Row */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Volume Trend
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={orderVolume}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="date" />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="orders" stroke="#1976d2" strokeWidth={2} />
                  <Line type="monotone" dataKey="fulfilled" stroke="#4caf50" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Inventory Distribution
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={inventoryStatus}
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                    label={({ name, value }) => `${name}: ${value}%`}
                  >
                    {inventoryStatus.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Performance Tables */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Warehouse Performance
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Warehouse</TableCell>
                      <TableCell>Efficiency</TableCell>
                      <TableCell>Accuracy</TableCell>
                      <TableCell>Speed (hrs)</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {warehousePerformance.map((warehouse, index) => (
                      <TableRow key={index}>
                        <TableCell>{warehouse.name}</TableCell>
                        <TableCell>
                          <Chip
                            label={`${warehouse.efficiency}%`}
                            color={warehouse.efficiency >= 90 ? 'success' : 'warning'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={`${warehouse.accuracy}%`}
                            color={warehouse.accuracy >= 99 ? 'success' : 'warning'}
                            size="small"
                          />
                        </TableCell>
                        <TableCell>{warehouse.speed}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Top Performers
              </Typography>
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Name</TableCell>
                      <TableCell>Role</TableCell>
                      <TableCell>Performance</TableCell>
                      <TableCell>Orders</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {topPerformers.map((performer, index) => (
                      <TableRow key={index}>
                        <TableCell>{performer.name}</TableCell>
                        <TableCell>{performer.role}</TableCell>
                        <TableCell>
                          <Chip
                            label={`${performer.performance}%`}
                            color="success"
                            size="small"
                          />
                        </TableCell>
                        <TableCell>{performer.orders}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
};

export default Analytics;