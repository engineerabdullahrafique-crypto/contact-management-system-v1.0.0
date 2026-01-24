import { Navbar, Nav, Container, Button, Dropdown, Badge } from 'react-bootstrap';
import { 
  FaBars, 
  FaBell, 
  FaUserCircle, 
  FaCog, 
  FaSignOutAlt,
  FaEnvelope,
  FaPhone
} from 'react-icons/fa';
import { useAuth } from '../../context/AuthContext';


const Header = ({ toggleSidebar, sidebarCollapsed }) => {
  const { user, logout } = useAuth();

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="header-navbar">
      <Container fluid>
        <Button 
          variant="dark" 
          onClick={toggleSidebar}
          className="me-3"
        >
          <FaBars />
        </Button>

        <Navbar.Brand href="/dashboard" className="fw-bold">
          <FaEnvelope className="me-2" />
          Contact Manager
        </Navbar.Brand>

        <Nav className="ms-auto align-items-center">
          {/* Notifications */}
          <Dropdown className="me-3">
            <Dropdown.Toggle variant="dark" id="dropdown-notifications">
              <FaBell />
              <Badge bg="danger" className="notification-badge">3</Badge>
            </Dropdown.Toggle>
            <Dropdown.Menu align="end">
              <Dropdown.Item>
                <small className="text-muted">You have 3 new messages</small>
              </Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>

          {/* User Profile */}
          <Dropdown>
            <Dropdown.Toggle variant="dark" id="dropdown-user">
              <FaUserCircle size={24} className="me-2" />
              <span className="d-none d-md-inline">{user?.email}</span>
            </Dropdown.Toggle>
            <Dropdown.Menu align="end">
              <Dropdown.Header>
                <div className="text-center">
                  <FaUserCircle size={48} className="mb-2" />
                  <h6 className="mb-0">{user?.email}</h6>
                  <small className="text-muted">{user?.phone || 'No phone'}</small>
                </div>
              </Dropdown.Header>
              <Dropdown.Divider />
              <Dropdown.Item href="/profile">
                <FaUserCircle className="me-2" />
                My Profile
              </Dropdown.Item>
              <Dropdown.Item href="/settings">
                <FaCog className="me-2" />
                Settings
              </Dropdown.Item>
              <Dropdown.Divider />
              <Dropdown.Item onClick={logout} className="text-danger">
                <FaSignOutAlt className="me-2" />
                Logout
              </Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </Nav>
      </Container>
    </Navbar>
  );
};

export default Header;