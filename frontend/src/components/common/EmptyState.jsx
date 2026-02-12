import './common.css';

const EmptyState = ({ message, icon = '📭', actionButton }) => {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">{icon}</div>
      <h3 className="empty-state-title">No Data Found</h3>
      <p className="empty-state-message">{message || 'No items to display.'}</p>
      {actionButton && (
        <div className="empty-state-action">
          {actionButton}
        </div>
      )}
    </div>
  );
};

export default EmptyState;