# Дз 6

В `converter` добавить обработку ошибок со стороны `rates`. 
Если запрос `GET /rates` упал - сделать еще 3 ретрая. Через 50мс, 100мс и 150мс. 
(50мс между оригинальным запросом и первым ретраем, 100мс между 1 и 2 ретраями, 150мс - между 2 и 3 ретраями)
Если все повторные запросы тоже упали - возвращать ошибку с кодом 500


В `accounts` добавить circuit breaker ан grpc вызов конвертера.
CB должен открываться, если более 50% запросов за минуту заканчиваются ошибкой, минимальное количество запросов для открытия - 10.
Переходить в half-open через 10 секунд. Количество запросов для перехода в closed из half-open - 3

В `accounts` на запрос `/customers/{customerId}/balance добавить rate limit. 5 запросов в минуту на каждого пользователя (customerId)




# Тестирование

В свой воркфлоу сборки добавить новую джобу

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests-hw6.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      converter-image-name: foo/bar-converter # имя образа вашего приложения
      accounts-image-name: foo/bar-accounts # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
    secrets:
      HSE_LOKI_TOKEN: ${{ secrets.HSE_LOKI_TOKEN }}
```

# Материалы

https://github.com/MarcGiffing/bucket4j-spring-boot-starter
https://www.baeldung.com/resilience4j
https://www.baeldung.com/spring-retry
