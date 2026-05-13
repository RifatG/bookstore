# BookStore (MyBookShopApp)

Книжный интернет-магазин с микросервисной архитектурой, развёрнутый в Kubernetes. Проект для изучения и демонстрации современных подходов: микросервисы, асинхронное взаимодействие (Kafka), observability (Prometheus, Grafana, Loki), развёртывание в K8s через Helm, RAG-поиск с LLM (Qwen).

---

## Оглавление

1. [Архитектура проекта](#архитектура-проекта)
2. [Технологический стек](#технологический-стек)
3. [Требования](#требования)
4. [Локальный запуск (без Kubernetes)](#локальный-запуск-без-kubernetes)
5. [Запуск в Kubernetes (kind)](#запуск-в-kubernetes-kind)
6. [Структура Helm-чарта](#структура-helm-чарта)
7. [Мониторинг и логи](#мониторинг-и-логи)
8. [Важные команды](#важные-команды)
9. [Команды для отладки](#команды-для-отладки)
10. [Разработка и доработка](#разработка-и-доработка)
11. [Автор](#автор)

---

## Архитектура проекта

### Сервисы и компоненты

| Сервис | Где живёт | Назначение |
|--------|-----------|------------|
| **PostgreSQL** | Снаружи K8s (на хост-машине или в облаке) | Основная БД приложения (книги, пользователи, заказы) |
| **Redis** | Внутри K8s (StatefulSet + PVC) | Кэш, сессии пользователей |
| **main-app** | Внутри K8s (Deployment) | Spring Boot приложение: REST API, бизнес-логика |
| **Kafka** | Внутри K8s (StatefulSet) | Асинхронные события (просмотры книг) |
| **analytics** | Внутри K8s (Deployment) | Consumer: читает из Kafka, считает популярность книг |
| **Prometheus + Grafana** | Внутри K8s (Helm dependency) | Сбор метрик, дашборды |
| **Grafana Loki** | Внутри K8s (опционально) | Сбор логов |
| **Ingress (nginx)** | Внутри K8s (Helm dependency) | Вход в кластер извне |

### Схема взаимодействия

                                                  ┌─────────────────┐
                                                  │    Браузер      │
                                                  └────────┬────────┘
                                                           │
                                                ┌──────────▼──────────┐
                                                │   Ingress (nginx)   │
                                                │   bookstore.local   │
                                                └──────────┬──────────┘
                                                           │
                                                           ▼
                        ┌──────────────────────────────────────────────────────────────────────┐
                        │                           Kubernetes Cluster                         │
                        │                                                                      │
                        │   ┌─────────────────────────────────────────────────────────────┐    │
                        │   │                      main-app (Spring Boot)                 │    │
                        │   │ ┌─────────────────────────────────────────────────────┐     │    │
                        │   │ │ • REST API (книги, заказы, пользователи)            │     │    │
                        │   │ │ • Producer: отправляет события в Kafka              │     │    │
                        │   │ │ • Подключается к PostgreSQL (снаружи кластера)      │     │    │
                        │   │ │ • Подключается к Redis (внутри кластера)            │     │    │
                        │   │ └─────────────────────────────────────────────────────┘     │    │
                        │   └─────────────────────────────────────────────────────────────┘    │
                        │                        │                       │                     │
                        │                        │ (запросы)             │ (подключение)       │
                        │                        ▼                       ▼                     │
                        │                  ┌────────────┐         ┌─────────────┐              │
                        │                  │   Redis    │         │    Kafka    │              │
                        │                  │ (PVC + K8s)│         │(StatefulSet)│              │
                        │                  └────────────┘         └─────┬───────┘              │
                        │                                               │                      │
                        │                                               │ (события)            │
                        │                                               ▼                      │
                        │   ┌─────────────────────────────────────────────────────────────┐    │
                        │   │                analytics-service (Spring Boot)              │    │
                        │   │ • Consumer: читает события из Kafka                         │    │
                        │   │ • Считает популярность книг (оконные агрегации)             │    │
                        │   │ • RocksDB для stateful-обработки (Kafka Streams)            │    │
                        │   └─────────────────────────────────────────────────────────────┘    │
                        │                                                                      │
                        │   ┌─────────────────────────────────────────────────────────────┐    │
                        │   │                       Observability Stack                   │    │
                        │   │ • Prometheus (сбор метрик)                                  │    │
                        │   │ • Grafana (дашборды)                                        │    │
                        │   │ • Loki (логи)                                               │    │
                        │   │ • Alertmanager (алерты)                                     │    │
                        │   └─────────────────────────────────────────────────────────────┘    │
                        │                                                                      │
                        └──────────────────────────────────────────────────────────────────────┘
                                                         │
                                                         │ (подключение)
                                                         ▼
                                                ┌─────────────────┐
                                                │   PostgreSQL    │
                                                │  (снаружи K8s)  │
                                                └─────────────────┘

### Основание архитектуры

| Решение | Обоснование |
|---------|-------------|
| **PostgreSQL снаружи K8s** | Имитирует Managed Database в облаке. Для локальной разработки — запускается через Docker Compose или brew services |
| **Redis внутри K8s** | Тренировка с StatefulSet и PersistentVolume. В продакшене можно заменить на Managed Redis |
| **Kafka внутри K8s** | Показывает работу с StatefulSet. В облаке можно взять Managed Kafka |
| **analytics-service отдельно** | Демонстрирует микросервисную архитектуру и асинхронное взаимодействие |


---

## Технологический стек

### Backend

| Технология | Назначение |
|------------|------------|
| Java 17 / Kotlin | Основной язык |
| Spring Boot 3, MVC, Security, Data JPA | Фреймворк |
| PostgreSQL / Redis / Kafka | БД, кэш, очереди |
| Hibernate / Liquibase | ORM, миграции |
| gRPC / REST API | Межсервисное взаимодействие |

### Контейнеризация и оркестрация

| Технология | Назначение |
|------------|------------|
| Docker / Docker Compose | Контейнеризация |
| Kubernetes (kind) | Локальный кластер |
| Helm | Управление чартами |
| Ingress (nginx) | Вход в кластер |
| PersistentVolume (PVC) | Хранение данных |

### Observability

| Технология | Назначение |
|------------|------------|
| Prometheus + Grafana | Метрики |
| Grafana Loki | Логи |
| Alertmanager | Алерты |
| ServiceMonitor | Сбор метрик приложений |

### Frontend

| Технология | Назначение |
|------------|------------|
| Thymeleaf | Шаблонизатор |
| TypeScript / AngularJS | Интерфейс |

### DevOps

| Технология | Назначение |
|------------|------------|
| Git / GitLab CI/CD | Пайплайны |
| Maven | Сборка |
| Linux | ОС |

---

## Требования

- **Docker Desktop** (для kind и образов)
- **kind** (локальный кластер Kubernetes)
- **kubectl**
- **Helm** (3.x)
- **Java 17+**, **Maven**
- **PostgreSQL 14** (для локального запуска)

---

## Локальный запуск (без Kubernetes)

### Требуется отдельно поднять PostgreSQL

```bash
# Через Docker Compose (рекомендуется)
docker compose up -d postgres

# Или через brew (macOS)
brew services start postgresql@14

# Или через apt (Linux)
sudo systemctl start postgresql
```

### Запуск приложения

```bash
# Собрать
mvn clean package

# Запустить (PostgreSQL должен быть доступен)
java -jar target/MyBookShopApp-0.0.1-SNAPSHOT.jar --server.port=8085
```

## Запуск в Kubernetes (kind)

### Предварительные требования

1. **PostgreSQL доступен из кластера**  
   Для kind на macOS/Windows используйте `host.docker.internal`.  
   Для Linux используйте IP-адрес хоста (узнать командой `hostname -I`).

2. **Docker образ приложения загружен в реестр** (например, Docker Hub):

```bash
docker build -t rifatg13/bookstore-app:latest .
docker push rifatg13/bookstore-app:latest
```
3. **Установлены инструменты: docker, kind, kubectl, helm.**

### Пошаговая инструкция

1. **Создать кластер kind**

В корне проекта уже есть файл kind-config.yaml.

Выполните:

```bash
kind create cluster --config=kind-config.yaml
```

2. **Установить Helm-чарт (включает всё: приложение, Redis, мониторинг, Ingress)**

```bash
cd bookstore-chart
helm dependency build
helm install bookstore . --namespace bookstore --create-namespace
```
**Отдельно установить Kafka через Strimzi(Kubernetes Operator)**

```bash
helm uninstall kafka -n bookstore
# Установка оператора
kubectl apply -f 'https://strimzi.io/install/latest?namespace=bookstore' -n bookstore
# Создание кластера (1 брокер, без ZooKeeper, KRaft режим)
kubectl apply -f kafka-cluster.yaml -n bookstore
# Создание пользователя (уже через helm-чарт)
# Получаем пароль пользователя
kubectl get secret bookstore-user -n bookstore -o jsonpath='{.data.password}' | base64 -d
# Проверка (создаем временный консьюмер и смотрим сообщения) нужно поменять пароль
kubectl exec -it -n bookstore $(kubectl get pods -n bookstore -l strimzi.io/name=bookstore-kafka-kafka -o name | head -1 | cut -d/ -f2) -- bash -c "
cat > /tmp/client.properties <<EOF
security.protocol=SASL_PLAINTEXT
sasl.mechanism=SCRAM-SHA-512
sasl.jaas.config=org.apache.kafka.common.security.scram.ScramLoginModule required username=\"bookstore-user\" password=\"WvpyfX4nqCQHQl6D9n9SdetEQbZokfXR\";
EOF
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server bookstore-kafka-kafka-bootstrap:9092 --topic book-views --from-beginning --consumer.config /tmp/client.properties
"
```

3. **Проверить, что все поды запустились**

```bash
kubectl get pods -n bookstore
```
Все поды должны быть в статусе Running.

4. **Открыть доступ к приложению (port-forward, надёжный способ)**

```bash
kubectl port-forward -n bookstore svc/bookstore-app 8085:8085
```

5. **Открыть приложение в браузере**

```text
http://localhost:8085
```

### Удаление кластера

```bash
kind delete cluster --name bookstore-cluster
kind get clusters
docker system prune -a
```

## Структура Helm-чарта

```text
bookstore-chart/
├── Chart.yaml                 # метаданные и зависимости
├── values.yaml                # конфигурация
├── templates/
│   ├── app-deployment.yaml    # основное приложение
│   ├── app-service.yaml
│   ├── app-ingress.yaml
│   ├── app-servicemonitor.yaml
│   ├── redis-pvc.yaml
│   ├── redis-deployment.yaml
│   ├── redis-service.yaml
│   ├── analytics-deployment.yaml   # сервис аналитики
│   └── analytics-service.yaml
└── charts/                    # зависимости (kube-prometheus-stack, ingress-nginx, kafka)
```

## Мониторинг и логи

### Grafana

```bash
kubectl port-forward -n bookstore svc/bookstore-grafana 3000:80
```
Логин: admin
Пароль: (получить командой ниже)
```bash
kubectl get secret -n bookstore bookstore-grafana -o jsonpath="{.data.admin-password}" | base64 --decode
```

Импорт дашбордов:  

4701 — Spring Boot Statistics  
315 — Kubernetes Cluster Monitoring  

### Prometheus

```bash
kubectl port-forward -n bookstore svc/bookstore-kube-prometheus-prometheus 9090:9090
```

Запросы:

```text
application_started_time_seconds
rate(http_server_requests_seconds_count[1m])
```

### Loki (логи)

В Grafana → Explore → источник Loki.  
Запрос:

logql
{namespace="bookstore", app="bookstore-app"}


## Важные команды

### Kind

```bash
kind create cluster --config=kind-config.yaml
kind delete cluster --name bookstore-cluster
kind load docker-image bookstore-app:latest --name bookstore-cluster
kind get clusters
```

### Kubectl

```bash
kubectl get nodes
kubectl get pods -n bookstore
kubectl get svc -n bookstore
kubectl get ingress -n bookstore
kubectl get pvc -n bookstore
kubectl logs -n bookstore -l app=bookstore-app -f
kubectl describe pod -n bookstore <pod-name>
kubectl rollout restart deployment -n bookstore bookstore-app
kubectl port-forward -n bookstore svc/bookstore-app 8085:8085
kubectl create namespace bookstore
```

### Helm

```bash
helm install bookstore ./bookstore-chart --namespace bookstore --create-namespace
helm upgrade bookstore ./bookstore-chart --namespace bookstore
helm uninstall bookstore --namespace bookstore
helm list -n bookstore
helm dependency build
```

## Команды для отладки

### Проверка логов приложения

```bash
kubectl logs -n bookstore -l app=bookstore-app --tail=50 -f
```

### Проверка, что ServiceMonitor работает

```bash
kubectl get servicemonitor -n bookstore
kubectl port-forward -n bookstore svc/bookstore-kube-prometheus-prometheus 9090:9090
```
Открыть http://localhost:9090/targets

### Проверка Kafka (если добавлена)

```bash
kubectl exec -it -n bookstore kafka-0 -- kafka-topics.sh --list --bootstrap-server localhost:9092
kubectl exec -it -n bookstore kafka-0 -- kafka-console-consumer.sh --topic book-views --bootstrap-server localhost:9092 --from-beginning
```

### Проверка Redis изнутри кластера

```bash
kubectl run -it --rm redis-test --image=redis:7-alpine --restart=Never -n bookstore -- redis-cli -h redis ping
```

### Войти в под

```bash
kubectl exec -it -n bookstore <pod-name> -- /bin/sh
```

## Разработка и доработка

### Добавление нового функционала

1. Изменить код в src/
2. Пересобрать образ:

```bash
mvn clean package
docker build -t rifatg13/bookstore-app:latest .
docker push rifatg13/bookstore-app:latest
kubectl rollout restart deployment -n bookstore bookstore-app
```
3. Обновить чарт:

```bash
helm upgrade bookstore ./bookstore-chart -n bookstore
```

Остановка докер контейнеров
```bash
# Приостановить все ноды кластера
docker pause bookstore-cluster-control-plane bookstore-cluster-worker bookstore-cluster-worker2

# Возобновить работу кластера
docker unpause bookstore-cluster-control-plane bookstore-cluster-worker bookstore-cluster-worker2
```

### Добавление нового микросервиса

1. Создать новый модуль в проекте
2. Написать Dockerfile
3. Добавить манифесты в templates/
4. При необходимости добавить зависимости в Chart.yaml

### Запуск CI/CD (GitLab)

GitLab CI пайплайн еще не настроен (см. .gitlab-ci.yml). 

При пуше в main:
1. Собирается образ
2. Публикуется в Docker Hub
3. Деплоится в кластер через Helm

## Автор

Разработано Рифатом Галлямовым

Telegram: @rifatg13
Email: rifatg13@gmail.com