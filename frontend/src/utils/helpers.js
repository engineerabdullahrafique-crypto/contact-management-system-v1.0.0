import { STORAGE_KEYS } from './constants';

// Local Storage Helpers
export const getToken = () => localStorage.getItem(STORAGE_KEYS.TOKEN);
export const setToken = (token) => localStorage.setItem(STORAGE_KEYS.TOKEN, token);
export const removeToken = () => localStorage.removeItem(STORAGE_KEYS.TOKEN);

export const getUser = () => {
  const user = localStorage.getItem(STORAGE_KEYS.USER);
  return user ? JSON.parse(user) : null;
};
export const setUser = (user) => localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
export const removeUser = () => localStorage.removeItem(STORAGE_KEYS.USER);

// Formatting Helpers
export const formatPhoneNumber = (phone) => {
  if (!phone) return '';
  // Remove all non-numeric characters
  const cleaned = phone.replace(/\D/g, '');
  
  // Format based on length
  if (cleaned.length === 10) {
    return `(${cleaned.slice(0, 3)}) ${cleaned.slice(3, 6)}-${cleaned.slice(6)}`;
  }
  
  return phone;
};

export const formatDate = (dateString, format = 'display') => {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const options = { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric' 
  };
  
  if (format === 'full') {
    options.month = 'long';
  }
  
  if (format === 'with-time') {
    return date.toLocaleDateString('en-US', {
      ...options,
      hour: '2-digit',
      minute: '2-digit'
    });
  }
  
  return date.toLocaleDateString('en-US', options);
};

export const capitalize = (str) => {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

export const truncateText = (text, maxLength = 100) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + '...';
};

// Validation Helpers
export const isValidEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

export const isValidPhone = (phone) => {
  const phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/;
  return phoneRegex.test(phone);
};

// API Response Handler
export const handleApiResponse = async (response) => {
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'API request failed');
  }
  return response.json();
};

// Debounce Function
export const debounce = (func, wait) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

// Generate Initials
export const getInitials = (name) => {
  if (!name) return '?';
  return name
    .split(' ')
    .map(word => word.charAt(0))
    .join('')
    .toUpperCase()
    .slice(0, 2);
};

// File Helpers
export const validateFile = (file, maxSizeMB = 5) => {
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  const maxSize = maxSizeMB * 1024 * 1024;
  
  if (!validTypes.includes(file.type)) {
    return { valid: false, error: 'Invalid file type. Only JPEG, PNG, GIF, and WebP are allowed.' };
  }
  
  if (file.size > maxSize) {
    return { valid: false, error: `File size must be less than ${maxSizeMB}MB.` };
  }
  
  return { valid: true, error: null };
};

// Theme Helper
export const getTheme = () => {
  return localStorage.getItem(STORAGE_KEYS.THEME) || 'light';
};

export const setTheme = (theme) => {
  localStorage.setItem(STORAGE_KEYS.THEME, theme);
  document.documentElement.setAttribute('data-theme', theme);
};

// Array Helpers
export const sortByField = (array, field, order = 'asc') => {
  return [...array].sort((a, b) => {
    let aValue = a[field];
    let bValue = b[field];
    
    if (typeof aValue === 'string') {
      aValue = aValue.toLowerCase();
      bValue = bValue.toLowerCase();
    }
    
    if (order === 'desc') {
      return aValue < bValue ? 1 : -1;
    }
    
    return aValue > bValue ? 1 : -1;
  });
};

// Number Helpers
export const formatNumber = (num) => {
  if (typeof num !== 'number') return '0';
  return num.toLocaleString('en-US');
};