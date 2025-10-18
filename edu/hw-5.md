# Задание

Изменить протокол взаимодействия между сервисами `accounts` и `converter` с REST на GRPC.
Сделать отдельный модуль в котором будут генерироваться java модели/классы из `.proto` файла.
Использовать этот модуль как зависимость в `accounts` и `converter`. Авторизация не нужна

GRPC сервер должен 

В сервисе `accounts` добавить эндпоинт для подключению через websocket. Для рассылки сообщений через
websocket использовать протокол STOMP

Endpoint - `/ws`

Topic - `/topic/accounts`

При каждом изменении баланса/создании нового счета присылать в топик `/topic/accounts` сообщение с
информацией о счете.

Формат сообщения:

```json
{
  "accountNumber": 1,
  "currency": "RUB",
  "balance": 123.45
}
```

Пример:

1) Создали счет 1
    ```json
    {
      "accountNumber": 1,
      "currency": "RUB",
      "balance": 0.00
    }
    ```

2) Создали счет 2

    ```json
    {
      "accountNumber": 2,
      "currency": "RUB",
      "balance": 0.00
    }
    ```

3) Пополнили счет

    ```json
    {
      "accountNumber": 1,
      "currency": "RUB",
      "balance": 100.00
    }
    ```

4) перевели на другой счет - 2 сообщения. Счет отправки и счет поступления

    ```json
    {
      "accountNumber": 1,
      "currency": "RUB",
      "balance": 70.70
    }
    ```

    ```json
    {
      "accountNumber": 2,
      "currency": "RUB",
      "balance": 29.30
    }
    ```

Для желающих получить доп баллы и разобраться с реактивными библиотеками.

Сделать сервис `converter` реактивным. Для этого использовать плагин генерации reactor-совместимых grpc сервисов https://github.com/salesforce/reactive-grpc/tree/master/reactor

В качестве http клиента использовать **WebClient** и вызовом `bodyToMono()`

# Тестирование

## Переменные окружения

Ваши приложения должны работать со следующими переменными окружения

Accounts:

* **DB_HOST** - хост БД
* **DB_PORT** - порт БД
* **DB_NAME** - название БД
* **DB_USER** - пользователь БД
* **DB_PASSWORD** - пароль для подключения к БД
* **CONVERTER_URL** - адрес конвертера вида `smth:9090`
* **KEYCLOAK_URL** - адрес киклоки вида `http://smth:1234`
* **KEYCLOAK_REALM** - realm, в котором живет клиент
* **CLIENT_ID** - client-id
* **CLIENT_SECRET** - client-secret

Converter:

* **RATES_URL** - адрес сервиса курсов валют вида http://smth:1234
* **KEYCLOAK_URL** - адрес киклоки вида http://smth:1234
* **KEYCLOAK_REALM** - realm, в котором живет клиент
* **CLIENT_ID** - client-id
* **CLIENT_SECRET** - client-secret

При установке хелм чарта переменные окружения будут передаваться через переменные чарта.
Для этого нужно добавить в свои чарты поддержку переменной extraEnv

```yml
extraEnv:
  - name: ENV_NAME
    value: some-value
```

Чарт должен уметь работать с переменной-массивом и все параметры передавать в переменные окружения
пода

Чарт должен уметь принимать порт для pod/service
```yml
service:
   port: 9090
```

И конфигурацию проб
```yml
livenessProbe:
   httpGet: null
   grpc:
      port: 9090
   initialDelaySeconds: 10
   periodSeconds: 5
   successThreshold: 1
   timeoutSeconds: 2
readinessProbe:
   httpGet: null
   grpc:
      port: 9090
   initialDelaySeconds: 10
   periodSeconds: 5
   successThreshold: 1
   timeoutSeconds: 2

```

В свой воркфлоу сборки добавить новую джобу

Т.к. 

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests-hw5.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      converter-image-name: foo/bar-converter # имя образа вашего приложения
      accounts-image-name: foo/bar-accounts # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
    secrets:
       HSE_LOKI_TOKEN: ${{ secrets.HSE_LOKI_TOKEN }}
```

# Материалы

https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html
https://github.com/grpc-ecosystem/grpc-spring
https://spring.io/guides/gs/messaging-stomp-websocket


https://github.com/salesforce/reactive-grpc/tree/master/reactor
