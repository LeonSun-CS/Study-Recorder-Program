# Custom-Built Academic Note Management System

![Logo](my_logo.png) <!-- Assuming my_logo.png is the desired logo -->

This project is a comprehensive, full-stack academic note management system designed and implemented from the ground up. It provides a platform for users to create, manage, and organize their academic notes, incorporating features for rich content display and secure access.

**Live Demo:** [https://www.leon-sun.com](https://www.leon-sun.com)

---

## Features

*   **Note Management:** Full CRUD (Create, Read, Update, Delete) operations for notes.
*   **Course & Term Organization:** Manage notes organized by academic terms and courses.
*   **Rich Content Support:**
    *   Code syntax highlighting using **Prism.js**.
    *   LaTeX mathematical notation rendering via **MathJax**.
    *   Responsive web design using **Bootstrap**.
*   **User Authentication:** Secure login system for user access control.
*   **File Uploads:** Functionality to attach files to notes, with server-side constraints.
*   **Admin Interface:** Separate functionalities for administrative tasks (implied by `AdminController`).
*   **Security:**
    *   User session management using **Redis**.
    *   Bot protection using **Cloudflare Turnstile**.
    *   Secure deployment behind an **Nginx** reverse proxy.

---

## Technology Stack

This project utilizes a robust stack of technologies across the backend, frontend, and infrastructure:

**Backend:**

*   **Language:** Java 17
*   **Framework:** Spring Framework 5.3.22 (MVC, JDBC, Transactions, Aspects)
*   **Templating Engine:** Thymeleaf 3.0.11
*   **Database:** MySQL 9.0.0
*   **Data Mapper:** MyBatis 3.4.5 / MyBatis-Spring 1.3.1
*   **Database Connection Pooling:** Druid 1.2.23
*   **In-Memory Data Store:** Redis (using Jedis 5.1.3 client) for session management
*   **File Handling:** Apache Commons FileUpload 1.3.1
*   **JSON Processing:** Jackson Databind 2.17.1
*   **Logging:** SLF4j + Logback
*   **Build Tool:** Apache Maven
*   **Servlet Container:** (Requires a Servlet 2.5 compatible container like Tomcat)

**Frontend:**

*   **Framework/Library:** Bootstrap
*   **Syntax Highlighting:** Prism.js
*   **LaTeX Rendering:** MathJax
*   **Templating:** Thymeleaf (integrates with Spring on the backend)

**Infrastructure:**

*   **Web Server/Reverse Proxy:** Nginx
*   **Database Server:** MySQL Server
*   **Caching/Session Server:** Redis Server
*   **Operating System:** Ubuntu Server (Self-hosted)
*   **Security:** Cloudflare Turnstile
*   **Networking:** Dynamic DNS (DDNS) for consistent availability

---

## Architecture Overview

The application follows a standard Model-View-Controller (MVC) pattern facilitated by the Spring Framework.

1.  **Client Request:** User interacts with the frontend (rendered by Thymeleaf).
2.  **Nginx:** Acts as a reverse proxy, potentially handling SSL termination and load balancing, forwarding requests to the application server.
3.  **Spring Application (ClientEnd WAR):**
    *   `Controllers` handle incoming HTTP requests.
    *   `Interceptors` (like `AdminAuthWallInterceptor`, `CaptchaInterceptor`) manage security and pre-processing checks.
    *   `Services` encapsulate business logic.
    *   `DAOs` (Data Access Objects) interact with the database using MyBatis.
    *   `CommonUtils` module provides shared POJOs (Plain Old Java Objects) like `Note`, `Course`, `User`, etc.
4.  **Database (MySQL):** Persists application data (users, terms, courses, notes).
5.  **Redis:** Stores user session information for authentication state management.
6.  **Thymeleaf:** Renders dynamic HTML views on the server-side, integrating data from the backend.
7.  **Frontend Libraries (Bootstrap, Prism, MathJax):** Enhance the user interface and content rendering in the browser.

**Deployment:**

The application is packaged as a WAR file (`ClientEnd.war`) deployed to a servlet container (e.g., Apache Tomcat) running on a self-hosted Ubuntu server. Nginx acts as a reverse proxy in front of the servlet container. A DDNS service ensures the application remains accessible despite potential changes in the server's public IP address.

---

## Getting Started (Conceptual)

1.  **Prerequisites:** Java 17, Maven, MySQL Server, Redis Server, Servlet Container (e.g., Tomcat), Nginx.
2.  **Database Setup:** Configure the MySQL connection details in `ClientEnd/src/main/resources/db.properties`. Set up the required database schema.
3.  **Redis Setup:** Ensure Redis is running and accessible. Configuration might be within `spring.xml` or related files.
4.  **Build:** Navigate to the project root (`Study-Recorder-Program`) and run `mvn clean package`. This will generate `ClientEnd/target/ClientEnd.war`.
5.  **Deploy:** Deploy the `ClientEnd.war` file to your servlet container.
6.  **Nginx Configuration:** Set up Nginx as a reverse proxy to forward requests to the servlet container.
7.  **Cloudflare Turnstile:** Configure Turnstile keys within the application (likely in `spring.xml` or properties files).

*(Note: Specific configuration details for database, Redis, Cloudflare, etc., would need to be checked within the respective configuration files like `spring.xml`, `db.properties`)*
