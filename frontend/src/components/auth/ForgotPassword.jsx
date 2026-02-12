import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { authService } from '../../services/authService';
import { toast } from 'react-hot-toast';
import { Container, Row, Col, Card, Form, Button, FloatingLabel, Spinner } from 'react-bootstrap';
import { FaEnvelope, FaArrowLeft } from 'react-icons/fa';

const ForgotPassword = () => {
  const [loading, setLoading] = useState(false);

  const formik = useFormik({
    initialValues: { email: '' },
    validationSchema: Yup.object({
      email: Yup.string().email('Invalid email address').required('Email is required'),
    }),
    onSubmit: async (values) => {
      setLoading(true);
      const result = await authService.forgotPassword(values.email);
      if (result.success) {
        toast.success('If an account exists, a reset link has been sent!');
      } else {
        toast.error(result.error);
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
                    <FaEnvelope size={40} className="text-primary" />
                  </div>
                  <h2 className="mt-3 fw-bold">Forgot Password</h2>
                  <p className="text-muted">We'll send you a link to reset your password</p>
                </div>

                <Form onSubmit={formik.handleSubmit}>
                  <FloatingLabel label="Email Address" className="mb-4">
                    <Form.Control
                      type="email"
                      placeholder="name@example.com"
                      {...formik.getFieldProps('email')}
                      isInvalid={formik.touched.email && formik.errors.email}
                      className="border-start-0 border-end-0 border-top-0 rounded-0"
                    />
                    <Form.Control.Feedback type="invalid">{formik.errors.email}</Form.Control.Feedback>
                  </FloatingLabel>

                  <Button type="submit" variant="primary" size="lg" className="w-100 mb-3" disabled={loading}>
                    {loading ? <Spinner animation="border" size="sm" className="me-2" /> : 'Send Reset Link'}
                  </Button>
                  
                  <div className="text-center mt-3">
                    <Link to="/login" className="text-decoration-none text-muted">
                      <FaArrowLeft className="me-2" /> Back to Login
                    </Link>
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

export default ForgotPassword;