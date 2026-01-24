// API Endpoints
export const API_BASE_URL = 'http://localhost:5000/api';

// Auth Endpoints
export const AUTH_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/auth/login`,
  REGISTER: `${API_BASE_URL}/auth/register`,
  PROFILE: `${API_BASE_URL}/auth/profile`,
  CHANGE_PASSWORD: `${API_BASE_URL}/auth/changepassword`,
  LOGOUT: `${API_BASE_URL}/auth/logout`,
};

// Contacts Endpoints
export const CONTACT_ENDPOINTS = {
  GET_ALL: `${API_BASE_URL}/contacts`,
  GET_ONE: (id) => `${API_BASE_URL}/contacts/${id}`,
  CREATE: `${API_BASE_URL}/contacts`,
  UPDATE: (id) => `${API_BASE_URL}/contacts/${id}`,
  DELETE: (id) => `${API_BASE_URL}/contacts/${id}`,
  SEARCH: `${API_BASE_URL}/contacts/search`,
};

// Local Storage Keys
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER: 'user',
  THEME: 'theme',
};

// Theme Constants
export const THEMES = {
  LIGHT: 'light',
  DARK: 'dark',
};

// Validation Constants
export const VALIDATION = {
  PASSWORD_MIN_LENGTH: 6,
  NAME_MIN_LENGTH: 2,
  NAME_MAX_LENGTH: 50,
  EMAIL_MAX_LENGTH: 100,
  PHONE_MAX_LENGTH: 20,
  COMPANY_MAX_LENGTH: 100,
  JOB_TITLE_MAX_LENGTH: 100,
  ADDRESS_MAX_LENGTH: 200,
  NOTES_MAX_LENGTH: 500,
};

// Contact Status
export const CONTACT_STATUS = {
  ACTIVE: 'active',
  INACTIVE: 'inactive',
  ARCHIVED: 'archived',
};

// Contact Categories
export const CONTACT_CATEGORIES = [
  'Personal',
  'Work',
  'Family',
  'Friends',
  'Business',
  'Other'
];

// Sort Options
export const SORT_OPTIONS = [
  { value: 'name_asc', label: 'Name (A-Z)' },
  { value: 'name_desc', label: 'Name (Z-A)' },
  { value: 'created_desc', label: 'Newest First' },
  { value: 'created_asc', label: 'Oldest First' },
  { value: 'updated_desc', label: 'Recently Updated' },
];

// Pagination
export const ITEMS_PER_PAGE = 10;
export const DEFAULT_PAGE = 1;

// Toast Messages
export const TOAST_MESSAGES = {
  SUCCESS: {
    LOGIN: 'Login successful!',
    REGISTER: 'Registration successful!',
    PROFILE_UPDATE: 'Profile updated successfully!',
    PASSWORD_CHANGE: 'Password changed successfully!',
    CONTACT_CREATED: 'Contact created successfully!',
    CONTACT_UPDATED: 'Contact updated successfully!',
    CONTACT_DELETED: 'Contact deleted successfully!',
  },
  ERROR: {
    LOGIN: 'Invalid credentials',
    REGISTER: 'Registration failed',
    NETWORK: 'Network error. Please try again.',
    UNAUTHORIZED: 'Please login to continue',
    SERVER: 'Server error. Please try again later.',
    VALIDATION: 'Please check your input',
  },
};

// Date Formats
export const DATE_FORMATS = {
  DISPLAY: 'MMM DD, YYYY',
  DISPLAY_FULL: 'MMMM DD, YYYY',
  DISPLAY_WITH_TIME: 'MMM DD, YYYY hh:mm A',
  API: 'YYYY-MM-DD',
};