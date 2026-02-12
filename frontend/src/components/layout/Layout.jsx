import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Footer from './Footer';
import './Layout.css';

const Layout = () => {
  return (
    <div className="layout-container">
      <div className="main-wrapper d-flex">
        <div className="sidebar-wrapper">
          <Sidebar />
        </div>
        
        <div className="main-content-wrapper flex-grow-1">
          <div className="main-content">
            <Outlet />
          </div>
        </div>
      </div>
      
      <Footer />
    </div>
  );
};

export default Layout;