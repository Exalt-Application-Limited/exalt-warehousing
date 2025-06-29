import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const API_BASE_URL = 'http://localhost:8080/api';

class WarehouseService {
  constructor() {
    this.api = axios.create({
      baseURL: API_BASE_URL,
      timeout: 10000,
    });

    // Add auth token to requests
    this.api.interceptors.request.use(async (config) => {
      const token = await AsyncStorage.getItem('authToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });
  }

  // Dashboard
  async getDashboardStats() {
    try {
      const response = await this.api.get('/dashboard/stats');
      return response.data;
    } catch (error) {
      console.error('Error fetching dashboard stats:', error);
      return {
        pendingTasks: 0,
        completedToday: 0,
        activeZone: 'N/A',
        productivity: 0,
      };
    }
  }

  // Tasks
  async getTasks(status = null) {
    try {
      const params = status ? { status } : {};
      const response = await this.api.get('/tasks', { params });
      return response.data;
    } catch (error) {
      console.error('Error fetching tasks:', error);
      return [];
    }
  }

  async getTask(taskId) {
    try {
      const response = await this.api.get(`/tasks/${taskId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching task:', error);
      return null;
    }
  }

  async startTask(taskId) {
    try {
      const response = await this.api.post(`/tasks/${taskId}/start`);
      return response.data;
    } catch (error) {
      console.error('Error starting task:', error);
      throw error;
    }
  }

  async completeTask(taskId, data) {
    try {
      const response = await this.api.post(`/tasks/${taskId}/complete`, data);
      return response.data;
    } catch (error) {
      console.error('Error completing task:', error);
      throw error;
    }
  }

  // Scanning
  async scanLocation(qrCode) {
    try {
      const response = await this.api.post('/scan/location', { qrCode });
      return response.data;
    } catch (error) {
      console.error('Error scanning location:', error);
      throw error;
    }
  }

  async scanItem(barcode) {
    try {
      const response = await this.api.post('/scan/item', { barcode });
      return response.data;
    } catch (error) {
      console.error('Error scanning item:', error);
      throw error;
    }
  }

  // Profile
  async getProfile() {
    try {
      const response = await this.api.get('/profile');
      return response.data;
    } catch (error) {
      console.error('Error fetching profile:', error);
      return null;
    }
  }

  async updateProfile(data) {
    try {
      const response = await this.api.put('/profile', data);
      return response.data;
    } catch (error) {
      console.error('Error updating profile:', error);
      throw error;
    }
  }

  // Auth
  async login(username, password) {
    try {
      const response = await this.api.post('/auth/login', { username, password });
      const { token, user } = response.data;
      await AsyncStorage.setItem('authToken', token);
      await AsyncStorage.setItem('user', JSON.stringify(user));
      return { token, user };
    } catch (error) {
      console.error('Login error:', error);
      throw error;
    }
  }

  async logout() {
    try {
      await AsyncStorage.removeItem('authToken');
      await AsyncStorage.removeItem('user');
    } catch (error) {
      console.error('Logout error:', error);
    }
  }
}

export const warehouseService = new WarehouseService();