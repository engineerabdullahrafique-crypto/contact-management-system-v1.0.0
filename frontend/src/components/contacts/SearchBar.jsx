import React, { useState } from 'react';
import './contacts.css';

const SearchBar = ({ onSearch, placeholder = "Search contacts..." }) => {
  const [searchTerm, setSearchTerm] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(searchTerm);
  };

  const handleClear = () => {
    setSearchTerm('');
    onSearch('');
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSearch} className="search-form">
        <div className="input-group">
          <span className="input-group-text">
            <i className="bi bi-search"></i>
          </span>
          <input
            type="text"
            className="form-control"
            placeholder={placeholder}
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          {searchTerm && (
            <button
              type="button"
              className="btn btn-outline-secondary"
              onClick={handleClear}
            >
              <i className="bi bi-x"></i>
            </button>
          )}
          <button
            type="submit"
            className="btn btn-primary"
          >
            Search
          </button>
        </div>
      </form>
      
      <div className="search-filters">
        <div className="form-check form-check-inline">
          <input
            className="form-check-input"
            type="checkbox"
            id="searchName"
            defaultChecked
          />
          <label className="form-check-label" htmlFor="searchName">
            Name
          </label>
        </div>
        <div className="form-check form-check-inline">
          <input
            className="form-check-input"
            type="checkbox"
            id="searchEmail"
            defaultChecked
          />
          <label className="form-check-label" htmlFor="searchEmail">
            Email
          </label>
        </div>
        <div className="form-check form-check-inline">
          <input
            className="form-check-input"
            type="checkbox"
            id="searchPhone"
            defaultChecked
          />
          <label className="form-check-label" htmlFor="searchPhone">
            Phone
          </label>
        </div>
      </div>
    </div>
  );
};

export default SearchBar;