import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';
import { Card } from '../components/Card';
import { warehouseService } from '../services/warehouseService';

const DashboardScreen = () => {
  const [stats, setStats] = useState({
    pendingTasks: 0,
    completedToday: 0,
    activeZone: '',
    productivity: 0
  });

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const data = await warehouseService.getDashboardStats();
      setStats(data);
    } catch (error) {
      console.error('Error loading dashboard:', error);
    }
  };

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.header}>Welcome, Staff Member</Text>
ECHO is off.
      <View style={styles.statsGrid}>
        <Card title="Pending Tasks" value={stats.pendingTasks} color="#FF6B6B" />
        <Card title="Completed Today" value={stats.completedToday} color="#4ECDC4" />
        <Card title="Active Zone" value={stats.activeZone} color="#45B7D1" />
        <Card title="Productivity" value={`${stats.productivity}%`} color="#96CEB4" />
      </View>

      <TouchableOpacity style={styles.quickAction}>
        <Text style={styles.quickActionText}>Start New Task</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 20,
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 20,
  },
  statsGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  quickAction: {
    backgroundColor: '#2196F3',
    padding: 15,
    borderRadius: 8,
    marginTop: 20,
    alignItems: 'center',
  },
  quickActionText: {
    color: 'white',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default DashboardScreen;
