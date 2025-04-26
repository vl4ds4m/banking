# Задание

Написать java программу, которая принимает на вход два целочисоленных аргумента и выводит результат суммирования

Для написанной программы написать Dockerfile

Создать в GitHub Actions два workflow:

### Workflow сборки
Запускается при пуше/мерже в мастер и при создании pull request'а
1) Собрать проект
2) Собрать Docker образ
3) Запушить собранный образ в Docker Hub с тэгом **$branch_name-$commmit_hash** ($commmit_hash - первые 7 символов хэша коммита)
4) Если ворклоу запущен на мастер ветке дополнительно запушить собранный образ в Docker Hub с тэгом **latest**


### Workflow запуска
Запускается вручную, принимая на вход тэг образа и два слагаемых. Если тэг не передали - брать latest
1) Скачивает образ из Docker Hub
2) Запускает контейнер с образом и выводит результат в лог


В комменты на edu пиложить ссылку на ваш docker hub. Там должны быть видны собранные с разных веток образы


Материалы:

+ https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables
+ https://github.com/docker/login-action
+ https://docs.github.com/en/actions/security-guides/using-secrets-in-github-actions
+ https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#workflow_dispatch
+ https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#providing-inputs
