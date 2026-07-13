# Dantal API — Backend

Enterprise Dental Clinic Management Platform backend (Spring Boot).

## Requirements

- Java 17+ (Java 21 recommended when available)
- Maven 3.9+
- PostgreSQL 16+

## Quick Start

```bash
# Start PostgreSQL
docker compose up -d

# Run API (dev profile)
./mvnw spring-boot:run

# Health check
curl http://localhost:8080/v1/health
```

## Docker

Build and run the API with PostgreSQL:

```bash
docker compose up -d
docker build -t dantal-api .
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=dantal \
  -e DB_USER=dantal \
  -e DB_PASSWORD=dantal \
  -e JWT_SECRET=change-me-to-a-long-random-secret-at-least-32-chars \
  dantal-api
```

## Swagger

- UI: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs

## Profiles

| Profile | Purpose |
|---------|---------|
| `dev` | Local development (PostgreSQL on localhost); password-reset emails logged |
| `prod` | Production (env vars required) |
| `test` | Automated tests (H2 in-memory) |

## Environment Variables (Production)

| Variable | Description |
|----------|-------------|
| `JWT_SECRET` | Min 256-bit secret for HMAC-SHA256 |
| `DB_HOST` | PostgreSQL host |
| `DB_PORT` | PostgreSQL port |
| `DB_NAME` | Database name |
| `DB_USER` | Database user |
| `DB_PASSWORD` | Database password |

## Frontend Integration

The Next.js frontend (`cloud.ts`, `tasks-api.ts`) expects:

- Base URL: `http://localhost:8080`
- API prefix: `/v1`
- Auth response: `{ "tokens": { "access": "...", "refresh": "..." } }`

Dev license key: `DANTAL-DEV-CLINIC`

## Super Admin (Platform Dashboard)

In `dev` profile, a platform admin account is seeded automatically:

| Field | Value |
|-------|-------|
| Email | `superadmin@dantal.local` |
| Password | `SuperAdmin123!` |

1. Set `NEXT_PUBLIC_API_URL=http://localhost:8080` in the frontend `.env.local`
2. Start PostgreSQL + API (`docker compose up -d` then `./mvnw spring-boot:run`)
3. Login at `/login` with the super-admin credentials
4. You are redirected to `/platform/clinics` where you can:
   - List all clinics
   - Create a clinic with manager email + password
   - Optionally activate a license key at creation
   - Enable/disable clinics

The clinic manager logs in with the credentials you provide, selects the **owner** role, and adds staff from the **الكادر** page.

### Platform API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/platform/clinics` | List clinics (SUPER_ADMIN) |
| POST | `/v1/platform/clinics` | Create clinic + manager (SUPER_ADMIN) |
| PATCH | `/v1/platform/clinics/{id}` | Update clinic status (SUPER_ADMIN) |

## API Endpoints

All authenticated endpoints require `Authorization: Bearer <access_token>` unless noted.

### Health

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/health` | Service health check |

### Authentication

| Method | Path | Description |
|--------|------|-------------|
| POST | `/v1/auth/register` | Register clinic and owner |
| POST | `/v1/auth/login` | Login and issue tokens |
| POST | `/v1/auth/refresh` | Rotate refresh token |
| POST | `/v1/auth/logout` | Revoke refresh token |
| POST | `/v1/auth/forgot-password` | Request password reset |
| POST | `/v1/auth/reset-password` | Reset password with token |
| GET | `/v1/auth/me` | Current user profile |
| POST | `/v1/auth/change-password` | Change password |

### License

| Method | Path | Description |
|--------|------|-------------|
| POST | `/v1/license/activate` | Activate clinic license key |

### Cloud Sync

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/sync/pull?sinceVersion=` | Pull encrypted sync blob |
| POST | `/v1/sync/push` | Push encrypted sync blob |
| POST | `/v1/backups` | Create encrypted backup |

### Tasks

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/tasks` | List tasks |
| POST | `/v1/tasks` | Create task |
| PATCH | `/v1/tasks/{id}` | Update task |
| DELETE | `/v1/tasks/{id}` | Delete task |

### Clinic Data

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/clinic/settings` | Get clinic meta settings |
| PUT | `/v1/clinic/settings` | Replace clinic meta settings |
| GET | `/v1/clinic/export` | Export full DentistDb JSON |
| POST | `/v1/clinic/import` | Import full DentistDb JSON |

### Clinical Resources

Each resource supports the same CRUD pattern:

| Method | Path | Description |
|--------|------|-------------|
| GET | `/v1/{resource}` | List all items |
| GET | `/v1/{resource}/{id}` | Get one item |
| POST | `/v1/{resource}` | Create item |
| PUT | `/v1/{resource}/{id}` | Update item |
| DELETE | `/v1/{resource}/{id}` | Delete item |
| POST | `/v1/{resource}/bulk` | Replace entire collection |

| Resource | Base path | List key | Item key |
|----------|-----------|----------|----------|
| Patients | `/v1/patients` | `patients` | `patient` |
| Appointments | `/v1/appointments` | `appointments` | `appointment` |
| Staff | `/v1/staff` | `staff` | `staffMember` |
| Treatment plans | `/v1/plans` | `plans` | `plan` |
| Prescriptions | `/v1/prescriptions` | `prescriptions` | `prescription` |
| Payments | `/v1/payments` | `payments` | `payment` |
| Expenses | `/v1/expenses` | `expenses` | `expense` |
| Vendors | `/v1/vendors` | `vendors` | `vendor` |
| Inventory | `/v1/inventory` | `inventory` | `item` |
| Services | `/v1/services` | `services` | `service` |
| Cases | `/v1/cases` | `cases` | `case` |
| Reminders | `/v1/reminders` | `reminders` | `reminder` |
| Voice notes | `/v1/voice-notes` | `voiceNotes` | `voiceNote` |
| Audit log | `/v1/audit-log` | `auditLog` | `entry` |
| Settlements | `/v1/settlements` | `settlements` | `settlement` |
| Archives | `/v1/archives` | `archives` | `archive` |
| Referrals | `/v1/referrals` | `referrals` | `referral` |

### Actuator

| Method | Path | Description |
|--------|------|-------------|
| GET | `/actuator/health` | Spring Boot health |
| GET | `/actuator/info` | Application info |

## Security

Clinical endpoints require an authenticated user with one of: `SUPER_ADMIN`, `ADMIN`, `DOCTOR`, `DENTIST`, `RECEPTIONIST`, `ASSISTANT`, or `ACCOUNTANT`.
