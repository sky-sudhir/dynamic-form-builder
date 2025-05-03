# Dynamic Form Builder

A modern, full-stack web application for creating, managing, and analyzing dynamic forms with real-time analytics.

## 🚀 Features

- User Authentication & Authorization
- Dynamic Form Creation and Management
- Drag-and-Drop Form Builder
- Real-time Form Analytics
- Data Export Functionality
- Interactive Dashboards
- Word Cloud Visualization
- Response Analytics

## 🛠️ Tech Stack

### Frontend
- React (v19)
- Vite
- Redux Toolkit for state management
- TailwindCSS for styling
- DND Kit for drag-and-drop functionality
- Recharts & D3.js for visualizations
- React Hook Form for form handling
- Axios for API calls

### Backend
- Spring Boot
- Spring Security with JWT
- Redis for caching
- Async processing support
- Swagger for API documentation
- Gradle for build management

## 🏃‍♂️ Getting Started

### Prerequisites
- Node.js (v16 or higher)
- Java 17 or higher
- Redis Server
- MySQL Database

### Installation

1. Clone the repository:
```bash
git clone [repository-url]
```

2. Backend Setup:
```bash
cd BACKEND
./gradlew build
./gradlew bootRun
```

3. Frontend Setup:
```bash
cd frontend
npm install
npm run dev
```

## 🔑 Demo Access
- Email: sky875025@gmail.com
- Password: demo@123

## 📝 API Documentation
Access the Swagger API documentation at: `https://form-generator.duckdns.org/swagger-ui.html`

## 🛣️ Routes Documentation

### Frontend Routes
- `/auth/*` - Authentication routes
  - `/auth/login` - User login
  - `/auth/register` - User registration
  - `/auth/verify` - OTP verification
- `/form/*` - Public form routes
  - `/form/:formId` - View public form
  - `/form/submit/:formId` - Submit form response
- `/home/*` - Protected dashboard routes
  - `/home/dashboard` - Main dashboard
  - `/home/forms` - Form management
  - `/home/analytics/:formId` - Form analytics

### Backend API Endpoints

#### Authentication (`/api/users`)
- `POST /api/users/register/request-otp` - Request OTP for registration
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login

#### Forms (`/api/forms`)
- `POST /api/forms/create` - Create new form
- `GET /api/forms` - Get all forms
- `GET /api/forms/public/{formId}` - Get public form by ID
- `GET /api/forms/{formUid}` - Get form by UID
- `GET /api/forms/access/{formUid}` - Check form access
- `POST /api/forms/validate` - Validate form
- `DELETE /api/forms/{formUid}` - Delete form

#### Responses (`/api/responses`)
- `POST /api/responses/submit/{formId}` - Submit form response

#### Dashboard (`/api/dashboard`)
- `GET /api/dashboard/analytics/{formId}` - Get form analytics
- `GET /api/dashboard/export/excel/{formId}` - Export responses to Excel
- `GET /api/dashboard/forms/{formId}/user-responses` - Get user responses

## 🛠️ Environment Setup

### Backend Environment Variables
- `SPRING_DATASOURCE_URL`: MySQL database URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation
- `REDIS_HOST`: Redis server host
- `REDIS_PORT`: Redis server port

## 🔐 Credentials

### Admin Access
- **Email:** sky875025@gmail.com
- **Password:** demo@123
- **Form-Password:** 1234


## 🤝 Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b main`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin main`)
5. Open a Pull Request
