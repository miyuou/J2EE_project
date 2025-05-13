# Auction System

A Spring Boot-based auction system that allows users to create, bid on, and manage auctions with integrated payment processing.

## Features

- User registration and authentication
- Role-based access control (USER, ADMIN)
- Auction creation and management
- Real-time bidding system
- Payment processing
- Admin dashboard
- Responsive UI with Bootstrap

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- MySQL 8
- Thymeleaf
- Bootstrap 5
- Maven/Gradle

## Prerequisites

- Java 17 or higher
- MySQL 8
- Maven or Gradle
- IDE (IntelliJ IDEA, Eclipse, etc.)

## Setup Instructions

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd auction-system
   ```

2. Create MySQL database:
   ```sql
   CREATE DATABASE auction_db;
   ```

3. Configure database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/auction_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. Build the project:
   ```bash
   mvn clean install
   ```

5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`

## Default Users

- Admin:
  - Username: admin
  - Password: admin
  - Role: ADMIN

## Usage Guide

### User Registration

1. Click "Register" on the homepage
2. Fill in the registration form
3. Submit to create your account

### Creating an Auction

1. Log in to your account
2. Click "Create Auction"
3. Fill in auction details:
   - Title
   - Description
   - Starting price
   - End date
   - Upload image (optional)
4. Submit to create the auction

### Bidding

1. Browse active auctions
2. Click on an auction to view details
3. Enter your bid amount
4. Submit your bid

### Payment Processing

1. After winning an auction, go to "My Payments"
2. Select the payment method
3. Complete the payment process

### Admin Features

1. Log in as admin
2. Access admin dashboard
3. Manage users, auctions, and payments
4. View system statistics

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - User login

### Auctions
- GET `/api/auctions` - List all auctions
- POST `/api/auctions` - Create new auction
- GET `/api/auctions/{id}` - Get auction details
- PUT `/api/auctions/{id}` - Update auction
- DELETE `/api/auctions/{id}` - Delete auction

### Bids
- POST `/api/auctions/{id}/bid` - Place bid
- GET `/api/auctions/{id}/bids` - Get auction bids

### Payments
- GET `/api/payments` - List user payments
- POST `/api/payments` - Create payment
- GET `/api/payments/{id}` - Get payment details
- PUT `/api/payments/{id}` - Update payment
- DELETE `/api/payments/{id}` - Delete payment

## Security

- Role-based access control
- Password encryption
- CSRF protection
- XSS protection
- Secure session management

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 