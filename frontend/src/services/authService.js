import api from './api';

export const authService = {

  login: async (email, password) => {
    try {
      const response = await api.post('/api/auth/login', { email, password });

      // Store token as a string we take this as JSON later:
      const token = response.data;

      // Creating user object
      const user = { email: email };

      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify(user));

      return { success: true, data: { token, user } };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Login failed' };
    }
  },

  // Registering user
  register: async (userData) => {
    try {
      const response = await api.post('/api/auth/register', userData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Registration failed' };
    }
  },

  // Request Reset Link
  forgotPassword: async (email) => {
    try {
      const response = await api.post('/api/user/forgot-password', { email });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Request failed' };
    }
  },

  // Submit New Password
  resetPassword: async (token, newPassword) => {
    try {
      const response = await api.post('/api/user/reset-password', { token, newPassword });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Reset failed' };
    }
  },

  // Logout user
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
  },

  getCurrentUser: () => {
    try {
      const userStr = localStorage.getItem('user');
      if (!userStr || userStr === "undefined") {
        return null;
      }
      return JSON.parse(userStr);
    } catch (error) {
      console.error("Error parsing user from localStorage:", error);
      localStorage.removeItem('user');
      return null;
    }
  },

  // Checking user authentication
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },

  // Getting auth header
  getAuthHeader: () => {
    const token = localStorage.getItem('token');
    return token ? { Authorization: `Bearer ${token}` } : {};
  }
};