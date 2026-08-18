# CompaCultivo — Spring Boot

Plataforma de gestión de ciclos de cultivo compartidos entre dueño y socio, con
acceso controlado por pago. Java 21 + Spring Boot 3.4 + Thymeleaf + HTMX +
Alpine.js + Tailwind (CDN) + Three.js + PostgreSQL.

## Arrancar en local

1. Instala Java 21, Maven y Docker. (Este zip no incluye el wrapper `mvnw` —
   si lo prefieres, genera uno con `mvn wrapper:wrapper` una vez dentro de la
   carpeta, o usa tu `mvn` del sistema como se indica abajo.)
2. `cp .env.example .env` y llena `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`,
   `STRIPE_SECRET_KEY`, `STRIPE_WEBHOOK_SECRET`, `STRIPE_PRICE_ID`,
   `ADMIN_SEED_EMAIL` (el correo de Google con el que quieres entrar como admin).
3. `chmod +x start.sh && ./start.sh`
   — o manualmente: `docker-compose up -d` y luego `mvn spring-boot:run`.
4. Abre `http://localhost:8080`.

La primera vez que arranca, `DataSeeder` crea:
- un usuario **ADMIN** activo con el email de `ADMIN_SEED_EMAIL` (inicia sesión
  con esa cuenta de Google y ya tendrás acceso),
- un usuario de ejemplo en estado `PENDING`,
- el predio **Santa Cecilia** con movimientos reales de tu ciclo mar–jul 2026.

## Credenciales que necesitas conseguir

- **Google OAuth2**: crea credenciales tipo "Web application" en
  [Google Cloud Console](https://console.cloud.google.com/apis/credentials),
  con redirect URI `http://localhost:8080/login/oauth2/code/google`.
- **Stripe**: crea un producto con precio recurrente y copia su `price_id`;
  la `STRIPE_WEBHOOK_SECRET` la obtienes al registrar el endpoint
  `/pago/webhook` en el dashboard de Stripe (o con `stripe listen` en local).

## Qué queda pendiente de tu parte

- **HTTPS real**: la cookie de sesión está configurada `Secure`, así que en
  local por HTTP puro el navegador puede rechazarla. Para probar en local sin
  HTTPS, cambia `server.servlet.session.cookie.secure` a `false` en
  `application.yml` temporalmente (nunca en producción).
- **Migraciones**: `ddl-auto: update` es cómodo para desarrollar, pero antes de
  producción cámbialo por Flyway o Liquibase con migraciones versionadas.
- **Encriptación en reposo de campos sensibles** (si la necesitas más allá de
  TLS + Postgres gestionado) y **rate limiting** en `/login` y `/pago/webhook`
  no vienen incluidos — son piezas de infraestructura que conviene añadir con
  Bucket4j o un proxy (Nginx/Cloudflare) delante de la app, según tu despliegue.
- El hero 3D usa una figura geométrica simple (icosaedro) generada con Three.js,
  no un modelo GLTF descargado — así no depende de ningún CDN de modelos.

## Estructura

```
compacultivo/
├── docker-compose.yml       # Postgres 16
├── pom.xml
├── start.sh                 # docker-compose up + mvnw spring-boot:run
├── .env.example
└── src/main/
    ├── java/com/compacultivo/
    │   ├── CompacultivoApplication.java
    │   ├── DataSeeder.java
    │   ├── config/           # SecurityConfig, StripeConfig, RoleHierarchyConfig
    │   ├── security/         # OAuth2 user service, success handler, access filter
    │   ├── model/            # User, Predio, CostoInversion, CostoSiembra, Nomina, Insumo, ResumenActividad
    │   ├── repository/
    │   ├── service/          # UserService, PredioService, PaymentService
    │   └── controller/       # PageController, DashboardController, AdminController, PagoController
    └── resources/
        ├── templates/        # landing, login, dashboard, pago, admin + fragments/
        ├── static/js/three-hero.js
        └── application.yml
```
