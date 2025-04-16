# Task Management System with Kafka Notifications

## Описание проекта

Система управления задачами с REST API и асинхронными уведомлениями об изменении статусов через Apache Kafka.

## Основные функции

### CRUD операции для задач

- Создание, чтение, обновление и удаление задач
- Получение списка всех задач
- Логирование операций через Spring AOP

### Система уведомлений

- Отправка событий в Kafka при изменении статуса задачи
- Обработка событий и отправка email-уведомлений
- Надежная доставка сообщений

## Технологии

- **Backend**:
    - Java 17
    - Spring Boot 3.4.4
    - Spring Data JPA
    - Spring Kafka
    - Spring Mail
    - Lombok
    - H2/PostgreSQL

- **Инфраструктура**:
    - Apache Kafka
    - Docker
    - Maven

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/your-repo/t1_hw1.git
cd t1_hw1
```

### 2. Запуск инфраструктуры

```bash
docker-compose up -d
```

### 3. Создание топиков Kafka

```bash
docker exec -it kafka kafka-topics --create \
  --topic task-status-updates \
  --partitions 1 \
  --replication-factor 1 \
  --bootstrap-server localhost:9092
```

### 4. Запуск приложения

```bash
mvn spring-boot:run
```

## Использование API

### Создание задачи

```bash
curl -X POST -H "Content-Type: application/json" \
  -d '{"title":"First task","description":"Do something","userId":1,"status":"PENDING"}' \
  http://localhost:8080/tasks
```

### Получение задачи по ID

```bash
curl http://localhost:8080/tasks/1
```

### Обновление задачи

```bash
curl -X PUT -H "Content-Type: application/json" \
  -d '{"title":"Updated task","description":"Do something else","userId":1,"status":"IN_PROGRESS"}' \
  http://localhost:8080/tasks/1
```

### Удаление задачи

```bash
curl -X DELETE http://localhost:8080/tasks/1
```

### Получение всех задач

```bash
curl http://localhost:8080/tasks
```

## Настройка email-уведомлений

1. Отредактируйте `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

2. Для Gmail необходимо создать "Пароль приложения" в настройках аккаунта Google.

## Мониторинг Kafka

1. Установите [Offset Explorer](https://www.kafkatool.com/)
2. Подключитесь к `localhost:29092`
3. Просматривайте сообщения в топике `task-status-updates`

## Архитектура

```
Client -> REST Controller -> Service -> Repository
                    |
                    v
                Kafka Producer -> Kafka Topic
                                    |
                                    v
                            Kafka Consumer -> Notification Service -> Email
```

## Логирование

Система логирует:

- Попытки создания задач
- Успешные операции получения задач
- Ошибки (например, задача не найдена)
- Время выполнения методов

## Тестирование

Запуск тестов:

```bash
mvn test
```

Тесты покрывают:

- Unit-тесты сервисов
- Интеграционные тесты REST API
- Тесты Kafka с EmbeddedKafka
- Тесты аспектов логирования
