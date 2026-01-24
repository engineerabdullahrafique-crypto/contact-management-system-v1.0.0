import { VALIDATION } from './constants';

export const validateName = (name) => {
  if (!name || name.trim().length < VALIDATION.NAME_MIN_LENGTH) {
    return `Name must be at least ${VALIDATION.NAME_MIN_LENGTH} characters`;
  }
  if (name.length > VALIDATION.NAME_MAX_LENGTH) {
    return `Name must be less than ${VALIDATION.NAME_MAX_LENGTH} characters`;
  }
  return '';
};

export const validateEmail = (email) => {
  if (!email) {
    return 'Email is required';
  }
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return 'Please enter a valid email address';
  }
  
  if (email.length > VALIDATION.EMAIL_MAX_LENGTH) {
    return `Email must be less than ${VALIDATION.EMAIL_MAX_LENGTH} characters`;
  }
  
  return '';
};

export const validatePassword = (password) => {
  if (!password) {
    return 'Password is required';
  }
  
  if (password.length < VALIDATION.PASSWORD_MIN_LENGTH) {
    return `Password must be at least ${VALIDATION.PASSWORD_MIN_LENGTH} characters`;
  }
  
  // Optional: Add more password requirements
  if (!/[A-Z]/.test(password)) {
    return 'Password must contain at least one uppercase letter';
  }
  
  if (!/[0-9]/.test(password)) {
    return 'Password must contain at least one number';
  }
  
  return '';
};

export const validateConfirmPassword = (password, confirmPassword) => {
  if (password !== confirmPassword) {
    return 'Passwords do not match';
  }
  return '';
};

export const validatePhone = (phone) => {
  if (!phone) return ''; // Phone is optional
  
  const phoneRegex = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/;
  if (!phoneRegex.test(phone)) {
    return 'Please enter a valid phone number';
  }
  
  if (phone.length > VALIDATION.PHONE_MAX_LENGTH) {
    return `Phone number must be less than ${VALIDATION.PHONE_MAX_LENGTH} characters`;
  }
  
  return '';
};

export const validateCompany = (company) => {
  if (!company) return ''; // Company is optional
  
  if (company.length > VALIDATION.COMPANY_MAX_LENGTH) {
    return `Company name must be less than ${VALIDATION.COMPANY_MAX_LENGTH} characters`;
  }
  
  return '';
};

export const validateJobTitle = (jobTitle) => {
  if (!jobTitle) return ''; // Job title is optional
  
  if (jobTitle.length > VALIDATION.JOB_TITLE_MAX_LENGTH) {
    return `Job title must be less than ${VALIDATION.JOB_TITLE_MAX_LENGTH} characters`;
  }
  
  return '';
};

export const validateAddress = (address) => {
  if (!address) return ''; // Address is optional
  
  if (address.length > VALIDATION.ADDRESS_MAX_LENGTH) {
    return `Address must be less than ${VALIDATION.ADDRESS_MAX_LENGTH} characters`;
  }
  
  return '';
};

export const validateNotes = (notes) => {
  if (!notes) return ''; // Notes are optional
  
  if (notes.length > VALIDATION.NOTES_MAX_LENGTH) {
    return `Notes must be less than ${VALIDATION.NOTES_MAX_LENGTH} characters`;
  }
  
  return '';
};

// Contact validation
export const validateContact = (contact) => {
  const errors = {};
  
  errors.name = validateName(contact.name);
  errors.email = validateEmail(contact.email);
  errors.phone = validatePhone(contact.phone);
  errors.company = validateCompany(contact.company);
  errors.jobTitle = validateJobTitle(contact.jobTitle);
  errors.address = validateAddress(contact.address);
  errors.notes = validateNotes(contact.notes);
  
  // Filter out empty error messages
  const filteredErrors = Object.fromEntries(
    Object.entries(errors).filter(([_, value]) => value !== '')
  );
  
  return {
    isValid: Object.keys(filteredErrors).length === 0,
    errors: filteredErrors
  };
};

// Login validation
export const validateLogin = (email, password) => {
  const errors = {};
  
  errors.email = validateEmail(email);
  errors.password = password ? '' : 'Password is required';
  
  const filteredErrors = Object.fromEntries(
    Object.entries(errors).filter(([_, value]) => value !== '')
  );
  
  return {
    isValid: Object.keys(filteredErrors).length === 0,
    errors: filteredErrors
  };
};

// Registration validation
export const validateRegistration = (user) => {
  const errors = {};
  
  errors.name = validateName(user.name);
  errors.email = validateEmail(user.email);
  errors.password = validatePassword(user.password);
  errors.password2 = validateConfirmPassword(user.password, user.password2);
  
  const filteredErrors = Object.fromEntries(
    Object.entries(errors).filter(([_, value]) => value !== '')
  );
  
  return {
    isValid: Object.keys(filteredErrors).length === 0,
    errors: filteredErrors
  };
};

// Change password validation
export const validateChangePassword = (passwords) => {
  const errors = {};
  
  errors.currentPassword = passwords.currentPassword ? '' : 'Current password is required';
  errors.newPassword = validatePassword(passwords.newPassword);
  errors.confirmPassword = validateConfirmPassword(passwords.newPassword, passwords.confirmPassword);
  
  const filteredErrors = Object.fromEntries(
    Object.entries(errors).filter(([_, value]) => value !== '')
  );
  
  return {
    isValid: Object.keys(filteredErrors).length === 0,
    errors: filteredErrors
  };
};

// Helper to display validation errors
export const getFirstError = (errors) => {
  const firstKey = Object.keys(errors)[0];
  return errors[firstKey] || '';
};