# BankingApp

Демо банковского сервиса: счета, переводы, реквизиты, вход по JWT и курсы валют ЦБ. Актуальный код — в ветке `dev`.

## Стек

Java 17, Spring Boot 3.2, PostgreSQL, Liquibase, Kafka, Redis, Elasticsearch, Logstash, Kibana. Модуль курсов работает на WebFlux и R2DBC.

## Запуск

Инфраструктура основного приложения:

```bash
cd main-app
docker compose up -d
```

Для Compose и приложения нужны переменные `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `JWT_SECRET`, `KAFKA_SSL_KEYSTORE_PASSWORD`, `KAFKA_SSL_TRUSTSTORE_PASSWORD`, `KAFKA_SSL_KEY_PASSWORD`.

```bash
./mvnw -pl main-app spring-boot:run
```

Приложение слушает порт `9002`. Swagger: [http://localhost:9002/swagger-ui/index.html](http://localhost:9002/swagger-ui/index.html).

Сиды: логин `ivan` или `petr`, пароль `password`.

Модуль курсов (`currency-module`, порт `8081`) поднимает отдельную базу:

```bash
cd currency-module
docker compose up -d
./mvnw -pl currency-module spring-boot:run
```

Для него дополнительно нужны `DB_URL_R2DBC`, `DB_URL_JDBC` и `CBR_URL`.
