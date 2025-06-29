# To-Do List with Authentication
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00.svg?style=flat&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=flat&logo=spring&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=flyway&logoColor=white)
![MapStruct](https://img.shields.io/badge/MapStruct-E94E1B)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=black)
[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](./LICENSE)
### Challenge
A RESTful API for personal task management with JWT authentication, user roles, Swagger/OpenAPI documentation, and global exception handling.
#### Features
- User authentication with login/password and JWT token generation
- User and admin registration
- Task CRUD with ownership control
- Mark tasks as completed
- Filter by completed / pending tasks
- Global exception handling with standard response format
- Full API documentation via Swagger
- Role-based authorization (ADMIN and USER)
#### Class Diagram
``` mermaid
classDiagram
class User {
-UUID id
-String username
-String password
-String role
}
class Task {
-UUID id
-String title
-String description
-LocalDate dueDate
-Boolean completed
-User user
}
User "1"--> "*" Task
```
#### Entity Relationship Diagram
``` mermaid
erDiagram
USERS ||--o{ TASKS : contain
USERS {
UUID id PK
VARCHAR(30) username
VARCHAR(200) password
String role
}
TASKS {
UUID id PK
VARCHAR(100) title
VARCHAR(500) description
Timestamp dueDate
Boolean completed
UUID user_id FK
}
```
#### Package Structure
├── controller<br/>
├── domain<br/>
├── exception<br/>
├── infra<br/>
├── repository<br/>
└── service
#### Authentication and Authorization
- **Login** returns a JWT token valid for 2 hours
- Protected endpoints require the token in `Authorization: Bearer <token>`
- `ADMIN` users have additional permissions (e.g., registering other admins)
#### Swagger Documentation
Once the application is running, access:
http://localhost:8080/docs
#### Testing
- Unit and integration tests for controllers
- Coverage of expected HTTP statuses (200, 400, 401, 403, 404, etc.)
## Running the Project
1. Clone the repository:
```bash
git clone https://github.com/samanthamaiaduarte/todo-api.git
```
2. Create a PostgreSQL database (e.g., `todoapi`)
3. Configure your database settings in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
```
4. Run the application
```bash
./mvnw spring-boot:run
```
The API will be available at http://localhost:8080
## Deployment
You can deploy this project on:
- Render.com
- Railway
- Fly.io
- Heroku (using JDK 21 buildpack)
  ⚠️ Be sure to configure environment variables like the token secret and database credentials on the platform's settings.
## Contributing
Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request to the repository.
When contributing to this project, please follow the existing code style, [commit conventions](https://www.conventionalcommits.org/en/v1.0.0/), and submit your changes in a separate branch.

---
### Author
Made with ❤️ by Samantha Maia Duarte
contact@samanthamaiaduarte.com