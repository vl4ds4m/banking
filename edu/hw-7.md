# Дз 7

В `accounts` при изменении денег на счету (пополнение и перевод) отправлять уведомление 

Метод `POST /notification`

Сообщение должно быть формата `"Счет ${номер счета}. Операция: ${сумма операции}. Баланс: ${баланс после операции}"`

Примеры:
```json
{
  "customerId": 1,
  "message": "Счет 123. Операция: +900. Баланс: 900"
}
```

```json
{
  "customerId": 1,
  "message": "Счет 123. Операция: -200. Баланс: 700"
}
```

Отправку уведомлений реализовать через transactional outbox паттерн. 
Учесть, что accounts может быть развернуть в нескольких экземплярах


# Тестирование

Новые переменные окружения:
* **NOTIFICATION_SERVICE_URL** - url к сервису для отправки пушей

В свой воркфлоу сборки добавить новую джобу

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests-hw7.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      converter-image-name: foo/bar-converter # имя образа вашего приложения
      accounts-image-name: foo/bar-accounts # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
    secrets:
      HSE_LOKI_TOKEN: ${{ secrets.HSE_LOKI_TOKEN }}
```

# Материалы

https://habr.com/ru/companies/lamoda/articles/678932/
https://microservices.io/patterns/data/transactional-outbox.html
