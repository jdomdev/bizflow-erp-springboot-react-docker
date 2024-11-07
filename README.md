# Expense Note App

Expense Note App is a backend monolithic application developed using **Spring Boot** and **Java JDK 11**, recently upgraded to **JDK 17**. It serves as an expense management system that allows employees, who may or may not be system users, to manage their expense notes and payroll data. The system includes a **RESTful API** for handling requests, which can be tested through **Postman**.

## Key Features

- **Monolithic Architecture**: The application follows a monolithic architecture for simplicity and centralized control.
- **RESTful API**: Endpoints are exposed for interaction, currently being tested with **Postman**.
- **DTO & DAO Patterns**: The application utilizes **Data Transfer Object (DTO)** and **Data Access Object (DAO)** patterns to streamline data processing and persistence.
- **Role-Based Access Control (RBAC)**:
  - Two roles: `ROLE_ADMIN` and `ROLE_USER`.
  - **JWT Authentication**: Users authenticate through JSON Web Tokens (JWT), ensuring secure access based on roles.
  - Employees may or may not have user accounts within the system.
- **Persistence**: Data is stored in a **PostgreSQL** database, supporting the persistence of employee records, system users, and payroll information.
- **Java and Spring Boot Versions**: 
  - Initially developed with **JDK 11** and **Spring Boot 2**, with a planned migration to **Spring Boot 3**.

## Tech Stack

- **Java JDK**: Initially built on **JDK 11**, migrated to **JDK 17**.
- **Spring Boot**: Version 2, planning migration to **Spring Boot 3**.
- **Database**: PostgreSQL for reliable data storage.
- **JWT**: Secure authentication and authorization mechanism.
- **API Testing Tool**: Postman for manual testing of API endpoints.

## Installation and Setup

1. **Clone the repository**:
   git clone https://github.com/jdomdev/expense-note-app-springboot.git
   cd expense-note-app-springboot
   

2. **Configure PostgreSQL**:
   - Ensure a PostgreSQL instance is running.
   - Create a database for the application and configure connection properties in `application.properties`.

3. **Run the Application**:
   - Use Maven to compile and run the application:
     mvn spring-boot:run
     

## API Documentation

The **Expense Note App** includes RESTful endpoints to handle requests for:
- **Employees**: Adding, viewing, and managing employee data.
- **Users**: Managing users of the expense system with assigned roles.
- **Payroll**: Handling payroll records and related operations.

Documentation and endpoint details can be accessed via Postman for detailed request and response formats.

## Authentication and Authorization

The application uses **JWT** for secure authentication. Access is role-based, with the following roles:
- `ROLE_ADMIN`: Full access to system resources and management capabilities.
- `ROLE_USER`: Limited access, with permissions scoped to their own data.

## Future Enhancements

- **Migration to Spring Boot 3**: Planned update to leverage new features and optimizations.
- **Frontend Integration**: Upcoming features include a frontend interface for easier interaction.
- **Extended Testing**: Adding JUnit tests to enhance API stability and reliability.

## Contributing

Feel free to submit issues and pull requests. For major changes, please discuss them via issues or email with the maintainers.

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](./LICENSE) file for more details.
With this update, it is specified that the application is under the **GPLv3** license. This informs contributors and users that they can modify and redistribute the software under the terms of this license.
