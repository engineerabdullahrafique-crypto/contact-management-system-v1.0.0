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

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const pageSize = 10;

  const fetchContacts = async (page, query = '') => {
    try {
      setLoading(true);
      setError('');

      let result;
      if (query.trim()) {
        result = await contactService.searchContacts(query);
      } else {
        result = await contactService.getAllContacts(page, pageSize);
      }

      if (result.success) {
        const data = query.trim() ? result.data : (result.data.content || []);

        setContacts(data);
        setFilteredContacts(data);

        if (!query.trim()) {
          setTotalPages(result.data.totalPages || 0);
          setTotalElements(result.data.totalElements || 0);
        } else {
          setTotalElements(data.length);
          setTotalPages(0);
        }
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
    fetchContacts(currentPage, searchTerm);
  }, [currentPage, searchTerm]);

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

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
      window.scrollTo(0, 0);
    }
  };

  if (loading) return <LoadingSpinner fullPage text="Loading contacts..." />;

  return (
    <div className="contact-list-container">
      <div className="contact-list-header">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <div>
            <h2>Contacts</h2>
            <p className="text-muted">Total contacts: {totalElements}</p>
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
        <>
          <div className="row">
            {filteredContacts.map(contact => (
              <div key={contact.id} className="col-md-6 col-lg-4 mb-4">
                <div className="card h-100 shadow-sm">
                  <div className="card-body">
                    <h5 className="card-title">{contact.firstName} {contact.lastName}</h5>

                    <p className="card-text mb-1">
                      <small className="text-muted">📧 {contact.emailWork || contact.emailPersonal || 'No email'}</small>
                    </p>

                    <p className="card-text">
                      <small className="text-muted">📞 {contact.phonePersonal || contact.phoneWork || 'No phone'}</small>
                    </p>

                    <div className="mt-3">
                      <Link to={`/dashboard/contacts/${contact.id}`} className="btn btn-sm btn-outline-primary me-2">View</Link>
                      <Link to={`/dashboard/contacts/${contact.id}/edit`} className="btn btn-sm btn-outline-secondary">Edit</Link>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/*Pagination controls */}
          {totalPages > 1 && (
            <nav aria-label="Page navigation" className="mt-4">
              <ul className="pagination justify-content-center">
                <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => handlePageChange(currentPage - 1)}
                  >
                    Previous
                  </button>
                </li>

                {[...Array(totalPages).keys()].map(pageIndex => (
                  <li
                    key={pageIndex}
                    className={`page-item ${currentPage === pageIndex ? 'active' : ''}`}
                  >
                    <button
                      className="page-link"
                      onClick={() => handlePageChange(pageIndex)}
                    >
                      {pageIndex + 1}
                    </button>
                  </li>
                ))}

                <li className={`page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => handlePageChange(currentPage + 1)}
                  >
                    Next
                  </button>
                </li>
              </ul>
            </nav>
          )}
        </>
      )}
    </div>
  );
};

export default ContactList;