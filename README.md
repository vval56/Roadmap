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
- сложный GET через `JPQL`
- аналогичный GET через `native query`
- пагинация через `Pageable`
- in-memory индекс на `HashMap` с составным ключом
- инвалидация кэша при изменении данных

## Структура проекта
```text
RoadMap2026/
├── .gitattributes
├── .gitignore
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── .vscode/
│   ├── extensions.json
│   ├── launch.json
│   └── settings.json
├── README.md
├── api.iml
├── docker-compose.yml
├── google_checks.xml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/example/roadmap/
    │   │   ├── RoadMap2026Application.java
    │   │   ├── bootstrap/
    │   │   │   └── DataInitializer.java
    │   │   ├── cache/
    │   │   │   ├── RoadMapItemSearchIndexService.java
    │   │   │   └── RoadMapItemSearchKey.java
    │   │   ├── controller/
    │   │   │   ├── CommentController.java
    │   │   │   ├── RoadMapController.java
    │   │   │   ├── RoadMapItemController.java
    │   │   │   ├── TagController.java
    │   │   │   ├── TransactionDemoController.java
    │   │   │   └── UserController.java
    │   │   ├── dto/
    │   │   │   ├── CommentDto.java
    │   │   │   ├── CommentMapper.java
    │   │   │   ├── RoadMapDto.java
    │   │   │   ├── RoadMapItemDto.java
    │   │   │   ├── RoadMapItemMapper.java
    │   │   │   ├── RoadMapItemWithTagsDto.java
    │   │   │   ├── RoadMapMapper.java
    │   │   │   ├── TagDto.java
    │   │   │   ├── TagMapper.java
    │   │   │   ├── TransactionDemoRequestDto.java
    │   │   │   ├── TransactionDemoResultDto.java
    │   │   │   ├── UserDto.java
    │   │   │   └── UserMapper.java
    │   │   ├── exception/
    │   │   │   └── ResourceNotFoundException.java
    │   │   ├── model/
    │   │   │   ├── Comment.java
    │   │   │   ├── ItemStatus.java
    │   │   │   ├── RoadMap.java
    │   │   │   ├── RoadMapItem.java
    │   │   │   ├── Tag.java
    │   │   │   └── User.java
    │   │   ├── repository/
    │   │   │   ├── CommentRepository.java
    │   │   │   ├── RoadMapItemRepository.java
    │   │   │   ├── RoadMapRepository.java
    │   │   │   ├── TagRepository.java
    │   │   │   └── UserRepository.java
    │   │   └── service/
    │   │       ├── CommentService.java
    │   │       ├── CommentServiceImpl.java
    │   │       ├── RoadMapItemService.java
    │   │       ├── RoadMapItemServiceImpl.java
    │   │       ├── RoadMapService.java
    │   │       ├── RoadMapServiceImpl.java
    │   │       ├── TagService.java
    │   │       ├── TagServiceImpl.java
    │   │       ├── TransactionDemoService.java
    │   │       ├── TransactionDemoServiceImpl.java
    │   │       ├── TransactionWorkerService.java
    │   │       ├── TransactionWorkerServiceImpl.java
    │   │       ├── UserService.java
    │   │       └── UserServiceImpl.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/com/example/roadmap/
        │   └── RoadMap2026ApplicationTests.java
        └── resources/
            ├── application.properties
            └── mockito-extensions/
                └── org.mockito.plugins.MockMaker
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

## ER-логика модели
- `app_users` хранит пользователя с `first_name`, `last_name`, `email`
- `roadmaps` хранит roadmap и его владельца
- `roadmap_items` хранит пункты roadmap
- `parent_item_id` задает прямую связь item-to-item
- `roadmap_item_tag` связывает items и tags
- `comments` привязаны к item и автору
