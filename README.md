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

## User Interface (опционально)

В проект добавлен простой серверный пользовательский интерфейс на Thymeleaf + Bootstrap.
UI предназначен **исключительно для демонстрации и ручного тестирования backend-логики**
(ORM, связи сущностей, транзакции, lazy-loading).

UI не заменяет и не дублирует REST API, а использует те же сервисы и DTO.

Доступные страницы:
- `/ui/courses` — список курсов
- `/ui/courses/{id}` — структура курса и запись студента
- `/ui/assignments` — список заданий
- `/ui/assignments/{id}/submit` — отправка решения
- `/ui/students/{id}` — курсы, задания и результаты тестов студента
