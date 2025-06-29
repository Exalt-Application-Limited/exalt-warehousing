import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  TextField,
  Switch,
  FormControlLabel,
  Divider,
  Alert,
  AlertTitle,
  Tab,
  Tabs,
  List,
  ListItem,
  ListItemText,
  ListItemSecondaryAction,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip
} from '@mui/material';
import {
  Save as SaveIcon,
  Refresh as RefreshIcon,
  Delete as DeleteIcon,
  Add as AddIcon,
  Notifications as NotificationsIcon,
  Security as SecurityIcon,
  Storage as StorageIcon,
  Settings as SettingsIcon,
  Api as ApiIcon
} from '@mui/icons-material';

const Settings = () => {
  const [activeTab, setActiveTab] = useState(0);
  const [showSaveAlert, setShowSaveAlert] = useState(false);
  const [apiKeyDialog, setApiKeyDialog] = useState(false);
  const [newApiKey, setNewApiKey] = useState({ name: '', permissions: [] });

  // General Settings State
  const [generalSettings, setGeneralSettings] = useState({
    companyName: 'Exalt Application Limited',
    timeZone: 'UTC',
    dateFormat: 'MM/DD/YYYY',
    currency: 'USD',
    language: 'English',
    autoBackup: true,
    maintenanceMode: false
  });

  // Notification Settings State
  const [notificationSettings, setNotificationSettings] = useState({
    emailNotifications: true,
    smsNotifications: false,
    pushNotifications: true,
    lowStockAlerts: true,
    orderAlerts: true,
    systemAlerts: true,
    performanceReports: true,
    weeklyDigest: true
  });

  // Security Settings State
  const [securitySettings, setSecuritySettings] = useState({
    twoFactorAuth: true,
    sessionTimeout: 30,
    passwordExpiry: 90,
    loginAttempts: 5,
    requireStrongPassword: true,
    auditLogging: true
  });

  // Integration Settings State
  const [integrationSettings, setIntegrationSettings] = useState({
    apiRateLimit: 1000,
    webhookRetries: 3,
    cacheTimeout: 300,
    enableCors: true,
    apiVersion: 'v1'
  });

  // API Keys State
  const [apiKeys, setApiKeys] = useState([
    {
      id: '1',
      name: 'Main Dashboard',
      key: 'ak_live_xxxxxxxxxxxx',
      permissions: ['read', 'write'],
      created: '2024-01-15',
      lastUsed: '2024-01-20'
    },
    {
      id: '2',
      name: 'Mobile App',
      key: 'ak_live_yyyyyyyyyyyy',
      permissions: ['read'],
      created: '2024-01-10',
      lastUsed: '2024-01-19'
    }
  ]);

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
  };

  const handleSave = () => {
    // Simulate save operation
    setShowSaveAlert(true);
    setTimeout(() => setShowSaveAlert(false), 3000);
  };

  const handleGeneralSettingChange = (field, value) => {
    setGeneralSettings(prev => ({ ...prev, [field]: value }));
  };

  const handleNotificationChange = (field, value) => {
    setNotificationSettings(prev => ({ ...prev, [field]: value }));
  };

  const handleSecurityChange = (field, value) => {
    setSecuritySettings(prev => ({ ...prev, [field]: value }));
  };

  const handleIntegrationChange = (field, value) => {
    setIntegrationSettings(prev => ({ ...prev, [field]: value }));
  };

  const handleCreateApiKey = () => {
    const newKey = {
      id: Date.now().toString(),
      ...newApiKey,
      key: `ak_live_${Math.random().toString(36).substring(2, 15)}`,
      created: new Date().toISOString().split('T')[0],
      lastUsed: 'Never'
    };
    setApiKeys(prev => [...prev, newKey]);
    setApiKeyDialog(false);
    setNewApiKey({ name: '', permissions: [] });
  };

  const handleDeleteApiKey = (id) => {
    if (window.confirm('Are you sure you want to delete this API key?')) {
      setApiKeys(prev => prev.filter(key => key.id !== id));
    }
  };

  const TabPanel = ({ children, value, index }) => (
    <div hidden={value !== index}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4" component="h1">
          System Settings
        </Typography>
        <Button
          variant="contained"
          startIcon={<SaveIcon />}
          onClick={handleSave}
        >
          Save Changes
        </Button>
      </Box>

      {showSaveAlert && (
        <Alert severity="success" sx={{ mb: 3 }}>
          <AlertTitle>Success</AlertTitle>
          Settings have been saved successfully!
        </Alert>
      )}

      <Card>
        <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tabs value={activeTab} onChange={handleTabChange} aria-label="settings tabs">
            <Tab label="General" icon={<SettingsIcon />} />
            <Tab label="Notifications" icon={<NotificationsIcon />} />
            <Tab label="Security" icon={<SecurityIcon />} />
            <Tab label="Integration" icon={<ApiIcon />} />
            <Tab label="Storage" icon={<StorageIcon />} />
          </Tabs>
        </Box>

        <TabPanel value={activeTab} index={0}>
          <Typography variant="h6" gutterBottom>
            General Settings
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Company Name"
                value={generalSettings.companyName}
                onChange={(e) => handleGeneralSettingChange('companyName', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Time Zone</InputLabel>
                <Select
                  value={generalSettings.timeZone}
                  label="Time Zone"
                  onChange={(e) => handleGeneralSettingChange('timeZone', e.target.value)}
                >
                  <MenuItem value="UTC">UTC</MenuItem>
                  <MenuItem value="America/New_York">Eastern Time</MenuItem>
                  <MenuItem value="America/Chicago">Central Time</MenuItem>
                  <MenuItem value="America/Denver">Mountain Time</MenuItem>
                  <MenuItem value="America/Los_Angeles">Pacific Time</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Date Format</InputLabel>
                <Select
                  value={generalSettings.dateFormat}
                  label="Date Format"
                  onChange={(e) => handleGeneralSettingChange('dateFormat', e.target.value)}
                >
                  <MenuItem value="MM/DD/YYYY">MM/DD/YYYY</MenuItem>
                  <MenuItem value="DD/MM/YYYY">DD/MM/YYYY</MenuItem>
                  <MenuItem value="YYYY-MM-DD">YYYY-MM-DD</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Currency</InputLabel>
                <Select
                  value={generalSettings.currency}
                  label="Currency"
                  onChange={(e) => handleGeneralSettingChange('currency', e.target.value)}
                >
                  <MenuItem value="USD">USD ($)</MenuItem>
                  <MenuItem value="EUR">EUR (€)</MenuItem>
                  <MenuItem value="GBP">GBP (£)</MenuItem>
                  <MenuItem value="CAD">CAD (C$)</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" gutterBottom>
                System Preferences
              </Typography>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={generalSettings.autoBackup}
                    onChange={(e) => handleGeneralSettingChange('autoBackup', e.target.checked)}
                  />
                }
                label="Enable Auto Backup"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={generalSettings.maintenanceMode}
                    onChange={(e) => handleGeneralSettingChange('maintenanceMode', e.target.checked)}
                  />
                }
                label="Maintenance Mode"
              />
            </Grid>
          </Grid>
        </TabPanel>

        <TabPanel value={activeTab} index={1}>
          <Typography variant="h6" gutterBottom>
            Notification Settings
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Typography variant="subtitle1" gutterBottom>
                Delivery Channels
              </Typography>
            </Grid>
            <Grid item xs={12} sm={4}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.emailNotifications}
                    onChange={(e) => handleNotificationChange('emailNotifications', e.target.checked)}
                  />
                }
                label="Email Notifications"
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.smsNotifications}
                    onChange={(e) => handleNotificationChange('smsNotifications', e.target.checked)}
                  />
                }
                label="SMS Notifications"
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.pushNotifications}
                    onChange={(e) => handleNotificationChange('pushNotifications', e.target.checked)}
                  />
                }
                label="Push Notifications"
              />
            </Grid>
            <Grid item xs={12}>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" gutterBottom>
                Alert Types
              </Typography>
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.lowStockAlerts}
                    onChange={(e) => handleNotificationChange('lowStockAlerts', e.target.checked)}
                  />
                }
                label="Low Stock Alerts"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.orderAlerts}
                    onChange={(e) => handleNotificationChange('orderAlerts', e.target.checked)}
                  />
                }
                label="Order Status Alerts"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.systemAlerts}
                    onChange={(e) => handleNotificationChange('systemAlerts', e.target.checked)}
                  />
                }
                label="System Alerts"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={notificationSettings.performanceReports}
                    onChange={(e) => handleNotificationChange('performanceReports', e.target.checked)}
                  />
                }
                label="Performance Reports"
              />
            </Grid>
          </Grid>
        </TabPanel>

        <TabPanel value={activeTab} index={2}>
          <Typography variant="h6" gutterBottom>
            Security Settings
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={securitySettings.twoFactorAuth}
                    onChange={(e) => handleSecurityChange('twoFactorAuth', e.target.checked)}
                  />
                }
                label="Two-Factor Authentication"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={securitySettings.auditLogging}
                    onChange={(e) => handleSecurityChange('auditLogging', e.target.checked)}
                  />
                }
                label="Audit Logging"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Session Timeout (minutes)"
                type="number"
                value={securitySettings.sessionTimeout}
                onChange={(e) => handleSecurityChange('sessionTimeout', parseInt(e.target.value))}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Password Expiry (days)"
                type="number"
                value={securitySettings.passwordExpiry}
                onChange={(e) => handleSecurityChange('passwordExpiry', parseInt(e.target.value))}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Max Login Attempts"
                type="number"
                value={securitySettings.loginAttempts}
                onChange={(e) => handleSecurityChange('loginAttempts', parseInt(e.target.value))}
              />
            </Grid>
          </Grid>
        </TabPanel>

        <TabPanel value={activeTab} index={3}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
            <Typography variant="h6">
              API Integration Settings
            </Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setApiKeyDialog(true)}
            >
              Create API Key
            </Button>
          </Box>
          
          <Grid container spacing={3} sx={{ mb: 4 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="API Rate Limit (requests/hour)"
                type="number"
                value={integrationSettings.apiRateLimit}
                onChange={(e) => handleIntegrationChange('apiRateLimit', parseInt(e.target.value))}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Webhook Retries"
                type="number"
                value={integrationSettings.webhookRetries}
                onChange={(e) => handleIntegrationChange('webhookRetries', parseInt(e.target.value))}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Cache Timeout (seconds)"
                type="number"
                value={integrationSettings.cacheTimeout}
                onChange={(e) => handleIntegrationChange('cacheTimeout', parseInt(e.target.value))}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={integrationSettings.enableCors}
                    onChange={(e) => handleIntegrationChange('enableCors', e.target.checked)}
                  />
                }
                label="Enable CORS"
              />
            </Grid>
          </Grid>

          <Typography variant="subtitle1" gutterBottom>
            API Keys
          </Typography>
          <List>
            {apiKeys.map((key) => (
              <ListItem key={key.id} divider>
                <ListItemText
                  primary={key.name}
                  secondary={
                    <Box>
                      <Typography variant="body2" color="textSecondary">
                        Key: {key.key}
                      </Typography>
                      <Box sx={{ mt: 1 }}>
                        {key.permissions.map((permission) => (
                          <Chip
                            key={permission}
                            label={permission}
                            size="small"
                            sx={{ mr: 1 }}
                          />
                        ))}
                      </Box>
                      <Typography variant="caption" color="textSecondary">
                        Created: {key.created} | Last used: {key.lastUsed}
                      </Typography>
                    </Box>
                  }
                />
                <ListItemSecondaryAction>
                  <IconButton
                    edge="end"
                    onClick={() => handleDeleteApiKey(key.id)}
                    color="error"
                  >
                    <DeleteIcon />
                  </IconButton>
                </ListItemSecondaryAction>
              </ListItem>
            ))}
          </List>
        </TabPanel>

        <TabPanel value={activeTab} index={4}>
          <Typography variant="h6" gutterBottom>
            Storage & Backup Settings
          </Typography>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <Alert severity="info">
                <AlertTitle>Storage Information</AlertTitle>
                Current storage usage and backup settings will be displayed here.
              </Alert>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Database Storage
                  </Typography>
                  <Typography variant="h4" color="primary">
                    2.4 GB
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    of 100 GB used
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Card variant="outlined">
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    File Storage
                  </Typography>
                  <Typography variant="h4" color="primary">
                    15.8 GB
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    of 500 GB used
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </TabPanel>
      </Card>

      {/* API Key Creation Dialog */}
      <Dialog open={apiKeyDialog} onClose={() => setApiKeyDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create New API Key</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="API Key Name"
            value={newApiKey.name}
            onChange={(e) => setNewApiKey({ ...newApiKey, name: e.target.value })}
            sx={{ mb: 2, mt: 1 }}
          />
          <FormControl fullWidth>
            <InputLabel>Permissions</InputLabel>
            <Select
              multiple
              value={newApiKey.permissions}
              onChange={(e) => setNewApiKey({ ...newApiKey, permissions: e.target.value })}
              renderValue={(selected) => (
                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                  {selected.map((value) => (
                    <Chip key={value} label={value} />
                  ))}
                </Box>
              )}
            >
              <MenuItem value="read">Read</MenuItem>
              <MenuItem value="write">Write</MenuItem>
              <MenuItem value="admin">Admin</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setApiKeyDialog(false)}>Cancel</Button>
          <Button onClick={handleCreateApiKey} variant="contained">
            Create
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default Settings;