# Spring Cloud Alibaba + Mailexam

Minimal [Spring Cloud Alibaba](https://sca.aliyun.com/) microservice that sends test mail through [Mailexam](https://mailexam.io/) SMTP via `spring-boot-starter-mail` and `JavaMailSender`.

Based on the [Mailexam Spring Cloud Alibaba guide](https://wiki.mailexam.ru/en/examples/spring-cloud-alibaba/).

## What you need

- A Mailexam account and a project with SMTP credentials.
- JDK 17+.

From your Mailexam welcome email or dashboard:

| Variable | Description |
|----------|-------------|
| `MAILEXAM_LOGIN` | SMTP login (for example, `xxxxx`) |
| `MAILEXAM_PASSWORD` | SMTP password (paired with the login) |
| Host | `{MAILEXAM_LOGIN}.mailexam.io` (used in `application.yml`) |

Nacos service discovery is included but **disabled by default** (`NACOS_DISCOVERY_ENABLED=false`) so you can run the example without a Nacos server.

## Quick start (host)

1. Export environment variables (or copy from `.env.example` into your shell):

```bash
cp .env.example .env
# edit .env, then:
set -a && source .env && set +a
```

2. Run the application:

```bash
./gradlew bootRun
```

The server listens on `http://127.0.0.1:8080` by default.

3. Send a test message:

```bash
curl -X POST http://127.0.0.1:8080/mail/test \
  -H 'Content-Type: application/json' \
  -d '{"to":"user@example.test","subject":"Test","body":"Hello"}'
```

The message appears in the Mailexam dashboard → your project → inbox.

## Environment variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `MAILEXAM_LOGIN` | yes | — | SMTP login; host is `{login}.mailexam.io` |
| `MAILEXAM_PASSWORD` | yes | — | SMTP password |
| `MAILEXAM_PORT` | no | `587` | SMTP port (`587`, `2525`, or `25`) |
| `MAIL_FROM` | no | `noreply@example.test` | Sender address |
| `NACOS_DISCOVERY_ENABLED` | no | `false` | Register the service in Nacos when `true` |
| `NACOS_SERVER_ADDR` | no | `127.0.0.1:8848` | Nacos server address |
| `HTTP_HOST` | no | `127.0.0.1` | HTTP bind address |
| `HTTP_PORT` | no | `8080` | HTTP listen port |

For port **587** STARTTLS is enabled via `mail.smtp.starttls.enable=true`. For port **25** set `spring.mail.properties.mail.smtp.starttls.enable=false`.

## Project layout

```
.
├── build.gradle
├── src/main/java/com/example/demo/
│   ├── MailexamSpringCloudAlibabaApplication.java
│   ├── mail/MailService.java
│   └── web/MailController.java
├── src/main/resources/application.yml
├── .env.example
├── Dockerfile         # for local debugging only
└── docker-compose.yml
```

## Docker (debugging)

Docker is provided for local debugging. For day-to-day development, run the app on the host with `./gradlew bootRun` (see above).

```bash
cp .env.example .env
# edit .env with your credentials

docker compose up --build
```

Then call the same endpoint on the mapped port:

```bash
curl -X POST http://127.0.0.1:8080/mail/test \
  -H 'Content-Type: application/json' \
  -d '{"to":"user@example.test","subject":"Test","body":"Hello"}'
```

Inside the container the server binds to `0.0.0.0:8080` so the port mapping works.

## CI

Set these secrets in your CI environment:

```yaml
variables:
  MAILEXAM_LOGIN: $MAILEXAM_LOGIN
  MAILEXAM_PASSWORD: $MAILEXAM_PASSWORD
  MAILEXAM_PORT: "587"
  MAIL_FROM: "noreply@example.test"
  NACOS_DISCOVERY_ENABLED: "false"
```

After sending a message in a test, verify delivery via the [Mailexam API](https://mailexam.io/api).

## Troubleshooting

**TLS or connection error**

- `spring.mail.host` must be `{login}.mailexam.io`, where `{login}` matches `MAILEXAM_LOGIN`.
- Login and password must come from the same Mailexam project.

**Nacos connection errors on startup**

- Keep `NACOS_DISCOVERY_ENABLED=false` for local SMTP testing without Nacos.
- When enabling discovery, ensure Nacos is reachable at `NACOS_SERVER_ADDR`.

**Variables not substituted**

- Export `MAILEXAM_LOGIN` and `MAILEXAM_PASSWORD` before `./gradlew bootRun`, or set them in your IDE Run configuration.

**Message not in the dashboard**

- Open the inbox of the same Mailexam project.
- Enable logging: `logging.level.org.springframework.mail=DEBUG`.

## See also

- [Mailexam Spring Cloud Alibaba guide (wiki)](https://wiki.mailexam.ru/en/examples/spring-cloud-alibaba/)
- [Spring Boot reference implementation](https://github.com/mailexam/Spring-Boot) — same SMTP setup without Spring Cloud
- [Ktor reference implementation](https://github.com/mailexam/Ktor) — another JVM framework with Jakarta Mail
- [Spring Cloud Alibaba documentation](https://sca.aliyun.com/)
- [Mailexam API documentation](https://mailexam.io/api)
