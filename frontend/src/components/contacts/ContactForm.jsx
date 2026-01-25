import React, { useEffect, useState } from 'react';
import { Form, Button, Row, Col, Spinner, Card, Container } from 'react-bootstrap';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { useParams, useNavigate } from 'react-router-dom';
import { contactService } from '../../services/contactService';
import { toast } from 'react-hot-toast';
import { FaSave, FaArrowLeft, FaUser, FaEnvelope, FaPhone } from 'react-icons/fa';

const ContactForm = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditing = !!id;
  const [loading, setLoading] = useState(isEditing);

  const validationSchema = Yup.object({
    firstName: Yup.string().required('First name is required'),
    lastName: Yup.string().required('Last name is required'),
    emailWork: Yup.string().email('Invalid email'),
    phonePersonal: Yup.string().matches(/^[0-9+\-\s()]*$/, 'Invalid phone number'),
  });

  const formik = useFormik({
    initialValues: {
      firstName: '', lastName: '', title: '',
      emailWork: '', emailPersonal: '',
      phoneWork: '', phoneHome: '', phonePersonal: ''
    },
    validationSchema,
    onSubmit: async (values) => {
      try {
        const result = isEditing 
          ? await contactService.updateContact(id, values)
          : await contactService.createContact(values);

        if (result.success) {
          toast.success(isEditing ? 'Updated!' : 'Created!');
          navigate('/dashboard/contacts');
        } else {
          toast.error(result.error);
        }
      } catch (error) {
        toast.error('An error occurred');
      }
    }
  });

    useEffect(() => {
    if (isEditing) {
      const fetchContact = async () => {
        const result = await contactService.getContactById(id);
        if (result.success) {
          formik.setValues(result.data);
        } else {
          toast.error("Could not find contact");
          navigate('/dashboard/contacts');
        }
        setLoading(false);
      };
      fetchContact();
    }
  }, [id, formik, isEditing, navigate]);

  if (loading) return <div className="text-center mt-5"><Spinner animation="border" /></div>;

  return (
    <Container className="py-4">
      <Button variant="link" onClick={() => navigate(-1)} className="mb-3 text-decoration-none">
        <FaArrowLeft className="me-2" /> Back
      </Button>
      
      <Card className="shadow-sm">
        <Card.Body className="p-4">
          <h2 className="mb-4">
            <FaUser className="me-2 text-primary" />
            {isEditing ? 'Edit Contact' : 'Create New Contact'}
          </h2>

          <Form onSubmit={formik.handleSubmit}>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>First Name *</Form.Label>
                  <Form.Control
                    name="firstName"
                    value={formik.values.firstName}
                    onChange={formik.handleChange}
                    isInvalid={formik.touched.firstName && formik.errors.firstName}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Last Name *</Form.Label>
                  <Form.Control
                    name="lastName"
                    value={formik.values.lastName}
                    onChange={formik.handleChange}
                    isInvalid={formik.touched.lastName && formik.errors.lastName}
                  />
                </Form.Group>
              </Col>
            </Row>

            <Form.Group className="mb-3">
              <Form.Label>Work Email</Form.Label>
              <Form.Control
                name="emailWork"
                value={formik.values.emailWork}
                onChange={formik.handleChange}
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label>Personal Phone</Form.Label>
              <Form.Control
                name="phonePersonal"
                value={formik.values.phonePersonal}
                onChange={formik.handleChange}
              />
            </Form.Group>

            <div className="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
              <Button variant="secondary" onClick={() => navigate('/dashboard/contacts')}>Cancel</Button>
              <Button type="submit" variant="primary" disabled={formik.isSubmitting}>
                {formik.isSubmitting ? <Spinner size="sm" /> : <><FaSave className="me-2"/> Save Contact</>}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ContactForm;