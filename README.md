# URL Shortener

A simple URL shortener built with Spring Boot that lets users create and manage short links with ease.  
It includes authentication, a clean dashboard, and redirects tracking.  
Designed for fast performance, easy deployment, and a smooth user experience.

---

## 🚀 Live Demo

- **Deployed Application (UI):** [http://urlshort.eu-north-1.elasticbeanstalk.com/register](http://urlshort.eu-north-1.elasticbeanstalk.com/register)  
- **API Documentation (Swagger UI):** [http://urlshort.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html#/](http://urlshort.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html#/)

---

## ✨ Features

- User registration and authentication with JWT  
- Create and manage personal short links  
- Public dashboard for trending links  
- Click tracking and link expiration  
- Deployed with Docker and supports environment-based configuration  

---

## 🧩 Tech Stack

- **Java 17**  
- **Spring Boot 3** (Web, Security, JPA)  
- **MySQL**  
- **Thymeleaf** (for minimal UI)  
- **Docker & Docker Compose**

---

## 🐳 Run with Docker

```bash
docker compose up --build
