helm install postgresql oci://registry-1.docker.io/bitnamicharts/postgresql -f test/k8s/cfg/postgres-values.yml --wait

helm install redis oci://registry-1.docker.io/bitnamicharts/redis -f test/k8s/cfg/redis-values.yml --wait

helm install kafka oci://registry-1.docker.io/bitnamicharts/kafka -f test/k8s/cfg/kafka-values.yml --wait

kubectl create secret generic realm-secret --from-file=test/k8s/cfg/realm.json
helm install keycloak keycloakx --repo https://codecentric.github.io/helm-charts --version 2.3.0 -f test/k8s/cfg/keycloak-values.yml --wait

helm install rates ./helm-chart -f test/k8s/cfg/rates-values.yml --wait

helm install converter ./helm-chart -f test/k8s/cfg/converter-values.yml --wait

helm install accounts ./helm-chart -f test/k8s/cfg/accounts-values.yml --wait
