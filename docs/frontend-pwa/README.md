# PWA Frontend

This frontend for the Flat app is implemented using Vite and Vue.
For visuals, PrimeVue and Tailwind CSS are utilized.

## Further Documentation

* [Project Structure](/docs/frontend-pwa/project-structure.md)
* [Maps API](/docs/frontend-pwa/maps-api.md)
* [Client Storage](/docs/frontend-pwa/client-storage.md)
* [Custom Plugins](/docs/frontend-pwa/custom-plugins.md)

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

- [x]   Map display
- [x]   Divide map into areas
- [ ]   Track your own location via GPS (can be started and stopped)
- [ ]   Share tracking with other team members via backend
- [ ]   Join the team via link
- [ ]   User name

Optional:

- [ ]   Join via QR code
- [ ]   Leaderboard/Statistics
- [x]   Dark Mode
- [ ]   Define collection templates

Nice-to-have:

- [ ]   Start/stop tracking via notification
- [ ]   Profile pictures
- [ ]   Team leader can send push messages to participants
- [ ]   Suggestions for optimal route distribution
