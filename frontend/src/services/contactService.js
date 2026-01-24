import api from './api';
import { authService } from './authService';
export const contactService = {
  getAllContacts: async (page = 0, size = 10) => {
    try {
      const response = await api.get(`/api/contacts`, {
        params: { page, size },
        headers: authService.getAuthHeader()
      });
      // Return success and the data (Spring page object)
      return { success: true, data: response.data };
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Failed to fetch contacts' 
      };
    }
  },
  // Searching contacts
  searchContacts: async (query) => {
    try {
      const response = await api.get('/api/contacts/search', {
        params: { query }
      });
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Search failed' };
    }
  },

  // Get single contact
  getContactById: async (id) => {
    try {
      const response = await api.get(`/api/contacts/${id}`);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to fetch contact' };
    }
  },

  // Creating a contact
  createContact: async (contactData) => {
    try {
      const response = await api.post('/api/contacts', contactData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to create contact' };
    }
  },

  // Updating a contact
  updateContact: async (id, contactData) => {
    try {
      const response = await api.put(`/api/contacts/${id}`, contactData);
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to update contact' };
    }
  },

  // Deleting a contact
  deleteContact: async (id) => {
    try {
      await api.delete(`/api/contacts/${id}`);
      return { success: true };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to delete contact' };
    }
  },

  // Count contacts and show on homepage
  countContacts: async () => {
    try {
      const response = await api.get('/api/contacts/count');
      return { success: true, data: response.data };
    } catch (error) {
      return { success: false, error: error.response?.data?.message || 'Failed to count contacts' };
    }
  }
};