import React from 'react';
import { View, Text, StyleSheet, ViewStyle, TouchableOpacity, Alert } from 'react-native';
import { DashboardAlert } from '../../api/dashboard';

interface AlertsPanelProps {
  alerts: DashboardAlert[];
  style?: ViewStyle;
}

const AlertsPanel: React.FC<AlertsPanelProps> = ({ alerts, style }) => {
  const getAlertColor = (type: string) => {
    switch (type) {
      case 'error': return '#ef4444';
      case 'warning': return '#f59e0b';
      case 'info': return '#3b82f6';
      default: return '#6b7280';
    }
  };

  const getAlertIcon = (type: string) => {
    switch (type) {
      case 'error': return '🚨';
      case 'warning': return '⚠️';
      case 'info': return 'ℹ️';
      default: return '📝';
    }
  };

  const handleAlertPress = (alert: DashboardAlert) => {
    Alert.alert(alert.title, alert.message);
  };

  if (alerts.length === 0) return null;

  return (
    <View style={[styles.container, style]}>
      <View style={styles.header}>
        <Text style={styles.title}>Alerts</Text>
        <Text style={styles.badge}>{alerts.length}</Text>
      </View>
      
      {alerts.map((alert) => (
        <TouchableOpacity
          key={alert.id}
          style={styles.alertItem}
          onPress={() => handleAlertPress(alert)}
        >
          <View style={[styles.alertIndicator, { backgroundColor: getAlertColor(alert.type) }]} />
          <View style={styles.alertIcon}>
            <Text style={styles.alertIconText}>{getAlertIcon(alert.type)}</Text>
          </View>
          <View style={styles.alertContent}>
            <Text style={styles.alertTitle}>{alert.title}</Text>
            <Text style={styles.alertMessage} numberOfLines={2}>
              {alert.message}
            </Text>
            <Text style={styles.alertTime}>
              {new Date(alert.timestamp).toLocaleTimeString([], { 
                hour: '2-digit', 
                minute: '2-digit' 
              })}
            </Text>
          </View>
        </TouchableOpacity>
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#ffffff',
    marginHorizontal: 20,
    borderRadius: 12,
    padding: 16,
    shadowColor: '#000000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.05,
    shadowRadius: 4,
    elevation: 2,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 12,
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1f2937',
  },
  badge: {
    backgroundColor: '#ef4444',
    color: '#ffffff',
    fontSize: 12,
    fontWeight: '600',
    paddingHorizontal: 8,
    paddingVertical: 2,
    borderRadius: 10,
    minWidth: 20,
    textAlign: 'center',
  },
  alertItem: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f3f4f6',
  },
  alertIndicator: {
    width: 4,
    height: '100%',
    borderRadius: 2,
    marginRight: 12,
  },
  alertIcon: {
    marginRight: 8,
  },
  alertIconText: {
    fontSize: 16,
  },
  alertContent: {
    flex: 1,
  },
  alertTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#1f2937',
    marginBottom: 2,
  },
  alertMessage: {
    fontSize: 12,
    color: '#6b7280',
    lineHeight: 16,
    marginBottom: 4,
  },
  alertTime: {
    fontSize: 10,
    color: '#9ca3af',
  },
});

export default AlertsPanel;