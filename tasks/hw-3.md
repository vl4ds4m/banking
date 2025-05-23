# Задание

Написать Spring приложение с одним REST методом:

`GET /convert?from={currency}&to={currency}&amount={number}` - принимает на вход исходную/целевую
валюты и сумму конвертации

Для получения курса валют вызвать метод `GET /rates`.

Список доступных валют и схема запроса/ответа доступны в [файле](../common/src/main/openapi/currency.yml). Модели необходимо сгенерировать из файла с помощью swagger-codegen

Сервис возвращает список курсов валют относительно рубля для всех доступных валют.

Если приходит запрос на конвертацию двух иностранных валют - проводить ее по кросс-курсу. Например `EUR->USD = EUR->RUB->USD`

Возвращать ответ в виде json

```json
{
  "currency": "USD",
  "amount": 42342.56
}
```

Все суммы нужно передавать до 2 знаков после запятой. Способ округления - half even.

Формат ответа при ошибке

```json
{
  "message": "error message"
}
```

Сообщения об ошибках:

+ amount <= 0: Http code 400, message - `"Отрицательная сумма"`
+ to/from нет в списке доступных валют: Http code 400, message `"Валюта {currency} недоступна"`

# Тесты

В свой воркфлоу сборки добавить новую джобу

```yaml
jobs:
  autotest:
    needs: $build_job_name # имя вашей основной джобы сборки
    uses: central-university-dev/hse-ab-cicd-hw/.github/workflows/autotests.yml@main
    with:
      chart-path: ./rates # путь к чарту из второй дз
      image-name: foo/bar-converter # имя образа вашего приложения
      image-tag: $branch_name-$commit_hash # таг образа, который собран в рамках данного ПРа
    secrets:
      HSE_LOKI_TOKEN: ${{ secrets.HSE_LOKI_TOKEN }}
```

Чарт из второй дз должен уметь принимать на вход параметры:

+ image.repository
+ image.tag
+ replicaCount

В чарте должны быть правильно указаны ready/live пробы

Сервис с курсами будет доступен по http://rates:8080

# ДЗ без пройденных автотестов не проверяется
