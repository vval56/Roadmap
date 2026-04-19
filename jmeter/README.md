# JMeter Load Testing

В проект добавлены готовые тест-планы:

- `jmeter/RoadMap2026_Lab6_Async_Bulk_RoadMapItem_Test.jmx`
- `jmeter/RoadMap2026_Lab6_Smoke_Check.jmx`
- `jmeter/RoadMap2026_Lab6_GET_Boundary_Load_Test.jmx`
- `jmeter/RoadMap2026_Lab6_Async_Concurrency_Load_Test.jmx`

## 0. Async bulk RoadMapItem test

План:
- `jmeter/RoadMap2026_Lab6_Async_Bulk_RoadMapItem_Test.jmx`

Что делает:
- `POST /api/async-tasks/roadmaps/{roadMapId}/bulk-roadmap-items`
- считает только успешность принятия async bulk задачи по `202 Accepted`

Важно:
- это именно async bulk для `RoadMapItem`
- в body можно использовать одинаковые `title`, потому что у `RoadMapItem` нет unique constraint на title
- чтобы не получать искусственные `404`, статус задачи проверяйте отдельно в Swagger или Postman, а не внутри нагрузочного JMeter плана
- в JMeter не вставляйте полный URL в поле `Path`

Правильно:
- `Server Name or IP`: `localhost`
- `Port Number`: `8080`
- `Path`: `/api/async-tasks/roadmaps/${roadMapId}/bulk-roadmap-items`

Запуск:

```bash
jmeter -n \
  -JserverHost=localhost \
  -JserverPort=8080 \
  -JroadMapId=32 \
  -t jmeter/RoadMap2026_Lab6_Async_Bulk_RoadMapItem_Test.jmx \
  -l jmeter/results/lab6-async-bulk-results.jtl \
  -e \
  -o jmeter/results/lab6-async-bulk-html-report
```

Для 1000 async POST-заявок используется конфигурация самого плана:
- `500` потоков
- `2` цикла
- итого `1000` POST запросов

## 1. Smoke test: программа работает нормально

План:
- `jmeter/RoadMap2026_Lab6_Smoke_Check.jmx`

Что проверяет:
- `GET /api/users`
- `GET /api/roadmaps`
- `GET /api/roadmap-items`
- `GET /api/async-tasks/metrics`

Ожидаемый результат:
- `Error % = 0`
- все запросы возвращают `200`

Запуск:

```bash
jmeter -n \
  -JserverHost=localhost \
  -JserverPort=8080 \
  -t jmeter/RoadMap2026_Lab6_Smoke_Check.jmx \
  -l jmeter/results/lab6-smoke-results.jtl \
  -e \
  -o jmeter/results/lab6-smoke-html-report
```

## 2. Boundary load test: несколько GET-запросов и количество ошибок

План:
- `jmeter/RoadMap2026_Lab6_GET_Boundary_Load_Test.jmx`

Что нагружается:
- `GET /api/users`
- `GET /api/roadmaps`
- `GET /api/roadmap-items`

Ожидаемая идея:
- при умеренной нагрузке `Error % = 0`
- дальше вы повышаете `readThreads` и/или `readLoops`
- в момент, когда появляются ошибки, вы видите пограничную нагрузку системы
- ошибки считаются не только по HTTP, но и по превышению SLA времени ответа `slaMs`

Базовый запуск:

```bash
jmeter -n \
  -JserverHost=localhost \
  -JserverPort=8080 \
  -JreadThreads=80 \
  -JreadLoops=20 \
  -JrampUp=15 \
  -JslaMs=475 \
  -t jmeter/RoadMap2026_Lab6_GET_Boundary_Load_Test.jmx \
  -l jmeter/results/lab6-get-boundary-results.jtl \
  -e \
  -o jmeter/results/lab6-get-boundary-html-report
```

Как искать границу:

1. Сначала убедитесь, что приложение реально запущено на `localhost:8080`.
2. Запустите с `readThreads=50` и `slaMs=475`.
3. Потом увеличивайте `readThreads`: `100`, `150`, `200`.
4. Если ошибок мало, уменьшайте `slaMs`: `450`, `400`, `350`.
5. Если ошибок слишком много, увеличивайте `slaMs` или `rampUp`.
6. Цель для наглядной демонстрации: выйти примерно на `30-50%` ошибок.
7. Смотрите в summary:
   - `Err`
   - `Error %`
   - `Throughput`
   - `Max`
8. Последняя конфигурация с `0` ошибок — безопасная.
9. Конфигурация, где доля ошибок резко пошла вверх, — пограничная нагрузка.

Проверенный ориентир по вашим реальным данным:
- `readThreads=120`
- `readLoops=20`
- `rampUp=3`
- `slaMs=470`
- это даёт примерно `50.7%` ошибок

Команда для наглядной демонстрации:

```bash
jmeter -n \
  -JserverHost=localhost \
  -JserverPort=8080 \
  -JreadThreads=120 \
  -JreadLoops=20 \
  -JrampUp=3 \
  -JslaMs=470 \
  -t jmeter/RoadMap2026_Lab6_GET_Boundary_Load_Test.jmx \
  -l jmeter/results/lab6-get-boundary-results.jtl \
  -e \
  -o jmeter/results/lab6-get-boundary-html-report
```

## 3. Полный комбинированный тест-план

План:
- `jmeter/RoadMap2026_Lab6_Async_Concurrency_Load_Test.jmx`

Что нагружается:

1. `POST /api/async-tasks/roadmaps/{roadMapId}/analytics-report`
2. `GET /api/async-tasks/{taskId}`
3. `GET /api/roadmap-items`
4. `POST /api/concurrency/race-condition`

## Перед запуском

1. Запустите PostgreSQL:

```bash
docker compose up -d
```

2. Запустите приложение:

```bash
./mvnw spring-boot:run
```

3. Дождитесь полного старта приложения и проверьте, что `8080` действительно слушается:

```bash
curl -i http://localhost:8080/api/roadmap-items
```

4. Убедитесь, что roadmap с `id=2` существует. Если у вас другой id, передайте его в запуске через `-JroadMapId=...`.
5. Убедитесь, что JMeter установлен локально и команда `jmeter` доступна в `PATH`.

Запуск:

```bash
jmeter -n \
  -t jmeter/RoadMap2026_Lab6_Async_Concurrency_Load_Test.jmx \
  -l jmeter/results/lab6-results.jtl \
  -e \
  -o jmeter/results/lab6-html-report
```

Если приложение у вас не на `localhost:8080` или roadmap имеет другой id, запускайте так:

```bash
jmeter -n \
  -JserverHost=localhost \
  -JserverPort=8080 \
  -JroadMapId=2 \
  -t jmeter/RoadMap2026_Lab6_Async_Concurrency_Load_Test.jmx \
  -l jmeter/results/lab6-results.jtl \
  -e \
  -o jmeter/results/lab6-html-report
```

## Что показать преподавателю

1. Таблицу Summary Report / Aggregate Report.
2. HTML report из `jmeter/results/lab6-html-report/index.html`.
3. Метрики:
   - `Average`
   - `Min`
   - `Max`
   - `Throughput`
   - `Error %`
4. Для smoke test показать `Error % = 0`.
5. Для boundary load test показать, при каком `readThreads` начали появляться ошибки.
6. Для полного плана показать, что async endpoint отвечает быстро, а concurrency endpoint демонстрирует race condition.
7. Заполненный шаблон результатов из `jmeter/results/README.md`.

## Ожидаемая интерпретация

- `POST /api/async-tasks/roadmaps/{roadMapId}/analytics-report` быстро отвечает, потому что сама работа уходит в `@Async`.
- `GET /api/async-tasks/{taskId}` позволяет наблюдать жизненный цикл фоновой задачи.
- `GET /api/roadmap-items` используется как контрольная read-нагрузка.
- в boundary GET test часть ошибок может быть вызвана именно превышением SLA `slaMs`, а не HTTP 500/404
- `POST /api/concurrency/race-condition` показывает, что даже при внешней нагрузке unsafe counter остаётся источником race condition, а `synchronized` и `AtomicInteger` дают корректный результат.
