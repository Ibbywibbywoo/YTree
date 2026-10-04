## Decisions

### Backend
- **In-memory storage** — used a `LinkedHashMap` to store providers rather than a database. Sufficient for this task and keeps setup simple.
- **Status calculated dynamically** — `getStatus()` lives on the `Provider` model and computes status at read time rather than storing it. This means it stays accurate without needing updates.
- **3-month boundary** — a statement older than 3 months is OUTDATED. A statement exactly 3 months old is considered OUTDATED (using `isBefore` with `minusMonths(3)`).
- **Java 21 / Spring Boot 4.1.1** — Java 21 the latest stable Spring Boot.
- **CORS** — configured to allow requests from `localhost:5173` (Vite dev server).

### Frontend
- **Vite + React + TypeScript** — fast setup, type safety throughout.
- **Axios** — for HTTP requests, clean and readable.
- **No external component library** — kept styling inline and minimal to match the brief's design language closely.
- **Status pill component** — extracted as a reusable component with colour coded states matching the brief.
- **Submit validation on both sides** — frontend disables the button, backend also rejects invalid submissions. Never trust the client alone.

## Trade-offs

- **No persistence** — restarting the backend resets all state. A real implementation would use a database (PostgreSQL).
- **No authentication** — assumes a single client as per the brief.
- **No real file handling** — uploads store a filename and today's date only, as per the brief.
- **No loading states** — a production app would show spinners during API calls.
- **No error boundaries** — a production app would handle network failures more gracefully.

## What I'd do with more time

- Add a database (PostgreSQL + Spring Data JPA)
- Add proper loading and error states on the frontend
- Add more comprehensive test coverage (Playwright e2e for the submit flow)
- Add TypeScript strict mode and proper error handling throughout
- Extract frontend components into separate files
- Add animations for status changes to make the UI feel more responsive

# Connect Your Accounts

A full-stack account onboarding screen built with React + TypeScript (frontend) and Java Spring Boot (backend).

## Getting Started

### Backend (Spring Boot)

1. Open the `accounts` folder in IntelliJ
2. Run `AccountsApplication.java`
3. Backend starts on `http://localhost:8080`

### Frontend (React)

1. Open a terminal and navigate to the `frontend` folder:
```bash
cd frontend
npm install
npm run dev
```
2. Frontend starts on `http://localhost:5173`

## Running Tests

### Backend
Run `AccountServiceTest.java` in IntelliJ — right click → Run

### Frontend
```bash
cd frontend
npm run test
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/accounts/providers` | Get client's providers |
| GET | `/api/accounts/available` | Get providers available to add |
| POST | `/api/accounts/providers` | Add a provider |
| DELETE | `/api/accounts/providers/{id}` | Remove a provider |
| POST | `/api/accounts/providers/{id}/upload` | Upload a statement |
| POST | `/api/accounts/submit` | Submit when all providers ready |
