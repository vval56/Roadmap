# Lab 6 JMeter Results

Сюда сохраняются результаты тестирования после запуска:

- `lab6-smoke-results.jtl`
- `lab6-smoke-html-report/`
- `lab6-get-boundary-results.jtl`
- `lab6-get-boundary-html-report/`
- `lab6-results.jtl`
- `lab6-html-report/`

## Smoke test

```bash
jmeter -n \
  -t jmeter/RoadMap2026_Lab6_Smoke_Check.jmx \
  -l jmeter/results/lab6-smoke-results.jtl \
  -e \
  -o jmeter/results/lab6-smoke-html-report
```

## Boundary GET load test

```bash
jmeter -n \
  -JreadThreads=80 \
  -JreadLoops=20 \
  -JrampUp=15 \
  -JslaMs=475 \
  -t jmeter/RoadMap2026_Lab6_GET_Boundary_Load_Test.jmx \
  -l jmeter/results/lab6-get-boundary-results.jtl \
  -e \
  -o jmeter/results/lab6-get-boundary-html-report
```

## Полный async/concurrency test

```bash
jmeter -n \
  -t jmeter/RoadMap2026_Lab6_Async_Concurrency_Load_Test.jmx \
  -l jmeter/results/lab6-results.jtl \
  -e \
  -o jmeter/results/lab6-html-report
```

Что приложить к защите:

| Scenario | Threads x loops | Avg ms | 95th pct ms | Throughput req/s | Error % | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| Smoke Check | 1 x 1 |  |  |  |  | Должно быть `0` ошибок |
| GET Boundary Load |  |  |  |  |  | Указать `readThreads/readLoops/rampUp/slaMs` и конфигурацию, на которой впервые появились ошибки |
| Start Async Analytics Report | 30 x 5 |  |  |  |  | `202 Accepted`, быстрый ответ |
| Get Async Task Status | 30 x 5 |  |  |  |  | polling статуса `PENDING/RUNNING/COMPLETED` |
| Get All RoadMap Items | 60 x 10 |  |  |  |  | контрольная read-нагрузка |
| Race Condition Demo | 8 x 3 |  |  |  |  | unsafe counter теряет инкременты, safe counters корректны |

Подсказка:
- `Average`, `95th percentile`, `Throughput` и `Error %` возьмите из `Summary Report` или HTML report.
- Если `Error % > 0`, приложите причину: например, неверный `roadMapId`, неготовая БД или неуспевший старт приложения.
