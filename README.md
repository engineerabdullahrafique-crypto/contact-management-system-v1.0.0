# Contact Management System

A full-stack contact management application built with **Spring Boot 3.5** (backend) and **React 19** (frontend). Users can register, log in, and perform CRUD operations on their contacts. The project includes comprehensive testing, code quality analysis with SonarQube, and is ready for CI/CD integration.

---

## Features

- **User Authentication**
  - Register with email/phone and password
  - Login with JWT token authentication (JJWT 0.13.0)
  - Change password

- **Contact Management**
  - View contacts in a paginated list
  - Search contacts by first name or last name
  - Create, update, and delete contacts
  - Contact details include:
    - First name, last name, title
    - Work/personal email addresses
    - Work/home/mobile phone numbers

- **Quality & Testing**
  - Unit tests for service and repository layers (JUnit 5, Mockito)
  - Integration tests for controllers
  - Code coverage with JaCoCo (backend) and Jest (frontend)
  - SonarQube integration for static code analysis

- **Modern UI**
  - Responsive design built with React 19 and Bootstrap 5
  - Form validation with Formik + Yup
  - Toast notifications for user feedback (React Hot Toast)

---

## Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.5.9**
- **Spring Security** with JWT authentication
- **Spring Data JPA** (Hibernate)
- **SQL Server** (Windows Authentication)
- **Maven**
- **JUnit 5**, **Mockito**, **JaCoCo**
- **SonarQube** for code quality
- **Lombok 1.18.42**

### Frontend
- **React 19.2.3**
- **React Router 7.13.0**
- **Bootstrap 5.3.8** + **React-Bootstrap**
- **Formik** & **Yup** for form handling
- **Axios** for API calls
- **React Icons**
- **React Hot Toast** for notifications
- **Jest** for testing, **ESLint** / **Prettier** for code style

---

## Prerequisites

- **Java 17** or later
- **Node.js 18** or later (Required for React 19)
- **Maven 3.8+**
- **SQL Server** (with Windows Authentication enabled)
- **SonarQube** (optional, for local analysis)

---

## Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/engineerabdullahrafique-crypto/contact-management-system-v1.0.0.git
cd contact-management-system
```

### 2. Backend Setup
**a. Configure Database**
Create a database named `contactdb3` in SQL Server.
Update `backend/src/main/resources/application.properties` with the following configuration (uses Windows integrated security):

```properties
# SQL Server Connection with Integrated Security
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=contactdb3;integratedSecurity=true;encrypt=true;trustServerCertificate=true
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# Server port
server.port=8080

# JWT Secret (should be at least 64 characters)
jwt.secret=my-very-long-and-secure-secret-key-that-must-be-at-least-64-characters-long-12345
```

> **Note:** Integrated security uses the Windows account under which the application runs. Ensure your SQL Server is configured for Windows Authentication and that the account has access to the `contactdb3` database. The username and password fields are left empty because authentication is handled by Windows.

**b. Build and Run**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The backend will start at `http://localhost:8080`.

### 3. Frontend Setup
**a. Install Dependencies**
```bash
cd frontend
npm install
```

**b. Configure Environment**
Create a `.env` file in the frontend folder:
```
REACT_APP_API_URL=http://localhost:8080
```

**c. Start the Development Server**
```bash
npm start
```
The frontend will open at `http://localhost:3000`.

---

## Running Tests

### Backend Tests
```bash
cd backend
mvn test
# For coverage report
mvn clean test jacoco:report
# Open target/site/jacoco/index.html
```

### Frontend Tests
```bash
cd frontend
npm test
```

---

## API Documentation
Key Endpoints:

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get token |
| GET | `/api/contacts` | Get paginated contacts |
| POST | `/api/contacts` | Create a contact |
| PUT | `/api/contacts/{id}` | Update a contact |
| DELETE | `/api/contacts/{id}` | Delete a contact |
| GET | `/api/users/profile` | Get current user profile |
| PUT | `/api/users/change-password` | Change password |

---

## Project Structure
```
contact-management-system/
├── backend/                 # Spring Boot application
│   ├── src/
│   │   ├── main/java/com/ab/cmsBackend/
│   │   │   ├── config/      # Security, JWT
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── dto/         # Data transfer objects
│   │   │   ├── entity/      # JPA entities
│   │   │   ├── exception/   # Global exception handler
│   │   │   ├── repository/  # Spring Data JPA repositories
│   │   │   └── service/     # Business logic
│   │   └── resources/       # application.properties
│   └── pom.xml
│
├── frontend/                # React application
│   ├── public/
│   ├── src/
│   │   ├── components/      # UI components (auth, contacts, layout)
│   │   ├── services/        # API calls (axios)
│   │   ├── context/         # Auth context
│   │   ├── utils/           # Helpers, validation
│   │   ├── App.jsx
│   │   └── index.jsx
│   ├── .env
│   ├── package.json
│   └── sonar-project.properties
│
└── README.md
```
