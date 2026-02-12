import React, { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { authService } from '../../services/authService';
import { toast } from 'react-hot-toast';
import { Container, Row, Col, Card, Form, Button, FloatingLabel, Spinner } from 'react-bootstrap';
import { FaLock } from 'react-icons/fa';

const ResetPassword = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);

  const formik = useFormik({
    initialValues: { password: '', confirmPassword: '' },
    validationSchema: Yup.object({
      password: Yup.string().min(6, 'At least 6 characters').required('Required'),
      confirmPassword: Yup.string().oneOf([Yup.ref('password')], 'Passwords must match').required('Required')
    }),
    onSubmit: async (values) => {
      if (!token) {
        toast.error("Invalid or missing reset token");
        return;
      }
      setLoading(true);
      const result = await authService.resetPassword(token, values.password);
      if (result.success) {
        toast.success('Password updated! Please login.');
        navigate('/login');
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
                  <FaLock size={40} className="text-primary" />
                  <h2 className="mt-3 fw-bold">New Password</h2>
                  <p className="text-muted">Enter your new secure password</p>
                </div>
                <Form onSubmit={formik.handleSubmit}>
                  <FloatingLabel label="New Password" size="sm" className="mb-3">
                    <Form.Control type="password" name="password" {...formik.getFieldProps('password')} isInvalid={formik.touched.password && formik.errors.password} />
                  </FloatingLabel>
                  <FloatingLabel label="Confirm Password" size="sm" className="mb-4">
                    <Form.Control type="password" name="confirmPassword" {...formik.getFieldProps('confirmPassword')} isInvalid={formik.touched.confirmPassword && formik.errors.confirmPassword} />
                  </FloatingLabel>
                  <Button type="submit" variant="primary" size="lg" className="w-100" disabled={loading || !token}>
                    {loading ? <Spinner animation="border" size="sm" /> : 'Update Password'}
                  </Button>
                </Form>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};

export default ResetPassword;