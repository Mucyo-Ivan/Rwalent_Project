# 🔐 Rwalent – Backend

**Rwalent** is a secure and scalable backend API built with **Java Spring Boot**, providing RESTful services for user management, authentication, and protected resources. It integrates **JWT authentication**, **role-based access control**, and connects to a **MySQL database**.

This backend is designed to support the **Rwalent frontend** and can be easily extended with new features.

---

## ⚙️ Technologies Used

| Technology        | Description                          |
|-------------------|--------------------------------------|
| Java 17+          | Programming language                 |
| Spring Boot       | Backend framework                    |
| Spring Security   | Authentication & authorization       |
| JWT               | Token-based user sessions            |
| MySQL             | Relational database                  |
| JPA (Hibernate)   | ORM for database interaction         |
| Maven             | Dependency management                |
| Lombok            | Boilerplate code reduction           |
| Swagger (OpenAPI) | API documentation                    |

---

## 📂 Project Structure

rwalent-backend/
│
├── src/main/java/com/rwalent/
│ ├── config/ # Security, CORS, JWT config
│ ├── controller/ # REST API controllers
│ ├── dto/ # Data Transfer Objects
│ ├── entity/ # JPA entities
│ ├── exception/ # Custom exception handling
│ ├── repository/ # Spring Data JPA interfaces
│ ├── service/ # Business logic
│ └── RwalentApplication.java
│
├── src/main/resources/
│ ├── application.properties
│ └── schema.sql / data.sql (optional)
│
├── pom.xml
└── README.md

yaml
Copy
Edit

---

## 🚀 Features

- 🔐 **JWT-based Authentication**
- 🧑 **User Registration and Login**
- 🔐 **Role-Based Access Control** (`ROLE_USER`, `ROLE_ADMIN`)
- 📄 **Secured Endpoints** (accessible only with a valid token)
- 🌐 **Cross-Origin Resource Sharing (CORS)** config
- 📦 **MySQL Database Integration**
- 📘 **Swagger API Docs** for testing endpoints

---

## 🧪 API Endpoints

| Method | Endpoint         | Access        | Description             |
|--------|------------------|---------------|-------------------------|
| POST   | `/api/auth/login`| Public        | Login and get JWT       |
| POST   | `/api/auth/register` | Public   | Register a new user     |
| GET    | `/api/user/me`   | Authenticated | Get user profile        |
| GET    | `/api/admin/dashboard` | Admin  | Admin-only access       |

> Add more endpoints as you expand your backend features.

---

## ⚙️ Configuration

### 🐬 MySQL Setup

Make sure you have MySQL installed and running.

Create a database:

```sql
CREATE DATABASE rwalent_db;
📄 application.properties
properties
Copy
Edit
spring.datasource.url=jdbc:mysql://localhost:3306/rwalent_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

spring.security.jwt.secret=your_jwt_secret_key
spring.security.jwt.expiration=3600000

server.port=8080
Replace values as needed and never commit real credentials.

▶️ Running the App
Using Maven
bash
Copy
Edit
mvn spring-boot:run
Or build and run the jar:

bash
Copy
Edit
mvn clean install
java -jar target/rwalent-*.jar
The backend will run at:
http://localhost:8080

🧪 API Documentation
After running the app, visit:

bash
Copy
Edit
http://localhost:8080/swagger-ui/index.html
Test endpoints and inspect models using Swagger UI.

🧪 Testing
Unit and integration tests are written using JUnit and Mockito.

bash
Copy
Edit
mvn test
🔐 JWT Flow
Login/Register → Backend generates a JWT

Frontend stores token in localStorage

All protected requests include:

makefile
Copy
Edit
Authorization: Bearer <JWT>
Spring Security filters and validates token

🧑‍💻 Future Enhancements
🔁 Refresh Tokens

💬 Email Notifications

📈 Admin Analytics Dashboard

🗂️ User Profiles & Avatar Upload

🔐 Two-Factor Authentication

🤝 Contributing
Fork this repository

Create a branch: git checkout -b feature/feature-name

Commit your changes: git commit -m 'Add feature'

Push to the branch: git push origin feature-name

Create a Pull Request

📝 License
This project is licensed under the MIT License. See the LICENSE file for more details.

👨‍💻 Author
Mucyo Ivan
📧 mucyoivan25@gmail.com
🌍 GitHub | LinkedIn
