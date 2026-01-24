import api from './api';

export const userService = {
  // Getting user profile and show on frontend
  getProfile: async () => {
    try {      
      const response = await api.get('/api/user/profile'); 
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to fetch profile' };
    }
  },

  // Changing password function
  changePassword: async (passwordData) => {
    try {
      const response = await api.put('/api/user/change-password', passwordData); 
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to change password' };
    }
  },

  // Updating profile
  updateProfile: async (profileData) => {
    try {
      const response = await api.put('/api/user/profile', profileData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to update profile' };
    }
  }
};