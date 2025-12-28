# Learning Platform (ORM / Hibernate)

Учебное backend-приложение для изучения ORM и Hibernate на примере онлайн-платформы обучения.
Проект демонстрирует работу с сущностями, связями, транзакциями, DTO, REST API и lazy-loading.

Технологии: Java 17, Spring Boot 3.5.x, Spring Data JPA, Hibernate, PostgreSQL, Testcontainers.

## Запуск PostgreSQL

docker compose up -d

## Запуск приложения

mvn spring-boot:run

Приложение запускается на порту 8081 (профиль dev).

## Запуск тестов

mvn test

## REST API (пример)

POST /api/courses
GET /api/courses/{id}
GET /api/courses/{id}/structure

## UI (опционально)

/ui/courses
/ui/courses/{id}
/ui/assignments
/ui/assignments/{id}/submit
/ui/students/{id}
