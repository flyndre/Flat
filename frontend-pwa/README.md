# PWA Frontend

## Docs

The documentation for the PWA Frontend is - along all other docs - located [here](/docs/frontend-pwa/README.md).

## Installation

To install the Dependencies, run:

```
pnpm install
```

> [!NOTE]
> To install pnpm, run:
>
> ```
> npm install -g pnpm
> ```
>
> And restart your terminal or IDE afterwards.

## Run

To run the PWA Frontend in dev mode run:

```
pnpm dev
```

> [!NOTE]
> To properly use the Google Maps API, create the file `/frontend-pwa/.env.local` with the following contents:
>
> ```
> VITE_GOOGLE_MAPS_API_KEY=<api key>
> ```

## Build

To build the PWA Frontend run:

```
pnpm build
```
