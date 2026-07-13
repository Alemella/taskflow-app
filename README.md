# taskflow-app

TaskFlow es un proyecto personal para portafolio con backend en Spring Boot y autenticación JWT.

## Backend

- Java 21
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL en local
- H2 para pruebas

## Variables de entorno

Configura estas variables antes de levantar el backend en tu máquina:

- `TASKFLOW_DB_URL`
- `TASKFLOW_DB_USERNAME`
- `TASKFLOW_DB_PASSWORD`
- `TASKFLOW_JPA_DDL_AUTO`
- `TASKFLOW_JPA_SHOW_SQL`
- `SERVER_PORT`

Ejemplo local:

```powershell
$env:TASKFLOW_DB_URL="jdbc:postgresql://localhost:5432/taskflow_db"
$env:TASKFLOW_DB_USERNAME="postgres"
$env:TASKFLOW_DB_PASSWORD="tu_password"
$env:TASKFLOW_JPA_DDL_AUTO="update"
$env:TASKFLOW_JPA_SHOW_SQL="true"
$env:SERVER_PORT="8080"
```

## Ejecutar backend

```powershell
cd backend
./mvnw.cmd spring-boot:run
```

## Validación

```powershell
cd backend
./mvnw.cmd test
```

## Endpoints principales

- `POST /auth/register`
- `POST /auth/login`
- `GET /tasks`
- `POST /tasks`
- `GET /tasks/{id}`
- `PUT /tasks/{id}`
- `DELETE /tasks/{id}`
- `PATCH /tasks/{id}/complete`
- `GET /users`
- `POST /users`

## Notas

- Los endpoints de tareas requieren un JWT válido.
- Los endpoints de usuarios requieren rol `ADMIN`.
- Las contraseñas nunca se devuelven en las respuestas JSON.
