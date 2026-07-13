# TaskFlow Android Front Summary

## Objetivo
Crear la app Android de TaskFlow en Kotlin usando Jetpack Compose y conectarla al backend Spring Boot ya listo.

## Stack recomendado
- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Retrofit + OkHttp
- DataStore para guardar sesión
- MVVM con StateFlow

## Pantallas acordadas
- SplashScreen
- LoginScreen
- RegisterScreen
- TaskListScreen
- TaskDetailScreen
- TaskFormScreen
- ProfileScreen

## Navegación
- Pantallas públicas: login y registro
- Área privada: tareas y perfil
- Recomendación: bottom navigation dentro del área privada

## Sesión
- Guardar JWT localmente
- Opción del usuario: "quiero mantener la sesión iniciada"
- Si está activada, entrar directo a tareas al abrir la app

## Endpoints del backend
- POST /auth/register
- POST /auth/login
- GET /tasks
- POST /tasks
- GET /tasks/{id}
- PUT /tasks/{id}
- DELETE /tasks/{id}
- PATCH /tasks/{id}/complete
- GET /users
- POST /users

## Orden de implementación
1. Crear proyecto Compose en Android Studio
2. Estructura de paquetes
3. Navegación
4. Login y registro
5. Guardar token
6. Lista de tareas
7. Crear/editar/completar/eliminar tareas
8. Perfil y cerrar sesión

## Nota sobre admin
- El backend actual fuerza `role = USER` al crear usuarios.
- Para probar `GET /users`, hay que promover un usuario a `ADMIN` directamente en PostgreSQL y volver a iniciar sesión.
