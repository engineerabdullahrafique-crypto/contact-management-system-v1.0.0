import { NavLink, useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';
import './Layout.css';


const Sidebar = () => {
  const navigate = useNavigate();
  
  const menuItems = [
    { path: '/dashboard/contacts', icon: 'bi-people-fill', label: 'Contacts' },
    { path: '/dashboard/profile', icon: 'bi-person-fill', label: 'Profile' },
    { path: '/dashboard/change-password', icon: 'bi-key-fill', label: 'Change Password' },
  ];

  const handleLogout = () => {
    authService.logout(); 
    navigate('/login');
  };

return (
    <div className="sidebar">
      <div className="sidebar-header">
        <h3 className="sidebar-title">
          <i className="bi bi-journal-text me-2"></i>
          Contact Manager
        </h3>
      </div>
      
      <div className="sidebar-menu">
        <ul className="nav flex-column">
          {menuItems.map((item) => (
            <li className="nav-item" key={item.path}>
              <NavLink
                to={item.path}
                className={({ isActive }) => 
                  `nav-link ${isActive ? 'active' : ''}`
                }
              >
                <i className={`bi ${item.icon} me-2`}></i>
                {item.label}
              </NavLink>
            </li>
          ))}
        </ul>
      </div>
      
      <div className="sidebar-footer">
        <button 
          className="btn btn-outline-danger w-100 logout-btn"
          onClick={handleLogout}
        >
          <i className="bi bi-box-arrow-right me-2"></i>
          Logout
        </button>
      </div>
    </div>
  );
};

export default Sidebar;