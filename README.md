# 🛠️ Запуск на Windows

## 📌 Требования

- Java 17
- Docker & Docker Compose
- Git
- Maven (опционально — можно использовать wrapper)

---

## ✅ 1. Установка Java 17

1. Скачайте Temurin 17 для Windows по [ссылке](https://adoptium.net/download/)
2. Убедитесь, что существует `JAVA_HOME`:
    - Win + S → «Система» → «Доп. параметры системы» → «Переменные среды»
    - Должна быть переменная `JAVA_HOME`, путь вида:
      `C:\Program Files\Eclipse Adoptium\jdk-17.x.x_x`
    - `Path` должен содержать: `%JAVA_HOME%\bin`

3. Проверьте установку:
    ```bash
    java -version
    ```

---

## ✅ 2. Установка Docker

1. Скачайте Docker Desktop по [ссылке](https://www.docker.com/products/docker-desktop)
2. Установите и перезагрузитесь
3. Проверьте:
    ```bash
    docker --version
    ```

---

## ✅ 3. Клонирование проекта

```bash
git clone https://github.com/your-username/your-repo-name.git
cd your-repo-name
```


---

## ✅ 4. Запуск зависимостей (БД и Redis)

```bash
cd docker
docker-compose up -d
cd ..
```

Проверьте, что контейнеры запущены:

```bash
docker ps
```

---

## ✅ 5. Запуск приложения

**С Maven:**

```bash
mvn install
mvn spring-boot:run
```

**Или через wrapper:**

```bash
./mvnw install
./mvnw spring-boot:run
```


**Откройте в браузере:**

[http://localhost:8080](http://localhost:8080)