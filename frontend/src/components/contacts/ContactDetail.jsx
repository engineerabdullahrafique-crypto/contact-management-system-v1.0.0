import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Container, Card, Button, Row, Col, Spinner, Badge } from 'react-bootstrap';
import { contactService } from '../../services/contactService';
import { toast } from 'react-hot-toast';
import { FaArrowLeft, FaEdit, FaTrash, FaEnvelope, FaPhone, FaBuilding, FaUser } from 'react-icons/fa';

const ContactDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [contact, setContact] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchContactData = async () => {
      try {
        setLoading(true);
        const result = await contactService.getContactById(id);
        if (result.success) {
          setContact(result.data);
        } else {
          toast.error(result.error || 'Contact not found');
          navigate('/dashboard/contacts');
        }
      } catch (err) {
        toast.error('Failed to load contact details');
      } finally {
        setLoading(false);
      }
    };

    fetchContactData();
  }, [id, navigate]);

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this contact?')) {
      const result = await contactService.deleteContact(id);
      if (result.success) {
        toast.success('Contact deleted successfully');
        navigate('/dashboard/contacts');
      } else {
        toast.error(result.error);
      }
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '300px' }}>
        <Spinner animation="border" variant="primary" />
      </div>
    );
  }

  if (!contact) return null;

  return (
    <Container className="py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <Button variant="outline-secondary" onClick={() => navigate('/dashboard/contacts')}>
          <FaArrowLeft className="me-2" /> Back to List
        </Button>
        <div>
          <Link to={`/dashboard/contacts/${id}/edit`} className="btn btn-primary me-2">
            <FaEdit className="me-2" /> Edit
          </Link>
          <Button variant="danger" onClick={handleDelete}>
            <FaTrash className="me-2" /> Delete
          </Button>
        </div>
      </div>

      <Card className="shadow-sm border-0">
        <Card.Body className="p-4">
          <Row>
            <Col md={4} className="text-center border-end">
              <div className="bg-light rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style={{ width: '120px', height: '120px' }}>
                <FaUser size={60} className="text-secondary" />
              </div>
              <h3>{contact.firstName} {contact.lastName}</h3>
              <p className="text-muted">{contact.title || 'No Title'}</p>
              <Badge bg="info" className="px-3 py-2">Contact ID: {id}</Badge>
            </Col>
            
            <Col md={8} className="ps-md-5">
              <h5 className="mb-4 text-primary">Contact Information</h5>
              
              <div className="mb-4">
                <p className="text-muted mb-1"><FaEnvelope className="me-2"/> Email Addresses</p>
                <div className="ms-4">
                  {contact.emailWork && <div><strong>Work:</strong> {contact.emailWork}</div>}
                  {contact.emailPersonal && <div><strong>Personal:</strong> {contact.emailPersonal}</div>}
                  {!contact.emailWork && !contact.emailPersonal && <span className="text-muted">None provided</span>}
                </div>
              </div>

              <div className="mb-4">
                <p className="text-muted mb-1"><FaPhone className="me-2"/> Phone Numbers</p>
                <div className="ms-4">
                  {contact.phoneWork && <div><strong>Work:</strong> {contact.phoneWork}</div>}
                  {contact.phonePersonal && <div><strong>Mobile:</strong> {contact.phonePersonal}</div>}
                  {contact.phoneHome && <div><strong>Home:</strong> {contact.phoneHome}</div>}
                </div>
              </div>

              <div>
                <p className="text-muted mb-1"><FaBuilding className="me-2"/> Additional Info</p>
                <div className="ms-4">
                  <p><strong>Full Name:</strong> {contact.firstName} {contact.lastName}</p>
                </div>
              </div>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default ContactDetail;