# Docs

Welcome to the docs of Flat: Fleet Live Area Tracking.

## Structure of Application and Docs

The Flat app family is split into three main parts:

-   [Android Frontend](/docs/frontend-android/README.md)
-   [PWA Frontend](/docs/frontend-pwa/README.md) (Progressive Web App)
-   [Backend](/docs/backend/README.md) (Shared by both frontends)

Likewise, the codebase and therefore the docs are split in the same manner.
Shared documentation is located in the folder this README is located in:

-   [Shared UI Specifications](/docs/ui.md)
-   [Backend API Docs](/docs/api.md)
-   [Git and Code Conventions](/docs/conventions.md)

## Tech Stack

| Component        | Technologies                                  |
| ---------------- | --------------------------------------------- |
| Android Frontend | Jetpack Compose, Kotlin                       |
| PWA Frontend     | TypeScript, Vite, Vue, Tailwind CSS, PrimeVue |
| Backend          | .NET, C#                                      |

## Requirements

Must-have:

-   Map display
-   Divide map into areas
-   Track your own location via GPS (can be started and stopped)
-   Share tracking with other team members via backend
-   Join the team via link
-   User name

Optional:

-   Join via QR code
-   Leaderboard/Statistics
-   Dark Mode
-   Define collection templates

Nice-to-have:

-   Start/stop tracking via notification
-   Profile pictures
-   Team leader can send push messages to participants
-   Suggestions for optimal route distribution
