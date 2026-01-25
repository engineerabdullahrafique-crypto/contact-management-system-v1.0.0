import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { contactService } from '../../services/contactService';
import EmptyState from '../common/EmptyState';
import ErrorAlert from '../common/ErrorAlert';
import LoadingSpinner from '../common/LoadingSpinner';
import SearchBar from './SearchBar';
import './contacts.css';

const ContactList = () => {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filteredContacts, setFilteredContacts] = useState([]);

  const fetchContacts = async () => {
    try {
      setLoading(true);
      setError('');
      
      const result = await contactService.getAllContacts();
      
      if (result.success) {
        // Handling spring boot page object
        const data = result.data.content || result.data;
        const contactArray = Array.isArray(data) ? data : [];
        
        setContacts(contactArray);
        setFilteredContacts(contactArray);
      } else {
        setError(result.error);
      }
    } catch (err) {
      setError('An unexpected error occurred while fetching contacts.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchContacts();
  }, []);

  useEffect(() => {
    if (searchTerm.trim() === '') {
      setFilteredContacts(contacts);
    } else {
      const filtered = contacts.filter(contact =>        
        contact.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        contact.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        contact.email?.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setFilteredContacts(filtered);
    }
  }, [searchTerm, contacts]);

  if (loading) return <LoadingSpinner fullPage text="Loading contacts..." />;

  return (
    <div className="contact-list-container">
      <div className="contact-list-header">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h2>Contacts</h2>
            <p className="text-muted">Total contacts: {contacts.length}</p>
          </div>
          <Link to="/dashboard/contacts/new" className="btn btn-primary">
            Add New Contact
          </Link>
        </div>
        <SearchBar onSearch={(term) => setSearchTerm(term)} />
      </div>

      {error && <ErrorAlert message={error} />}

      {!error && filteredContacts.length === 0 ? (
        <EmptyState 
          message={searchTerm ? `No results for "${searchTerm}"` : "No contacts found."}
          icon="📇"
        />
      ) : (
        <div className="row">
          {filteredContacts.map(contact => (            
            <div key={contact.id} className="col-md-6 col-lg-4 mb-4">
              <div className="card h-100 shadow-sm">
                <div className="card-body">
                  <h5 className="card-title">{contact.firstName} {contact.lastName}</h5>
                  <p className="card-text text-muted">{contact.email}</p>
                  <p className="card-text">{contact.phoneNumber}</p>
                  <div className="mt-3">
                    <Link to={`/dashboard/contacts/${contact.id}`} className="btn btn-sm btn-outline-primary me-2">View</Link>
                    <Link to={`/dashboard/contacts/${contact.id}/edit`} className="btn btn-sm btn-outline-secondary">Edit</Link>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ContactList;