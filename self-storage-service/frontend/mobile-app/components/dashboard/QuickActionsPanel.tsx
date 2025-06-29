import React from 'react';
import { View, Text, TouchableOpacity, StyleSheet, ViewStyle, Alert } from 'react-native';

interface QuickActionsPanelProps {
  style?: ViewStyle;
}

const QuickActionsPanel: React.FC<QuickActionsPanelProps> = ({ style }) => {
  const handleAction = (action: string) => {
    Alert.alert('Quick Action', `${action} functionality coming soon!`);
  };

  const actions = [
    { id: 'scan', title: 'Scan QR', icon: '📱', color: '#2563eb' },
    { id: 'inventory', title: 'Inventory', icon: '📦', color: '#059669' },
    { id: 'orders', title: 'Orders', icon: '📋', color: '#7c3aed' },
    { id: 'reports', title: 'Reports', icon: '📊', color: '#dc2626' },
  ];

  return (
    <View style={[styles.container, style]}>
      <View style={styles.header}>
        <Text style={styles.title}>Quick Actions</Text>
      </View>
      <View style={styles.actionsGrid}>
        {actions.map((action) => (
          <TouchableOpacity
            key={action.id}
            style={styles.actionButton}
            onPress={() => handleAction(action.title)}
          >
            <View style={[styles.actionIcon, { backgroundColor: `${action.color}15` }]}>
              <Text style={styles.actionIconText}>{action.icon}</Text>
            </View>
            <Text style={styles.actionTitle}>{action.title}</Text>
          </TouchableOpacity>
        ))}
      </View>
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
    marginBottom: 16,
  },
  title: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1f2937',
  },
  actionsGrid: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  actionButton: {
    alignItems: 'center',
    flex: 1,
    paddingVertical: 8,
  },
  actionIcon: {
    width: 48,
    height: 48,
    borderRadius: 12,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 8,
  },
  actionIconText: {
    fontSize: 20,
  },
  actionTitle: {
    fontSize: 12,
    color: '#6b7280',
    textAlign: 'center',
  },
});

export default QuickActionsPanel;