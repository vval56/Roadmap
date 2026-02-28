# RoadMap2026

Spring Boot REST API для лабораторной работы №1.

## Автор
- Студент: **Могильный Владислав**
- Группа: **450503**

## Содержание
1. [Описание проекта](#описание-проекта)
2. [Реализованные требования](#реализованные-требования)
3. [Стек технологий](#стек-технологий)
4. [Запуск проекта](#запуск-проекта)
5. [Демонстрация в Postman](#демонстрация-в-postman)
6. [Сценарий демонстрации (защита)](#сценарий-демонстрации-защита)
7. [Эндпоинты API](#эндпоинты-api)
8. [Структура проекта](#структура-проекта)
9. [Проверка стиля Checkstyle](#проверка-стиля-checkstyle)

## Описание проекта
Проект реализует REST API для ключевой сущности предметной области: **RoadMapItem** (элемент дорожной карты обучения/задач).

Приложение построено по слоям:
- `Controller` — принимает HTTP-запросы;
- `Service` — содержит бизнес-логику;
- `Repository` — доступ к данным (in-memory хранилище).

Также реализованы:
- DTO для API (`RoadMapItemDto`);
- Mapper для преобразования `Entity <-> DTO`.

## Реализованные требования
1. Создано Spring Boot приложение.
2. Реализован REST API для сущности `RoadMapItem`.
3. Реализованы обязательные GET endpoint'ы:
   - `@RequestParam`: `GET /api/items?status=...`
   - `@PathVariable`: `GET /api/items/{id}`
4. Реализованы слои: `Controller -> Service -> Repository`.
5. Реализованы DTO и mapper между моделью и API-ответом.
6. Подключен и настроен Checkstyle (`google_checks.xml`).

## Стек технологий
- Java 21
- Spring Boot 3.3.5
- Maven
- Spring Web
- Spring Validation
- Checkstyle

## Запуск проекта
### 1) Сборка и тесты
```bash
./mvnw test
```

### 2) Запуск приложения
```bash
./mvnw spring-boot:run
```

По умолчанию приложение стартует на `http://localhost:8080`.

Если порт `8080` занят:
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--server.port=8086
```

## Демонстрация в Postman
В репозитории есть готовая коллекция:
- `postman/RoadMap2026.postman_collection.json`

### Шаги демонстрации
1. Открыть Postman.
2. Нажать `Import`.
3. Выбрать файл `postman/RoadMap2026.postman_collection.json`.
4. Убедиться, что переменная `baseUrl` равна `http://localhost:8080` (или вашему порту).
5. Поочередно выполнить запросы:
   - `Get all items`
   - `Get item by id`
   - `Get items by status`
   - `Create item`

## Сценарий демонстрации (защита)
Рекомендуемый порядок показа:
1. `GET /api/items` — показать, что API возвращает список объектов.
2. `GET /api/items/1` — показать `@PathVariable`.
3. `GET /api/items?status=PLANNED` — показать `@RequestParam`.
4. `POST /api/items` — создать новый объект.
5. Повторно `GET /api/items` — показать, что новый объект появился в списке.

## Эндпоинты API
### 1) Получить все элементы
- **Method:** `GET`
- **URL:** `/api/items`

### 2) Получить элемент по id (`@PathVariable`)
- **Method:** `GET`
- **URL:** `/api/items/{id}`
- **Пример:** `/api/items/1`

### 3) Получить элементы по статусу (`@RequestParam`)
- **Method:** `GET`
- **URL:** `/api/items?status=...`
- **Пример:** `/api/items?status=PLANNED`

### 4) Создать элемент
- **Method:** `POST`
- **URL:** `/api/items`
- **Body (JSON):**
```json
{
  "title": "Prepare lab defense",
  "description": "Create API demo and README",
  "status": "IN_PROGRESS",
  "targetDate": "2026-03-10"
}
```

## Структура проекта
```text
RoadMap2026/
├── pom.xml
├── google_checks.xml
├── README.md
├── postman/
│   └── RoadMap2026.postman_collection.json
└── src/
    ├── main/
    │   ├── java/com/example/roadmap/
    │   │   ├── RoadMap2026Application.java
    │   │   ├── controller/
    │   │   │   └── RoadMapItemController.java
    │   │   ├── service/
    │   │   │   ├── RoadMapItemService.java
    │   │   │   └── RoadMapItemServiceImpl.java
    │   │   ├── repository/
    │   │   │   ├── RoadMapItemRepository.java
    │   │   │   └── InMemoryRoadMapItemRepository.java
    │   │   ├── model/
    │   │   │   └── RoadMapItem.java
    │   │   └── dto/
    │   │       ├── RoadMapItemDto.java
    │   │       └── RoadMapItemMapper.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/example/roadmap/
            └── RoadMap2026ApplicationTests.java
```

## Проверка стиля Checkstyle
Проверка запускается на фазе `validate` автоматически при `mvn test`.

Отдельный запуск:
```bash
./mvnw checkstyle:check
```
