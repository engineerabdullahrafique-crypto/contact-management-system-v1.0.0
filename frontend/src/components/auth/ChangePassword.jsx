import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { userService } from '../../services/userService';
import './Auth.css';

const ChangePassword = () => {
    const [formData, setFormData] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const { currentPassword, newPassword, confirmPassword } = formData;

    const onChange = e => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const onSubmit = async e => {
        e.preventDefault();
        setError('');
        setSuccess('');
        
        if (newPassword !== confirmPassword) {
            setError('New passwords do not match');
            return;
        }

        try {
            setLoading(true);            
            const result = await userService.changePassword({ 
                currentPassword, 
                newPassword 
            });

            if (result.success) {
                setSuccess('Password changed successfully!');
                setFormData({ currentPassword: '', newPassword: '', confirmPassword: '' });
                setTimeout(() => navigate('/dashboard'), 2000);
            } else {
                setError(result.error);
            }
        } catch (err) {
            setError('Failed to connect to the server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <h2>Change Password</h2>
                <form onSubmit={onSubmit} className="auth-form">
                    <div className="form-group">
                        <label>Current Password</label>
                        <input
                            type="password"
                            name="currentPassword"
                            value={currentPassword}
                            onChange={onChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>New Password</label>
                        <input
                            type="password"
                            name="newPassword"
                            value={newPassword}
                            onChange={onChange}
                            required
                            minLength="6"
                        />
                    </div>
                    <div className="form-group">
                        <label>Confirm New Password</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            value={confirmPassword}
                            onChange={onChange}
                            required
                        />
                    </div>
                    {error && <div className="alert alert-danger">{error}</div>}
                    {success && <div className="alert alert-success">{success}</div>}
                    <button type="submit" className="btn btn-primary w-100" disabled={loading}>
                        {loading ? 'Processing...' : 'Change Password'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default ChangePassword;