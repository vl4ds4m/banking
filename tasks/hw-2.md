1) установить minikube https://kubernetes.io/ru/docs/tasks/tools/install-minikube/ (если кто-то пользуется colima на маках с arm - https://pet2cattle.com/2022/09/minikube-colima-macos-m1)

2) В проекте с 1 ДЗ доработать программу до спринг приложения с одним контроллером, который на GET запрос возвращает случайное число, сгенерированное один раз на старте приложения и пишет в stdout информацию, что пришел запрос

3) Создать helm chart, разворачивающий в k8s ваше приложение в двух репликах. Chart должен состоять из деплоймента и сервиса (https://helm.sh/docs/chart_template_guide/getting_started/, https://habr.com/ru/articles/547682/)

В ответ к дз прикрепить вывод команд

kubectl get pods
kubectl get deployments
kubectl get services


4) Командой minikube service %service_name% прокинуть сервис наружу и несколько раз выполнить curl по методу, убедиться, что метод возвращает три разных числа в случайном порядке

Командами kubectl logs %pod_name_1% kubectl logs %pod_name_2% убедиться, что логи запросов есть в обеих репликах
