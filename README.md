# Mentorship Matching Platform (Capstone)

MentorMatch is a role-based mentorship platform that connects mentors and mentees through profile creation, skill-based matching, session booking, and admin moderation.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web, Validation, Data JPA
- H2 file database by default
- MySQL-ready configuration included
- Static frontend with HTML, CSS, and JavaScript

## Project Structure

- `controller` - REST API endpoints
- `service` - business logic and matching algorithm
- `repository` - database access with JPA repositories
- `entity` - database entities
- `dto` - request/response transfer objects
- `exception` - centralized exception handling

## Features Implemented

- Mentor, mentee, and admin login flows
- Profile registration and update
- Skill-based mentor matching with optional preferred skill search
- Mentorship session scheduling and status updates
- Admin profile listing and delete actions
- Input validations
- Global exception handling
- Seed data for quick testing
- Browser frontend for demo and testing

## Run Locally

1. Build:

```bash
mvn clean install
```

2. Start app:

```bash
mvn spring-boot:run
```

3. Base URL:

```text
http://localhost:8080
```

4. Frontend UI:

```text
http://localhost:8080/
```

5. H2 Console:

```text
http://localhost:8080/h2-console
```

JDBC URL: `jdbc:h2:file:./data/mentorshipdb`

6. Health endpoint:

```text
http://localhost:8080/api/health
```

Important: The app uses file-based H2, so newly registered users persist across restarts.

## API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`

### Profiles

- `POST /api/profiles`
- `GET /api/profiles`
- `GET /api/profiles/by-email?email=<email>`
- `GET /api/profiles/{id}`
- `PUT /api/profiles/{id}`
- `DELETE /api/profiles/{id}`

### Matching

- `GET /api/matches/mentee/{menteeId}?limit=3&skill=<preferredSkill>`

### Sessions

- `POST /api/sessions`
- `GET /api/sessions/user/{userId}`
- `PATCH /api/sessions/{sessionId}/status?status=CONFIRMED`

## Frontend Usage

Use the browser UI at `/` to:

- Login as mentor, mentee, or admin
- Register a new user
- Search mentors by preferred skill
- Book a session request
- View mentor or mentee sessions
- Admin can load and delete profiles

## Demo Public Users

- Mentor: `riya.mentor@example.com` / `Mentor@123` / Role: `MENTOR` / ID: `1`
- Mentor: `sneha.mentor@example.com` / `Mentor@123` / Role: `MENTOR` / ID: `2`
- Mentee: `aman.mentee@example.com` / `Mentee@123` / Role: `MENTEE` / ID: `3`
- Admin: `admin@example.com` / `Admin@123` / Role: `ADMIN` / ID: `4`

Default demo data is aligned to consulting mentorship.

New registrations are auto-assigned by the database. No manual ID entry is required.

## Backend Testing Flow (Postman)

1. `GET /api/health`
2. `POST /api/auth/login`
3. `POST /api/auth/register`
4. `GET /api/profiles`
5. `GET /api/matches/mentee/{menteeId}?limit=3&skill=consulting`
6. `POST /api/sessions`
7. `GET /api/sessions/user/{userId}`
8. `PATCH /api/sessions/{sessionId}/status?status=CONFIRMED`

## Sample Payloads

Create Profile:

```json
{
  "fullName": "Aarav Deshmukh",
  "email": "aarav.demo.mentee@example.com",
  "password": "Demo@123",
  "role": "MENTEE",
  "yearsOfExperience": 0,
  "bio": "MBA student preparing for consulting interviews",
  "skills": ["business analysis", "communication"],
  "interests": ["market analysis", "business strategy", "consulting careers", "case interviews"],
  "availableForMentorship": true
}
```

Create Session:

```json
{
  "mentorId": 1,
  "menteeId": 3,
  "topic": "Resume and Interview Strategy",
  "scheduledAt": "2026-05-01T11:00:00",
  "durationMinutes": 60
}
```

## Demo Video

<a href="demo-video.mp4"><img src="https://img.shields.io/badge/Watch-Final%20Demo-success?style=for-the-badge" alt="Watch Final Demo"></a>

## Notes for Examiner

- The project has proper layered architecture.
- Validation and exception handling are included.
- H2 database is ready by default and MySQL can be configured.
- Postman collection is available in the `postman` folder.
