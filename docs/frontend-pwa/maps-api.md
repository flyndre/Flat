# Google Maps API

For working with map data, the Google Maps JavaScript API is utilized.

## Map Component

As a base, the [vue3-google-map](https://www.npmjs.com/package/vue3-google-map) is used.
It provides a Vue component `GoogleMap` which loads the Maps API.
The component can be included to display a map and receives the `api-key` as a prop.

> [!CAUTION]
> Everytime the `GoogleMap` component is used, it has to be provided the same `api-key` and `libraries` props;
> Otherwise it causes the API to error.
> To achieve this, the constants `GOOGLE_MAPS_API_KEY` and `GOOGLE_MAPS_API_LIBRARIES` are to be imported from `src/data/constants.ts` and passed to the component.

It also provides other props for convenient reactivity such as `center` and `zoom`.
To access the `google.maps.Map` instance, use the `ref` attribute like in the following example.

```html
<script setup lang="ts">
    import { GoogleMap } from "vue3-google-map";
    import {
        GOOGLE_MAPS_API_KEY,
        GOOGLE_MAPS_API_LIBRARIES,
    } from "@/data/constants";

    const apiKey = GOOGLE_MAPS_API_KEY;
    const libraries = GOOGLE_MAPS_API_LIBRARIES;
    const center = ref({ lat: 0, lng: 0 });
    const zoom = ref(15);
    const map = ref<google.maps.Map>(null);
</script>

<template>
    <GoogleMap ref="map" :api-key :libraries :center :zoom />
</template>
```

For more information visit the [documentation](https://vue3-google-map.com/).

## Maps Utils

To centralize shared functionality concerning the Google Maps API, `src/utils/googleMapsUtils.ts` is used.
It provides functions such as:

-   `mapCenterWithDefaults`: use the browser geolocation with fallback
-   `getShapeBounds`: get the bounding box of any `google.maps.Overlay`
-   Functions for working with GeoJSON
