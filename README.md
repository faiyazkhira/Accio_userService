# Accio - User Service Microservice

## Overview

The **Accio User Service** is a microservice that handles user management for the **Accio Ecommerce Application**. Built using **Spring Boot**, **MySQL**, **Hibernate/JPA**, **Eureka**, and **JWT** token-based authentication, this microservice ensures a secure, efficient, and scalable solution for managing user authentication and authorization.

Key features:
- User **Signup** with OTP verification.
- **Login** with stateless JWT authentication.
- **Role-based Authorization** with **JWT** for secured routes.
- Integration with **Java Mail Sender API** for email functionalities.

## Technologies Used
- **Spring Boot** - Core framework for building the service.
- **MySQL** - Database for storing user information.
- **Hibernate/JPA** - For ORM and database interaction.
- **Eureka** - For service discovery.
- **Java Mail Sender API** - To send emails for user verification and notifications.
- **JWT (JSON Web Token)** - For secure authentication and stateless authorization.
- **Role-Based Authorization** - For controlling access to resources based on user roles.

## Installation

### Prerequisites
Before running the application, you will need:
1. A **MySQL** database configured and running.
2. A **mail sender configuration** (for OTP and email functionalities).
3. An **application.properties** file containing database and email configurations.

### Steps to Setup

1. Clone the repository:
    ```bash
    git clone <repository-url>
    ```

2. Navigate to the project folder:
    ```bash
    cd accio-user-service
    ```

3. Add your **database** and **mail sender** properties in the **application.properties** file:
    - `spring.datasource.url` - MySQL database connection string.
    - `spring.datasource.username` - Database username.
    - `spring.datasource.password` - Database password.
    - `mail.smtp.host` - SMTP server for sending emails.
    - `mail.smtp.port` - SMTP server port.
    - `mail.smtp.username` - SMTP username (Google App password, if using Gmail).
    - `mail.smtp.password` - SMTP password (Google App password, if using Gmail).

4. Import the project as a **Maven** project in your IDE (such as **IntelliJ IDEA** or **Eclipse**).

5. Run the application:
    ```bash
    mvn spring-boot:run
    ```

## Usage

- **User Signup**: A new user can sign up by providing their details. An OTP will be sent to the provided email for verification.
- **User Login**: Once the user is verified, they can log in using their credentials, and a stateless JWT token will be issued for further requests.
- **Role-Based Authorization**: The service supports roles like `ADMIN`, `USER` for managing permissions and access to resources.

## Continuous Improvement

This microservice is still under continuous improvement and new features are being added regularly. However, it is stable and ready for use in production environments.

## Contributing

Feel free to fork the repository and submit pull requests. If you find any issues or have feature requests, please open an issue in the GitHub repo.
