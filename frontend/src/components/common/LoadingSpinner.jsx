import './common.css';

const LoadingSpinner = ({ size = 'md', text = 'Loading...', fullPage = false }) => {
  const sizeClass = {
    sm: 'spinner-border-sm',
    md: '',
    lg: 'spinner-border-lg'
  }[size];

  const content = (
    <div className="loading-spinner">
      <div className={`spinner-border text-primary ${sizeClass}`} role="status">
        <span className="visually-hidden">Loading...</span>
      </div>
      {text && <p className="loading-text mt-2">{text}</p>}
    </div>
  );

  if (fullPage) {
    return <div className="loading-screen">{content}</div>;
  }

  return content;
};

export default LoadingSpinner;