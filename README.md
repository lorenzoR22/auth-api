# Registro y Login con verificacion por email

API REST para login, autenticación y registro con verificacion con email de usuarios con Spring Security y JWT.

## Caracteristicas
- Registro de usuario con verificación por correo electrónico
- Inicio de sesión de usuario con generación de tokens JWT
- Recuperacion de constraseña
- Flujo de verificación del correo electrónico del usuario (envío de código de verificación, verificación del usuario)
- Obtención de datos de usuario autenticado
---
## Configuración del proyecto (Clonación y ejecución)

1. Clonar el repositorio y entrar a la carpeta:

```bash
git clone https://github.com/lorenzoR22/auth-api.git
cd auth-api
```

2. Configurar las variables de entorno:

Usar archivo .env
```bash
cp .env.example .env
```
Editar .env y completar con tus datos(la db es postgresql):
```bash
JWT_SECRET_KEY=secret_key
JWT_EXPIRATION=3600000

SPRING_DATASOURCE_URL=url
SPRING_DATASOURCE_USERNAME=username
SPRING_DATASOURCE_PASSWORD=password
SPRING_DATASOURCE_DB=database

SPRING_MAIL_USERNAME=email
SPRING_MAIL_PASSWORD=password email
```
3. Levantar la base de datos con Docker:
```bash
docker-compose up -d
```
