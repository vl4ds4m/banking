helm install postgresql oci://registry-1.docker.io/bitnamicharts/postgresql -f test/ci-cd/postgres-values.yml --wait

helm install redis oci://registry-1.docker.io/bitnamicharts/redis -f test/ci-cd/redis-values.yml --wait

helm install kafka oci://registry-1.docker.io/bitnamicharts/kafka -f test/ci-cd/kafka-values.yml --wait

kubectl create secret generic realm-secret --from-file=test/ci-cd/realm.json
helm install keycloak keycloakx --repo https://codecentric.github.io/helm-charts --version 2.3.0 -f test/ci-cd/keycloak-values.yml --wait

helm install rates ./helm-chart -f test/ci-cd/rates-values.yml --wait

helm install converter ./helm-chart -f test/ci-cd/converter-values.yml --wait

helm install accounts ./helm-chart -f test/ci-cd/accounts-values.yml --wait
