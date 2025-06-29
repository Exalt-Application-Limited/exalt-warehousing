import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Fab
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Visibility as ViewIcon,
  LocationOn as LocationIcon,
  Business as BusinessIcon,
  Assessment as AssessmentIcon
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from 'react-query';

// Mock API service - replace with actual API calls
const warehouseService = {
  getWarehouses: async () => {
    // Simulate API call
    return [
      {
        id: '1',
        code: 'WH-001',
        name: 'Main Distribution Center',
        address: '123 Warehouse Blvd',
        city: 'Seattle',
        state: 'WA',
        country: 'USA',
        type: 'DISTRIBUTION_CENTER',
        status: 'ACTIVE',
        totalCapacity: 50000,
        availableCapacity: 25000,
        isActive: true
      },
      {
        id: '2',
        code: 'WH-002',
        name: 'East Coast Fulfillment',
        address: '456 Logistics Lane',
        city: 'Atlanta',
        state: 'GA',
        country: 'USA',
        type: 'FULFILLMENT_CENTER',
        status: 'ACTIVE',
        totalCapacity: 30000,
        availableCapacity: 15000,
        isActive: true
      }
    ];
  },
  createWarehouse: async (warehouse) => {
    // Simulate API call
    return { ...warehouse, id: Date.now().toString() };
  },
  updateWarehouse: async (id, warehouse) => {
    // Simulate API call
    return { ...warehouse, id };
  },
  deleteWarehouse: async (id) => {
    // Simulate API call
    return { success: true };
  }
};

const warehouseTypes = [
  'DISTRIBUTION_CENTER',
  'FULFILLMENT_CENTER',
  'REGIONAL_HUB',
  'CROSS_DOCK',
  'COLD_STORAGE',
  'VENDOR_MANAGED',
  'RETURNS_CENTER',
  'TEMPORARY',
  'MICRO_FULFILLMENT'
];

const warehouseStatuses = [
  'ACTIVE',
  'INACTIVE',
  'UNDER_CONSTRUCTION',
  'MAINTENANCE',
  'DECOMMISSIONED'
];

const Warehouses = () => {
  const [open, setOpen] = useState(false);
  const [editingWarehouse, setEditingWarehouse] = useState(null);
  const [formData, setFormData] = useState({
    code: '',
    name: '',
    address: '',
    city: '',
    state: '',
    country: 'USA',
    type: 'DISTRIBUTION_CENTER',
    status: 'ACTIVE',
    totalCapacity: '',
    availableCapacity: '',
    isActive: true
  });

  const queryClient = useQueryClient();

  const { data: warehouses = [], isLoading } = useQuery(
    'warehouses',
    warehouseService.getWarehouses
  );

  const createMutation = useMutation(warehouseService.createWarehouse, {
    onSuccess: () => {
      queryClient.invalidateQueries('warehouses');
      handleClose();
    }
  });

  const updateMutation = useMutation(
    ({ id, data }) => warehouseService.updateWarehouse(id, data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('warehouses');
        handleClose();
      }
    }
  );

  const deleteMutation = useMutation(warehouseService.deleteWarehouse, {
    onSuccess: () => {
      queryClient.invalidateQueries('warehouses');
    }
  });

  const handleOpen = (warehouse = null) => {
    if (warehouse) {
      setEditingWarehouse(warehouse);
      setFormData(warehouse);
    } else {
      setEditingWarehouse(null);
      setFormData({
        code: '',
        name: '',
        address: '',
        city: '',
        state: '',
        country: 'USA',
        type: 'DISTRIBUTION_CENTER',
        status: 'ACTIVE',
        totalCapacity: '',
        availableCapacity: '',
        isActive: true
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditingWarehouse(null);
  };

  const handleSubmit = () => {
    if (editingWarehouse) {
      updateMutation.mutate({ id: editingWarehouse.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this warehouse?')) {
      deleteMutation.mutate(id);
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      ACTIVE: 'success',
      INACTIVE: 'default',
      UNDER_CONSTRUCTION: 'warning',
      MAINTENANCE: 'info',
      DECOMMISSIONED: 'error'
    };
    return colors[status] || 'default';
  };

  const getTypeColor = (type) => {
    const colors = {
      DISTRIBUTION_CENTER: 'primary',
      FULFILLMENT_CENTER: 'secondary',
      REGIONAL_HUB: 'info',
      COLD_STORAGE: 'warning',
      VENDOR_MANAGED: 'default'
    };
    return colors[type] || 'default';
  };

  if (isLoading) {
    return <Typography>Loading warehouses...</Typography>;
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Warehouse Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Add Warehouse
        </Button>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <BusinessIcon sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Warehouses
                  </Typography>
                  <Typography variant="h5">
                    {warehouses.length}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <AssessmentIcon sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Warehouses
                  </Typography>
                  <Typography variant="h5">
                    {warehouses.filter(w => w.status === 'ACTIVE').length}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <LocationIcon sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Capacity
                  </Typography>
                  <Typography variant="h5">
                    {warehouses.reduce((sum, w) => sum + w.totalCapacity, 0).toLocaleString()}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <AssessmentIcon sx={{ fontSize: 40, color: 'warning.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Utilization
                  </Typography>
                  <Typography variant="h5">
                    {Math.round(
                      ((warehouses.reduce((sum, w) => sum + w.totalCapacity, 0) - 
                        warehouses.reduce((sum, w) => sum + w.availableCapacity, 0)) /
                        warehouses.reduce((sum, w) => sum + w.totalCapacity, 0)) * 100
                    )}%
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Warehouses Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Warehouse List
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Code</TableCell>
                  <TableCell>Name</TableCell>
                  <TableCell>Location</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Capacity</TableCell>
                  <TableCell>Utilization</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {warehouses.map((warehouse) => (
                  <TableRow key={warehouse.id}>
                    <TableCell>
                      <Typography variant="subtitle2">
                        {warehouse.code}
                      </Typography>
                    </TableCell>
                    <TableCell>{warehouse.name}</TableCell>
                    <TableCell>
                      {warehouse.city}, {warehouse.state}
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={warehouse.type.replace('_', ' ')}
                        color={getTypeColor(warehouse.type)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={warehouse.status}
                        color={getStatusColor(warehouse.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {warehouse.totalCapacity.toLocaleString()}
                    </TableCell>
                    <TableCell>
                      {Math.round(((warehouse.totalCapacity - warehouse.availableCapacity) / warehouse.totalCapacity) * 100)}%
                    </TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        onClick={() => handleOpen(warehouse)}
                        color="primary"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(warehouse.id)}
                        color="error"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

      {/* Add/Edit Dialog */}
      <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
        <DialogTitle>
          {editingWarehouse ? 'Edit Warehouse' : 'Add New Warehouse'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Warehouse Code"
                value={formData.code}
                onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Warehouse Name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Address"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="City"
                value={formData.city}
                onChange={(e) => setFormData({ ...formData, city: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="State"
                value={formData.state}
                onChange={(e) => setFormData({ ...formData, state: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                label="Country"
                value={formData.country}
                onChange={(e) => setFormData({ ...formData, country: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Type"
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value })}
              >
                {warehouseTypes.map((type) => (
                  <MenuItem key={type} value={type}>
                    {type.replace('_', ' ')}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Status"
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
              >
                {warehouseStatuses.map((status) => (
                  <MenuItem key={status} value={status}>
                    {status}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Total Capacity"
                type="number"
                value={formData.totalCapacity}
                onChange={(e) => setFormData({ ...formData, totalCapacity: parseInt(e.target.value) || '' })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Available Capacity"
                type="number"
                value={formData.availableCapacity}
                onChange={(e) => setFormData({ ...formData, availableCapacity: parseInt(e.target.value) || '' })}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingWarehouse ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Warehouses;