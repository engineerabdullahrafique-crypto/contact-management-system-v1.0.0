import './common.css';

const ErrorAlert = ({ message, onClose }) => {
  if (!message) return null;

  return (
    <div className="error-alert alert alert-danger d-flex align-items-center justify-content-between">
      <div className="d-flex align-items-center">
        <i className="bi bi-exclamation-triangle-fill me-2"></i>
        <span>{message}</span>
      </div>
      {onClose && (
        <button 
          type="button" 
          className="btn-close" 
          onClick={onClose}
          aria-label="Close"
        ></button>
      )}
    </div>
  );
};

export default ErrorAlert;