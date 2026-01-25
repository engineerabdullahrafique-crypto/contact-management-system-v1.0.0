import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import 'bootstrap/dist/css/bootstrap.min.css';
import './styles/App.css';

const Login = React.lazy(() => import('./components/auth/Login'));
const Register = React.lazy(() => import('./components/auth/Register'));
const Layout = React.lazy(() => import('./components/layout/Layout'));
const ContactList = React.lazy(() => import('./components/contacts/ContactList'));
const ChangePassword = React.lazy(() => import('./components/auth/ChangePassword'));
const ContactForm = React.lazy(() => import('./components/contacts/ContactForm'));
const ContactDetail = React.lazy(() => import('./components/contacts/ContactDetail'));
const UserProfile = React.lazy(() => import('./components/profile/UserProfile'));

const PrivateRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return isAuthenticated ? children : <Navigate to="/login" />;
};

const PublicRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  return !isAuthenticated ? children : <Navigate to="/dashboard" />;
};


const LoadingSpinner = () => (
  <div className="loading-screen">
    <div className="spinner-border text-primary" role="status">
      <span className="visually-hidden">Loading...</span>
    </div>
  </div>
);

function AppContent() {
  return (
    <div className="App">
      <Toaster
        position="top-right"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#363636',
            color: '#fff',
          },
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#4CAF50',
              secondary: '#fff',
            },
          },
          error: {
            duration: 4000,
            iconTheme: {
              primary: '#FF5252',
              secondary: '#fff',
            },
          },
        }}
      />

      <Suspense fallback={<LoadingSpinner />}>
        <Routes>          
          <Route path="/login" element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          } />
          <Route path="/register" element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          } />
          
          <Route path="/" element={<Navigate to="/dashboard" />} />
          <Route path="/dashboard" element={
            <PrivateRoute>
              <Layout />
            </PrivateRoute>
          }>
            <Route index element={<ContactList />} />
            <Route path="contacts" element={<ContactList />} />            
            <Route path="contacts/new" element={<ContactForm />} />
            <Route path="contacts/:id" element={<ContactDetail />} />
            <Route path="contacts/:id/edit" element={<ContactForm />} />            
            <Route path="change-password" element={<ChangePassword />} />
            <Route path="profile" element={<UserProfile />} />
          </Route>
          
          <Route path="*" element={<Navigate to="/dashboard" />} />
        </Routes>
      </Suspense>
    </div>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}

export default App;