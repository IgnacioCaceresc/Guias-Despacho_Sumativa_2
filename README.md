# Gestion de Guias de Despacho

Backend Spring Boot para la actividad sumativa de Desarrollo Cloud Native, Semana 6.

## Requisitos cubiertos

- Backend securitizado con Spring Security y JWT Resource Server.
- Integracion preparada para Azure AD B2C mediante `issuer-uri`.
- Rol `DESCARGA_GUIA` para descargar guias.
- Rol `GESTOR_GUIAS` para crear, consultar, actualizar, eliminar y subir guias a S3.
- Endpoints REST para crear, subir a S3, descargar, actualizar, eliminar y consultar por transportista/fecha.
- Persistencia con JPA y H2 para desarrollo local.
- Servicio S3 real configurable y modo simulado para pruebas locales.
- Integracion RabbitMQ con cola principal de guias y cola de errores.
- Endpoint para consumir mensajes de la cola principal y persistirlos en `guias_despacho_procesadas`.
- Dockerfile y `docker-compose.yml`.

## Roles esperados en Azure AD B2C

Crear dos roles o claims equivalentes en el token:

```text
DESCARGA_GUIA
GESTOR_GUIAS
```

La API lee roles desde los claims `roles`, `extension_Roles`, `extension_roles` o `scp`.

## Variables de ambiente

```text
AZURE_B2C_ISSUER_URI=https://<tenant>.b2clogin.com/<tenant>.onmicrosoft.com/<policy>/v2.0/
GUIAS_STORAGE_PATH=/tmp/guias-despacho
S3_ENABLED=false
S3_BUCKET=nombre-del-bucket
AWS_REGION=us-east-1
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
RABBIT_COLA_GUIAS=guias.cola1
RABBIT_COLA_ERRORES=guias.cola2.errores
```

Para usar S3 real, cambiar `S3_ENABLED=true` y entregar credenciales AWS por el mecanismo normal del SDK:
variables de ambiente, rol IAM del EC2 o perfil configurado.

## Ejecutar localmente

```bash
mvn spring-boot:run
```

La API queda en:

```text
http://localhost:8080
```

Health check:

```text
GET http://localhost:8080/actuator/health
```

## Docker

```bash
docker build -t gestion-guias-despacho .
docker run -p 8080:8080 gestion-guias-despacho
```

Con compose:

```bash
docker compose up --build
```

RabbitMQ queda disponible en:

```text
AMQP: amqp://localhost:5672
Panel: http://localhost:15672
Usuario: guest
Password: guest
```

## Endpoints

Todos los endpoints bajo `/api/guias` requieren token Bearer emitido por Azure AD B2C.

### Crear guia

Requiere rol `GESTOR_GUIAS`.
La guia se guarda localmente y se publica en la cola principal `guias.cola1`. Si falla el envio a la cola principal, se intenta enviar el mensaje a `guias.cola2.errores` y la guia queda con estado `ERROR_COLA`.

```bash
curl -X POST http://localhost:8080/api/guias \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>" \
  -H "Content-Type: application/json" \
  -d '{
    "numeroPedido": "PED-1001",
    "transportista": "Transportes Norte",
    "fechaDespacho": "2026-06-28",
    "destinatario": "Cliente Demo",
    "direccionDestino": "Av. Siempre Viva 123",
    "comunaDestino": "Santiago",
    "ciudadDestino": "Santiago",
    "pesoKg": 12.5,
    "cantidadBultos": 3,
    "observaciones": "Entrega en horario de oficina"
  }'
```

### Consultar por transportista y fecha

Requiere rol `GESTOR_GUIAS`.

```bash
curl "http://localhost:8080/api/guias?transportista=Transportes%20Norte&fecha=2026-06-28" \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>"
```

### Actualizar guia

Requiere rol `GESTOR_GUIAS`.
Tambien publica un mensaje en la cola principal con operacion `ACTUALIZACION`.

```bash
curl -X PUT http://localhost:8080/api/guias/1 \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>" \
  -H "Content-Type: application/json" \
  -d '{
    "numeroPedido": "PED-1001",
    "transportista": "Transportes Norte",
    "fechaDespacho": "2026-06-29",
    "destinatario": "Cliente Demo Actualizado",
    "direccionDestino": "Los Alerces 456",
    "comunaDestino": "Providencia",
    "ciudadDestino": "Santiago",
    "pesoKg": 13.0,
    "cantidadBultos": 4,
    "observaciones": "Cambio de direccion"
  }'
```

### Subir guia a S3

Requiere rol `GESTOR_GUIAS`.

```bash
curl -X POST http://localhost:8080/api/guias/1/subir-s3 \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>"
```

### Descargar guia

Requiere rol `DESCARGA_GUIA`.

```bash
curl -O -J http://localhost:8080/api/guias/1/descargar \
  -H "Authorization: Bearer <TOKEN_DESCARGA_GUIA>"
```

### Eliminar guia

Requiere rol `GESTOR_GUIAS`.

```bash
curl -X DELETE http://localhost:8080/api/guias/1 \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>"
```

### Consumir cola RabbitMQ

Requiere rol `GESTOR_GUIAS`.
Consume mensajes desde `guias.cola1` y los guarda en la tabla `guias_despacho_procesadas`.

```bash
curl -X POST "http://localhost:8080/api/guias/cola/consumir?cantidad=10" \
  -H "Authorization: Bearer <TOKEN_GESTOR_GUIAS>"
```

## Configuracion sugerida en API Gateway

Registrar rutas apuntando al DNS publico del EC2 o balanceador:

```text
POST   /api/guias
GET    /api/guias
PUT    /api/guias/{id}
DELETE /api/guias/{id}
POST   /api/guias/{id}/subir-s3
GET    /api/guias/{id}/descargar
POST   /api/guias/cola/consumir
```

El authorizer del API Gateway debe validar el JWT contra Azure AD B2C. La validacion por rol queda duplicada en Spring Security para que el backend tambien este protegido.
