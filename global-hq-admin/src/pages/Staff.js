import React, { useState } from 'react';
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
  Avatar,
  Badge
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Person as PersonIcon,
  Assignment as AssignmentIcon,
  Schedule as ScheduleIcon,
  Star as StarIcon
} from '@mui/icons-material';
import { useQuery, useMutation, useQueryClient } from 'react-query';

// Mock staff service - replace with actual API calls
const staffService = {
  getStaff: async () => {
    // Simulate API call
    return [
      {
        id: '1',
        employeeId: 'EMP001',
        firstName: 'John',
        lastName: 'Smith',
        email: 'john.smith@company.com',
        phone: '+1-555-0123',
        role: 'PICKER',
        department: 'FULFILLMENT',
        warehouse: 'Main Distribution Center',
        status: 'ACTIVE',
        shiftPattern: 'DAY',
        performanceRating: 4.8,
        tasksCompleted: 1247,
        accuracy: 99.2,
        hireDate: '2023-01-15'
      },
      {
        id: '2',
        employeeId: 'EMP002',
        firstName: 'Sarah',
        lastName: 'Johnson',
        email: 'sarah.johnson@company.com',
        phone: '+1-555-0124',
        role: 'SUPERVISOR',
        department: 'OPERATIONS',
        warehouse: 'Main Distribution Center',
        status: 'ACTIVE',
        shiftPattern: 'DAY',
        performanceRating: 4.9,
        tasksCompleted: 892,
        accuracy: 98.7,
        hireDate: '2022-06-20'
      },
      {
        id: '3',
        employeeId: 'EMP003',
        firstName: 'Mike',
        lastName: 'Davis',
        email: 'mike.davis@company.com',
        phone: '+1-555-0125',
        role: 'PACKER',
        department: 'FULFILLMENT',
        warehouse: 'East Coast Fulfillment',
        status: 'ACTIVE',
        shiftPattern: 'NIGHT',
        performanceRating: 4.6,
        tasksCompleted: 1156,
        accuracy: 97.8,
        hireDate: '2023-03-10'
      }
    ];
  },
  createStaff: async (staff) => {
    // Simulate API call
    return { ...staff, id: Date.now().toString() };
  },
  updateStaff: async (id, staff) => {
    // Simulate API call
    return { ...staff, id };
  },
  deleteStaff: async (id) => {
    // Simulate API call
    return { success: true };
  }
};

const roles = [
  'PICKER',
  'PACKER',
  'SUPERVISOR',
  'MANAGER',
  'QUALITY_CONTROL',
  'MAINTENANCE',
  'SECURITY',
  'DRIVER'
];

const departments = [
  'FULFILLMENT',
  'OPERATIONS',
  'QUALITY_CONTROL',
  'MAINTENANCE',
  'SECURITY',
  'ADMINISTRATION'
];

const shiftPatterns = [
  'DAY',
  'NIGHT',
  'SWING',
  'WEEKEND'
];

const statuses = [
  'ACTIVE',
  'INACTIVE',
  'ON_LEAVE',
  'TERMINATED'
];

const Staff = () => {
  const [open, setOpen] = useState(false);
  const [editingStaff, setEditingStaff] = useState(null);
  const [formData, setFormData] = useState({
    employeeId: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: 'PICKER',
    department: 'FULFILLMENT',
    warehouse: '',
    status: 'ACTIVE',
    shiftPattern: 'DAY',
    hireDate: ''
  });

  const queryClient = useQueryClient();

  const { data: staff = [], isLoading } = useQuery(
    'staff',
    staffService.getStaff
  );

  const createMutation = useMutation(staffService.createStaff, {
    onSuccess: () => {
      queryClient.invalidateQueries('staff');
      handleClose();
    }
  });

  const updateMutation = useMutation(
    ({ id, data }) => staffService.updateStaff(id, data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('staff');
        handleClose();
      }
    }
  );

  const deleteMutation = useMutation(staffService.deleteStaff, {
    onSuccess: () => {
      queryClient.invalidateQueries('staff');
    }
  });

  const handleOpen = (staffMember = null) => {
    if (staffMember) {
      setEditingStaff(staffMember);
      setFormData(staffMember);
    } else {
      setEditingStaff(null);
      setFormData({
        employeeId: '',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        role: 'PICKER',
        department: 'FULFILLMENT',
        warehouse: '',
        status: 'ACTIVE',
        shiftPattern: 'DAY',
        hireDate: ''
      });
    }
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditingStaff(null);
  };

  const handleSubmit = () => {
    if (editingStaff) {
      updateMutation.mutate({ id: editingStaff.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const handleDelete = (id) => {
    if (window.confirm('Are you sure you want to delete this staff member?')) {
      deleteMutation.mutate(id);
    }
  };

  const getStatusColor = (status) => {
    const colors = {
      ACTIVE: 'success',
      INACTIVE: 'default',
      ON_LEAVE: 'warning',
      TERMINATED: 'error'
    };
    return colors[status] || 'default';
  };

  const getRoleColor = (role) => {
    const colors = {
      SUPERVISOR: 'primary',
      MANAGER: 'secondary',
      PICKER: 'info',
      PACKER: 'success',
      QUALITY_CONTROL: 'warning'
    };
    return colors[role] || 'default';
  };

  const renderPerformanceStars = (rating) => {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        {[1, 2, 3, 4, 5].map((star) => (
          <StarIcon
            key={star}
            sx={{
              color: star <= rating ? 'warning.main' : 'grey.300',
              fontSize: 16
            }}
          />
        ))}
        <Typography variant="body2" sx={{ ml: 1 }}>
          {rating}
        </Typography>
      </Box>
    );
  };

  if (isLoading) {
    return <Typography>Loading staff...</Typography>;
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Staff Management
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => handleOpen()}
        >
          Add Staff Member
        </Button>
      </Box>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <PersonIcon sx={{ fontSize: 40, color: 'primary.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Staff
                  </Typography>
                  <Typography variant="h5">
                    {staff.length}
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
                <Badge badgeContent={staff.filter(s => s.status === 'ACTIVE').length} color="success">
                  <PersonIcon sx={{ fontSize: 40, color: 'success.main', mr: 2 }} />
                </Badge>
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Active Staff
                  </Typography>
                  <Typography variant="h5">
                    {staff.filter(s => s.status === 'ACTIVE').length}
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
                <AssignmentIcon sx={{ fontSize: 40, color: 'info.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Tasks
                  </Typography>
                  <Typography variant="h5">
                    {staff.reduce((sum, s) => sum + s.tasksCompleted, 0).toLocaleString()}
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
                <StarIcon sx={{ fontSize: 40, color: 'warning.main', mr: 2 }} />
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Avg Performance
                  </Typography>
                  <Typography variant="h5">
                    {(staff.reduce((sum, s) => sum + s.performanceRating, 0) / staff.length).toFixed(1)}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Staff Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Staff Directory
          </Typography>
          <TableContainer component={Paper}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Employee</TableCell>
                  <TableCell>Role</TableCell>
                  <TableCell>Department</TableCell>
                  <TableCell>Warehouse</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Performance</TableCell>
                  <TableCell>Tasks</TableCell>
                  <TableCell>Accuracy</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {staff.map((member) => (
                  <TableRow key={member.id}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <Avatar sx={{ mr: 2, bgcolor: 'primary.main' }}>
                          {member.firstName[0]}{member.lastName[0]}
                        </Avatar>
                        <Box>
                          <Typography variant="subtitle2">
                            {member.firstName} {member.lastName}
                          </Typography>
                          <Typography variant="body2" color="textSecondary">
                            {member.employeeId}
                          </Typography>
                        </Box>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={member.role}
                        color={getRoleColor(member.role)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{member.department}</TableCell>
                    <TableCell>{member.warehouse}</TableCell>
                    <TableCell>
                      <Chip
                        label={member.status}
                        color={getStatusColor(member.status)}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      {renderPerformanceStars(member.performanceRating)}
                    </TableCell>
                    <TableCell>{member.tasksCompleted.toLocaleString()}</TableCell>
                    <TableCell>
                      <Chip
                        label={`${member.accuracy}%`}
                        color={member.accuracy >= 98 ? 'success' : 'warning'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        onClick={() => handleOpen(member)}
                        color="primary"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(member.id)}
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
          {editingStaff ? 'Edit Staff Member' : 'Add New Staff Member'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Employee ID"
                value={formData.employeeId}
                onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="First Name"
                value={formData.firstName}
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Last Name"
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Phone"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Hire Date"
                type="date"
                value={formData.hireDate}
                onChange={(e) => setFormData({ ...formData, hireDate: e.target.value })}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Role"
                value={formData.role}
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              >
                {roles.map((role) => (
                  <MenuItem key={role} value={role}>
                    {role}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Department"
                value={formData.department}
                onChange={(e) => setFormData({ ...formData, department: e.target.value })}
              >
                {departments.map((dept) => (
                  <MenuItem key={dept} value={dept}>
                    {dept}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Warehouse"
                value={formData.warehouse}
                onChange={(e) => setFormData({ ...formData, warehouse: e.target.value })}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Status"
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
              >
                {statuses.map((status) => (
                  <MenuItem key={status} value={status}>
                    {status}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                select
                label="Shift Pattern"
                value={formData.shiftPattern}
                onChange={(e) => setFormData({ ...formData, shiftPattern: e.target.value })}
              >
                {shiftPatterns.map((shift) => (
                  <MenuItem key={shift} value={shift}>
                    {shift}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleSubmit} variant="contained">
            {editingStaff ? 'Update' : 'Create'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Staff;