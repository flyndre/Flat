# PWA Frontend

This frontend for the Flat app is implemented using [Vite](https://vitejs.dev/) and [Vue](https://vuejs.org/).
For visuals, [PrimeVue](https://primevue.org/) and [Tailwind CSS](https://tailwindcss.com/) are utilized.

## Further Documentation

-   [Maps API](/docs/frontend-pwa/maps-api.md)
-   [Client Storage](/docs/frontend-pwa/client-storage.md)
-   [Custom Plugins](/docs/frontend-pwa/custom-plugins.md)
-   [Deployment](/docs/frontend-pwa/deploy.md)

## Project Structure

The PWA Frontend is structured like most Vue applications.
For maximum clarity, the following bullet list assigns each folder inside the project its associated usage.

-   `public/`
    For global static assets like the icon or a manifest.
-   `src/`
    For project parts that have to be built/imported before deployment.
    -   `api/`
        For working with the REST/Websocket APIs
    -   `assets/`
        For static but imported assets like JSON or images.
    -   `components/`
        For atomic components, like a button.
    -   `data/`
        For all data-related files such as client-side storage access or constants.
    -   `layouts/`
        For compositions of components like a nav bar.
    -   `plugins/`
        For custom Vue Plugins.
    -   `router/`
        For the Vue Router configuration.
    -   `service/`
        For async services such as position logging.
    -   `types/`
        For TypeScript type declarations.
    -   `util/`
        For supporting functionality such as conversion functions and alike.
    -   `validation/`
        For validation functions.
    -   `views/`
        For sub-page layouts used in Vue Router.

## Requirements

Must-have:

-   [x] Map display
-   [x] Divide map into areas
-   [x] Track your own location via GPS (can be started and stopped)
-   [x] Share tracking with other team members via backend
    - [x] Start collection (⚠ has backend error)
    - [x] Send/receive tracking logs
    - [x] Assign divisions
    - [x] End collection
-   [x] Join the team via link
    -   [x] Join collection
    -   [x] Leave collection
-   [x] User name

Optional:

-   [x] Join via QR code
-   [x] Leaderboard/Statistics
-   [x] Dark Mode
-   [x] Define collection templates
    -   Achieved by enabling collection duplication

Nice-to-have:

-   [ ] Start/stop tracking via notification
    -    Not implementable for PWA since background workers [have no access to geolocation](https://stackoverflow.com/a/54208989/11793652).
-   [ ] Profile pictures
-   [ ] Team leader can send push messages to participants
-   [ ] Suggestions for optimal route distribution

Unplanned:

-   [x] Import/Export of collection presets
-   [x] Home screen with current or custom location
-   [x] Setting for handedness
-   [x] I18n
    -   English, German, Spanish
-   [x] Help for division drawing
