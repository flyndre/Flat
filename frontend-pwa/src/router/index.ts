import { createRouter, createWebHistory } from "vue-router";

import HomeView from "@/views/HomeView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import MapView from "@/views/MapView.vue";
import PresetsView from "@/views/PresetsView.vue";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "home",
            component: HomeView,
        },
        {
            path: "/map",
            name: "map",
            component: MapView,
        },
        {
            path: "/presets",
            name: "presets",
            component: PresetsView,
        },
        {
            path: "/:pathMatch(.*)*",
            name: "404",
            component: NotFoundView,
        },
    ],
});
export default router;
