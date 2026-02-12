import { Card, Badge, ButtonGroup, Button, Tooltip, OverlayTrigger } from 'react-bootstrap';
import { 
  FaUser, 
  FaEnvelope, 
  FaPhone, 
  FaEdit, 
  FaTrash, 
  FaEye,
  FaBriefcase,
  FaHome,
  FaMobile
} from 'react-icons/fa';

const ContactCard = ({ contact, onEdit, onDelete, onView }) => {
  const getInitials = (firstName, lastName) => {
    return `${firstName?.charAt(0) || ''}${lastName?.charAt(0) || ''}`.toUpperCase();
  };

  const getRandomColor = () => {
    const colors = [
      'bg-primary', 'bg-secondary', 'bg-success', 
      'bg-danger', 'bg-warning', 'bg-info', 'bg-dark'
    ];
    return colors[Math.floor(Math.random() * colors.length)];
  };

  const renderTooltip = (text) => (
    <Tooltip id="button-tooltip">{text}</Tooltip>
  );

  return (
    <Card className="contact-card shadow-sm h-100">
      <Card.Body className="p-4">
        {/* Contact Header */}
        <div className="d-flex align-items-start mb-3">
          <div className={`contact-avatar ${getRandomColor()} me-3`}>
            {getInitials(contact.firstName, contact.lastName)}
          </div>
          <div className="flex-grow-1">
            <div className="d-flex justify-content-between align-items-start">
              <div>
                <h5 className="mb-1">
                  {contact.firstName} {contact.lastName}
                </h5>
                {contact.title && (
                  <Badge bg="light" text="dark" className="mb-2">
                    <FaBriefcase className="me-1" />
                    {contact.title}
                  </Badge>
                )}
              </div>
              <ButtonGroup size="sm">
                {onView && (
                  <OverlayTrigger placement="top" overlay={renderTooltip('View')}>
                    <Button variant="outline-primary" onClick={() => onView(contact)}>
                      <FaEye />
                    </Button>
                  </OverlayTrigger>
                )}
                <OverlayTrigger placement="top" overlay={renderTooltip('Edit')}>
                  <Button variant="outline-warning" onClick={onEdit}>
                    <FaEdit />
                  </Button>
                </OverlayTrigger>
                <OverlayTrigger placement="top" overlay={renderTooltip('Delete')}>
                  <Button variant="outline-danger" onClick={onDelete}>
                    <FaTrash />
                  </Button>
                </OverlayTrigger>
              </ButtonGroup>
            </div>
          </div>
        </div>

        {/* Contact Info */}
        <div className="contact-info">
          {/* Emails */}
          {(contact.emailWork || contact.emailPersonal) && (
            <div className="contact-item mb-2">
              <FaEnvelope className="text-muted me-2" />
              <div>
                {contact.emailWork && (
                  <div className="text-truncate">
                    <small className="text-muted">Work: </small>
                    {contact.emailWork}
                  </div>
                )}
                {contact.emailPersonal && (
                  <div className="text-truncate">
                    <small className="text-muted">Personal: </small>
                    {contact.emailPersonal}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Phones */}
          {(contact.phoneWork || contact.phoneHome || contact.phonePersonal) && (
            <div className="contact-item">
              <FaPhone className="text-muted me-2" />
              <div>
                {contact.phoneWork && (
                  <div className="text-truncate">
                    <FaBriefcase size={12} className="me-1 text-muted" />
                    <small className="text-muted">Work: </small>
                    {contact.phoneWork}
                  </div>
                )}
                {contact.phoneHome && (
                  <div className="text-truncate">
                    <FaHome size={12} className="me-1 text-muted" />
                    <small className="text-muted">Home: </small>
                    {contact.phoneHome}
                  </div>
                )}
                {contact.phonePersonal && (
                  <div className="text-truncate">
                    <FaMobile size={12} className="me-1 text-muted" />
                    <small className="text-muted">Mobile: </small>
                    {contact.phonePersonal}
                  </div>
                )}
              </div>
            </div>
          )}
        </div>
      </Card.Body>

      <style jsx>{`
        .contact-card {
          transition: transform 0.3s, box-shadow 0.3s;
          border: none;
          border-radius: 15px;
          overflow: hidden;
        }
        
        .contact-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }
        
        .contact-avatar {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.5rem;
          font-weight: bold;
          color: white;
        }
        
        .contact-item {
          display: flex;
          align-items: flex-start;
          padding: 4px 0;
        }
        
        .contact-item div {
          flex: 1;
          min-width: 0;
        }
      `}</style>
    </Card>
  );
};

export default ContactCard;