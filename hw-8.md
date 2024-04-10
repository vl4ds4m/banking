# Дз 8

В `accounts` добавить ведение истории изменения баланса по каждому счету.
Метод `GET /accounts/{accountNumber}/transactions` должен возвращать список всех транзакций по счету

Формат ответа:

```json
[
  {
    "transactionId": "71da0866-4b13-402c-89e7-a814aa8b1d4a",
    "amount": 12.34
  },
  {
    "transactionId": "71da0866-4b13-402c-89e7-a814aa8b1d4b",
    "amount": -12.34
  }
]
```

В транзакциях должны отображаться пополнения через `POST /accounts/{accountNumber}/top-up` и
переводы через `POST /transfers`

Расширить методы `POST /accounts/{accountNumber}/top-up` и `POST /transfers`. Они должны возвращать
транзакции, созданные в результате их выполнения. Для трансфера возвращать только транзакцию счета, с которого делается перевод

```json
{
  "transactionId": "71da0866-4b13-402c-89e7-a814aa8b1d4b",
  "amount": -12.34
}
```

В `accounts` для методов `POST /transfers` и `POST /accounts/{accountNumber}/top-up` добавить
поддержку идемпотентности. 
Если приходит повторный запрос с тем же ключом, не выполнять запрос повторно, а брать ответ из кэша.
Для кэша использовать redis. TTL - 30 секунд
Ключ идемпотентности ожидать в заголовке `Idempotency-Key`

Решение должно быть гибким и конфигурируемым на случай появление новых методов, нуждающихся в идемпотентности

# Тестирование

Новые переменные окружения:

* **REDIS_HOST** - имя хоста redis сервера
* **REDIS_PORT** - порт redis сервера

В свой воркфлоу сборки добавить новую джобу

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests-hw8.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      converter-image-name: foo/bar-converter # имя образа вашего приложения
      accounts-image-name: foo/bar-accounts # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
```

# Материалы

https://levelup.gitconnected.com/spring-boot-3-build-the-efficient-idempotent-api-by-redis-8d8ef70d2574
https://www.youtube.com/watch?v=QpBaA6B1U90&ab_channel=suchkovtech
https://www.baeldung.com/spring-data-redis-properties
https://habr.com/ru/companies/domclick/articles/779872/
https://www.youtube.com/watch?v=YlXJMCdssAI&ab_channel=YandexforDevelopers
