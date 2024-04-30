# PWA Frontend

This frontend for the Flat app is implemented using [Vite](https://vitejs.dev/) and [Vue](https://vuejs.org/).
For visuals, [PrimeVue](https://primevue.org/) and [Tailwind CSS](https://tailwindcss.com/) are utilized.

## Further Documentation

-   [Maps API](/docs/frontend-pwa/maps-api.md)
-   [Client Storage](/docs/frontend-pwa/client-storage.md)
-   [Custom Plugins](/docs/frontend-pwa/custom-plugins.md)
-   [Deployment](/docs/frontend-pwa/deploy.md)

## Project Structure

The PWA Frontend is structured like most Vue applications:

-   `public/`
    For global static assets like the icon or a manifest.
-   `src/`
    For project parts that have to be built/imported before deployment.
    -   `assets/`
        For static but imported assets like JSON or images.
    -   `components/`
        For atomic components, like a button.
    -   `layouts/`
        For compositions of components like a nav bar.
    -   `router/`
        For the Vue Router configuration.
    -   `types/`
        For TypeScript type declarations.
    -   `views/`
        For sub-page layouts used in Vue Router.

## Requirements

Must-have:

-   [x] Map display
-   [x] Divide map into areas
-   [x] Track your own location via GPS (can be started and stopped)
-   [ ] Share tracking with other team members via backend
-   [ ] Join the team via link
-   [ ] User name

Optional:

-   [x] Join via QR code
-   [ ] Leaderboard/Statistics
-   [x] Dark Mode
-   [x] Define collection templates
    -   Archieved by enabling collection duplication

Nice-to-have:

-   [ ] Start/stop tracking via notification
-   [ ] Profile pictures
-   [ ] Team leader can send push messages to participants
-   [ ] Suggestions for optimal route distribution
