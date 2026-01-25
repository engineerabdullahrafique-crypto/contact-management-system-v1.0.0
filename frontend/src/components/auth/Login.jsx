import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { useAuth } from '../../context/AuthContext';
import { toast } from 'react-hot-toast';
import { 
  Container, 
  Row, 
  Col, 
  Card, 
  Form, 
  Button, 
  FloatingLabel,
  Spinner,
  Alert 
} from 'react-bootstrap';
import { FaSignInAlt, FaUserPlus, FaEnvelope, FaLock } from 'react-icons/fa';

const Login = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [loading, setLoading] = useState(false);

  const validationSchema = Yup.object({
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    password: Yup.string()
      .min(6, 'Password must be at least 6 characters')
      .required('Password is required')
  });

  const formik = useFormik({
    initialValues: {
      email: '',
      password: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      setLoading(true);
      const result = await login(values.email, values.password);
      
      if (result.success) {
        toast.success('Login successful!');
        // navigate('/dashboard');
        navigate('/contacts');
      } else {
        toast.error(result.error || 'Login failed');
      }
      setLoading(false);
    }
  });

  return (
    <div className="auth-page">
      <Container className="h-100">
        <Row className="h-100 justify-content-center align-items-center">
          <Col md={6} lg={5} xl={4}>
            <Card className="auth-card shadow-lg">
              <Card.Body className="p-5">
                <div className="text-center mb-4">
                  <div className="auth-icon">
                    <FaSignInAlt size={40} className="text-primary" />
                  </div>
                  <h2 className="mt-3 fw-bold">Welcome Back</h2>
                  <p className="text-muted">Sign in to your account</p>
                </div>

                <Form onSubmit={formik.handleSubmit}>
                  <FloatingLabel controlId="email" label="Email Address" className="mb-3">
                    <Form.Control
                      type="email"
                      placeholder="Email Address"
                      name="email"
                      value={formik.values.email}
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      isInvalid={formik.touched.email && formik.errors.email}
                      className="border-start-0 border-end-0 border-top-0 rounded-0"
                    />
                    <Form.Control.Feedback type="invalid">
                      {formik.errors.email}
                    </Form.Control.Feedback>
                  </FloatingLabel>

                  <FloatingLabel controlId="password" label="Password" className="mb-4">
                    <Form.Control
                      type="password"
                      placeholder="Password"
                      name="password"
                      value={formik.values.password}
                      onChange={formik.handleChange}
                      onBlur={formik.handleBlur}
                      isInvalid={formik.touched.password && formik.errors.password}
                      className="border-start-0 border-end-0 border-top-0 rounded-0"
                    />
                    <Form.Control.Feedback type="invalid">
                      {formik.errors.password}
                    </Form.Control.Feedback>
                  </FloatingLabel>

                  <Button 
                    type="submit" 
                    variant="primary" 
                    size="lg" 
                    className="w-100 mb-3"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Signing in...
                      </>
                    ) : (
                      <>
                        <FaSignInAlt className="me-2" />
                        Sign In
                      </>
                    )}
                  </Button>

                  <div className="text-center mt-4">
                    <p className="text-muted">
                      Don't have an account?{' '}
                      <Link to="/register" className="text-decoration-none fw-bold">
                        <FaUserPlus className="me-1" />
                        Sign Up
                      </Link>
                    </p>
                  </div>
                </Form>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default Login;