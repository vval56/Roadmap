# RoadMap2026

Spring Boot REST API для управления roadmap-структурой обучения с PostgreSQL, JPA/Hibernate, пагинацией, сложными запросами и in-memory кэшем поисковых результатов.

## Автор
- Студент: **Могильный Владислав**
- Группа: **450503**

## Что реализовано
1. Подключена PostgreSQL через Docker.
2. Реализованы сущности `User`, `RoadMap`, `RoadMapItem`, `Tag`, `Comment`.
3. Реализованы связи:
- `User -> RoadMap` (`OneToMany`)
- `RoadMap -> RoadMapItem` (`OneToMany`)
- `RoadMapItem -> Comment` (`OneToMany`)
- `RoadMapItem <-> Tag` (`ManyToMany`)
- `RoadMapItem -> RoadMapItem` через `parent_item_id` для прямой item-to-item связи
4. Реализован CRUD для основных сущностей.
5. Реализованы:
- `@EntityGraph`
- `fetch join`
- демонстрация `N+1`
- `@Transactional` demo
- bulk-операция массового создания `RoadMapItem`
- сложный GET через `JPQL`
- аналогичный GET через `native query`
- пагинация через `Pageable`
- in-memory индекс на `HashMap` с составным ключом
- инвалидация кэша при изменении данных
6. Дополнительно:
- глобальная обработка ошибок через `@RestControllerAdvice`
- единый JSON-формат ошибок для всех endpoint
- валидация входных данных через `@Valid`, `@Validated`, Bean Validation constraints
- использование `Stream API` и `Optional` в сервисном слое
- асинхронная бизнес-операция через `@Async` / `CompletableFuture` с `taskId` и polling статуса
- потокобезопасные счётчики async-задач на `AtomicLong`
- демонстрация race condition на `ExecutorService` (50+ потоков) и решение через `synchronized` / `AtomicInteger`
- логирование через `logback-spring.xml` с ротацией логов
- AOP-логирование времени выполнения сервисных методов
- Swagger/OpenAPI через `springdoc`

## Структура проекта
```text
RoadMap2026/
├── .github/
│   └── workflows/
│       └── sonar.yml
├── .mvn/
│   └── wrapper/
├── .vscode/
├── jmeter/
│   ├── README.md
│   ├── RoadMap2026_Lab7_Async_Concurrency_Load_Test.jmx
│   └── results/
├── postman/
│   └── collections/
├── README.md
├── docker-compose.yml
├── google_checks.xml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/roadmap/
    │   │   ├── RoadMap2026Application.java
    │   │   ├── aspect/
    │   │   ├── bootstrap/
    │   │   ├── cache/
    │   │   ├── config/
    │   │   ├── controller/
    │   │   │   ├── AsyncTaskController.java
    │   │   │   ├── CommentController.java
    │   │   │   ├── ConcurrencyDemoController.java
    │   │   │   ├── RoadMapController.java
    │   │   │   ├── RoadMapItemController.java
    │   │   │   ├── TagController.java
    │   │   │   ├── TransactionDemoController.java
    │   │   │   └── UserController.java
    │   │   ├── dto/
    │   │   │   ├── AsyncTaskCountersDto.java
    │   │   │   ├── AsyncTaskStatus.java
    │   │   │   ├── AsyncTaskStatusDto.java
    │   │   │   ├── AsyncTaskSubmissionDto.java
    │   │   │   ├── BaseRoadMapItemRequestDto.java
    │   │   │   ├── RaceConditionDemoRequestDto.java
    │   │   │   ├── RaceConditionDemoResultDto.java
    │   │   │   ├── RoadMapAnalyticsReportDto.java
    │   │   │   └── ...
    │   │   ├── exception/
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   │       ├── AsyncTaskRegistryService.java
    │   │       ├── ConcurrencyDemoService.java
    │   │       ├── RoadMapAnalyticsAsyncWorker.java
    │   │       ├── RoadMapAnalyticsTaskService.java
    │   │       └── ...
    │   └── resources/
    │       ├── application.properties
    │       └── logback-spring.xml
    └── test/
        ├── java/com/example/roadmap/
        │   ├── controller/
        │   ├── service/
        │   │   ├── AsyncTaskRegistryServiceTest.java
        │   │   ├── ConcurrencyDemoServiceTest.java
        │   │   ├── RoadMapAnalyticsAsyncWorkerTest.java
        │   │   └── ...
        │   └── RoadMap2026ApplicationTests.java
        └── resources/
            └── mockito-extensions/
```

## Запуск инфраструктуры
```bash
docker compose up -d
```

PostgreSQL:
- host: `localhost`
- port: `5433`
- db: `roadmap_db`
- user: `roadmap_user`
- password: `roadmap_pass`

pgAdmin:
- `http://localhost:5050`

## Запуск приложения
```bash
./mvnw spring-boot:run
```

Проверка сборки:
```bash
./mvnw -q -DskipTests compile
```

## Ключевые endpoint'ы

CRUD:
- `/api/users`
- `/api/roadmaps`
- `/api/roadmap-items`
- `/api/tags`
- `/api/comments`

Асинхронность и многопоточность:
- `POST /api/async-tasks/roadmaps/{roadMapId}/analytics-report`
- `GET /api/async-tasks/{taskId}`
- `GET /api/async-tasks/metrics`
- `POST /api/concurrency/race-condition`

Bulk-операция:
- `POST /api/roadmap-items/bulk/{roadMapId}`

Транзакционный bulk demo:
- `POST /api/transactions/without-transactional`
- `POST /api/transactions/with-transactional`

N+1 demo:
- `/api/roadmap-items/n-plus-one`
- `/api/roadmap-items/entity-graph`
- `/api/roadmap-items/fetch-join`

Сложные GET-запросы:
- `/api/roadmap-items/search/jpql`
- `/api/roadmap-items/search/native`

Пример:
```http
GET /api/roadmap-items/search/jpql?ownerEmail=vladislav@example.com&roadMapTitle=java&parentTitle=jpa&tagName=spring&status=IN_PROGRESS&page=0&size=5
```

```http
GET /api/roadmap-items/search/native?ownerEmail=vladislav@example.com&roadMapTitle=java&parentTitle=jpa&tagName=spring&status=IN_PROGRESS&page=0&size=5
```

Пример bulk-запроса:
```http
POST /api/roadmap-items/bulk/1
Content-Type: application/json

[
  {
    "title": "Configure Docker",
    "details": "Prepare postgres container",
    "status": "PLANNED",
    "tagIds": [1, 2]
  },
  {
    "title": "Write repositories",
    "details": "Create JpaRepository layer",
    "status": "IN_PROGRESS"
  }
]
```

Пример демонстрации без `@Transactional`:
```http
POST /api/transactions/without-transactional
Content-Type: application/json

{
  "ownerId": 1,
  "roadMapTitle": "Bulk Transaction Demo",
  "items": [
    {
      "title": "Step 1",
      "details": "Will stay in DB without transaction",
      "status": "PLANNED"
    },
    {
      "title": "Step 2",
      "details": "Failure happens after this save",
      "status": "PLANNED"
    }
  ]
}
```

Ожидаемая разница:
- `/without-transactional` оставляет в БД новую roadmap и первые сохраненные items
- `/with-transactional` откатывает всю bulk-операцию целиком

## ER-логика модели
- `app_users` хранит пользователя с `first_name`, `last_name`, `email`
- `roadmaps` хранит roadmap и его владельца
- `roadmap_items` хранит пункты roadmap
- `parent_item_id` задает прямую связь item-to-item
- `roadmap_item_tag` связывает items и tags
- `comments` привязаны к item и автору

## Swagger / OpenAPI
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## JMeter
- тест-план: `jmeter/RoadMap2026_Lab7_Async_Concurrency_Load_Test.jmx`
- инструкция запуска: `jmeter/README.md`
- шаблон для фиксации результатов: `jmeter/results/README.md`

## Lab 7: Асинхронность и многопоточность

Асинхронная бизнес-операция:
- `POST /api/async-tasks/roadmaps/{roadMapId}/analytics-report` сразу возвращает `202 Accepted` и `taskId`
- `GET /api/async-tasks/{taskId}` позволяет проверить `PENDING`, `RUNNING`, `COMPLETED`, `FAILED`
- `GET /api/async-tasks/metrics` показывает потокобезопасные счётчики задач на `AtomicLong`

Пример запуска async-задачи:
```http
POST /api/async-tasks/roadmaps/2/analytics-report
```

Пример ответа:
```json
{
  "taskId": "report-1001",
  "status": "PENDING",
  "statusEndpoint": "/api/async-tasks/report-1001"
}
```

Пример проверки статуса:
```http
GET /api/async-tasks/report-1001
```

Демонстрация race condition:
- `POST /api/concurrency/race-condition`
- используются `ExecutorService`, `64+` потоков, `UnsafeCounter`, `synchronized`, `AtomicInteger`

Пример запроса:
```json
{
  "threadCount": 64,
  "incrementsPerThread": 5000
}
```

Что проверять в ответе:
- `expectedValue` должен быть равен `threadCount * incrementsPerThread`
- `unsafeCounterValue` обычно меньше ожидаемого из-за race condition
- `synchronizedCounterValue` и `atomicCounterValue` должны совпадать с ожидаемым значением

## Логи
- активная конфигурация: `src/main/resources/logback-spring.xml`
- основной файл логов: `logs/roadmap.log`
- архивы с ротацией: `logs/archive/`

## GitHub Actions и Sonar
- workflow находится в `.github/workflows/sonar.yml`
- локальная генерация coverage: `./mvnw -Pcoverage verify`
- XML-отчёт для Sonar: `target/site/jacoco/jacoco.xml`
- для GitHub Actions нужно настроить:
- secret `SONAR_TOKEN`
- variable `SONAR_PROJECT_KEY`
- variable `SONAR_ORGANIZATION`
- optional variable `SONAR_HOST_URL`:
  для SonarCloud можно не задавать, по умолчанию используется `https://sonarcloud.io`
