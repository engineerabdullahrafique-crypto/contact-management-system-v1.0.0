import React, { useState, useEffect } from 'react';
import { userService } from '../../services/userService';
import './profile.css';
import LoadingSpinner from '../common/LoadingSpinner';
import ErrorAlert from '../common/ErrorAlert';

const UserProfile = () => {  
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    company: '',
    jobTitle: ''
  });

  // Data fetching func
  const fetchProfile = async () => {
    try {
      setLoading(true);
      setError('');      
      const result = await userService.getProfile();
      
      if (result.success) {
        setProfile(result.data);
        setFormData({
          name: result.data.name || '',
          email: result.data.email || '',
          phone: result.data.phone || '',
          company: result.data.company || '',
          jobTitle: result.data.jobTitle || ''
        });
      } else {
        setError(result.error);
      }
    } catch (err) {
      setError('Failed to load profile details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setError('');
      setSuccess('');
      
      const result = await userService.updateProfile(formData);
      
      if (result.success) {
        setProfile(result.data);
        setSuccess('Profile updated successfully!');
        setEditMode(false);
      } else {
        setError(result.error);
      }
    } catch (err) {
      setError('An error occurred while updating the profile');
    }
  };

  if (loading) return <LoadingSpinner fullPage text="Loading profile..." />;

  return (
    <div className="user-profile">
      <div className="profile-header">
        <h2>User Profile</h2>
        <button 
          className={`btn ${editMode ? 'btn-secondary' : 'btn-primary'}`}
          onClick={() => {
            setEditMode(!editMode);
            setError('');
            setSuccess('');
          }}
        >
          {editMode ? 'Cancel Edit' : 'Edit Profile'}
        </button>
      </div>

      {error && <ErrorAlert message={error} onClose={() => setError('')} />}
      
      {success && (
        <div className="alert alert-success alert-dismissible fade show" role="alert">
          {success}
          <button 
            type="button" 
            className="btn-close" 
            onClick={() => setSuccess('')}
          ></button>
        </div>
      )}

      <div className="profile-card">
        <div className="profile-avatar">
          {profile?.photo ? (
            <img src={profile.photo} alt={profile.name} />
          ) : (
            <div className="avatar-initials">
              {profile?.name ? profile.name.charAt(0).toUpperCase() : '?'}
            </div>
          )}
          <h3>{profile?.name}</h3>
          <p className="profile-email">{profile?.email}</p>
        </div>

        {editMode ? (
          <form onSubmit={handleSubmit} className="profile-form">
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Email</label>
                <input
                  type="email"
                  className="form-control"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">Phone</label>
                <input
                  type="tel"
                  className="form-control"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Company</label>
                <input
                  type="text"
                  className="form-control"
                  name="company"
                  value={formData.company}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="mb-3">
              <label className="form-label">Job Title</label>
              <input
                type="text"
                className="form-control"
                name="jobTitle"
                value={formData.jobTitle}
                onChange={handleChange}
              />
            </div>
            <button type="submit" className="btn btn-primary w-100">
              Save Changes
            </button>
          </form>
        ) : (
          <div className="profile-details">
            <div className="detail-group">
              <label>Email</label>
              <p>{profile?.email}</p>
            </div>
            <div className="detail-group">
              <label>Phone</label>
              <p>{profile?.phone || 'Not provided'}</p>
            </div>
            <div className="detail-group">
              <label>Company</label>
              <p>{profile?.company || 'Not provided'}</p>
            </div>
            <div className="detail-group">
              <label>Job Title</label>
              <p>{profile?.jobTitle || 'Not provided'}</p>
            </div>
            <div className="detail-group">
              <label>Member Since</label>
              <p>
                {profile?.createdAt 
                  ? new Date(profile.createdAt).toLocaleDateString() 
                  : 'N/A'}
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserProfile;