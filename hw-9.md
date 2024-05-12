# Дз 9

В `accounts` для метода `POST /transfers` добавить списание комиссии за перевод. 

Добавить метод `POST /admin/configs` для обновления конфигов.  `0 <= fee <= 1`
```json
{
  "fee": 0.05
}
```

Конфиг хранить в БД. Формат конфига в БД произвольный

При старте приложения должно ходить в БД за конфигом и кэшировать его локально. Если в БД нет значения, считать, что комиссия равна нулю

Учесть, что приложение может быть развернуто в нескольких инстансах. Для обновления кэша использовать кафку. 

При получении запроса на обновление кэша обновлять его в бд и слать в топик сообщение вида 
```json 
{
  "action": "UPDATE_FEE"
}
```

В каждом инстансе вычитывать топик. Если пришло сообщение с `action == UPDATE_FEE`, перечитывать кэш из базы, другие события игнорировать



# Тестирование

Новые переменные окружения:

* **KAFKA_BOOTSTRAP_SERVERS** - список хостов кафка брокера
* **KAFKA_TOPIC** - имя топика

В свой воркфлоу сборки добавить новую джобу

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests-hw9.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      converter-image-name: foo/bar-converter # имя образа вашего приложения
      accounts-image-name: foo/bar-accounts # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
    secrets:
      HSE_LOKI_TOKEN: ${{ secrets.HSE_LOKI_TOKEN }}
```

# Материалы
https://docs.spring.io/spring-kafka/reference/kafka/receiving-messages/listener-annotation.html
https://docs.spring.io/spring-kafka/reference/tips.html

https://github.com/bitnami/containers/blob/main/bitnami/kafka/README.md - разделы **Apache Kafka development setup example** и **Accessing Apache Kafka with internal and external clients**
