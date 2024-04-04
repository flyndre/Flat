# Project Structure

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
