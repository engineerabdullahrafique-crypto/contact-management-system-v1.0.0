import './Layout.css';

const Footer = () => {
  return (
    <footer className="footer bg-dark text-white py-4 mt-auto">
      <div className="container">
        <div className="row">
          <div className="col-md-6">
            <h5>Contact Management System</h5>
            <p className="mb-0 text-muted">
              Manage your contacts efficiently and securely.
            </p>
          </div>
          <div className="col-md-6 text-md-end">
            <p className="mb-0">
              © {new Date().getFullYear()} Contact Manager. All rights reserved.
            </p>
            <p className="text-muted mb-0">
              Version 1.0.0
            </p>
          </div>
        </div>
        <hr className="my-3 text-muted" />
        <div className="row">
          <div className="col-12 text-center">
            <p className="text-muted mb-0">
              <small>
                Built with React, Node.js, and MongoDB
              </small>
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;