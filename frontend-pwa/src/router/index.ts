import { createRouter, createWebHistory } from "vue-router";

import HomeView from "@/views/HomeView.vue";
import NotFoundView from "@/views/NotFoundView.vue";
import MapView from "@/views/MapView.vue";
import PresetsView from "@/views/PresetsView.vue";
import EditView from "@/views/EditView.vue";
import JoinView from "@/views/JoinView.vue";
import AboutView from "@/views/AboutView.vue";
import { parseIntElse as stringParamToInt } from "@/util/parseInt";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: "/",
            name: "home",
            component: HomeView,
        },
        {
            path: "/about",
            name: "about",
            component: AboutView,
        },
        {
            path: "/join",
            name: "scan",
            component: JoinView,
        },
        {
            path: "/join/:id",
            name: "join",
            component: JoinView,
            props: true,
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
            path: "/create",
            name: "create",
            component: EditView,
        },
        {
            path: "/edit/:id",
            name: "edit",
            component: EditView,
            props: (r) => ({
                edit: true,
                id: stringParamToInt(r.params["id"]),
            }),
        },
        {
            path: "/:pathMatch(.*)*",
            name: "404",
            component: NotFoundView,
        },
    ],
});
export default router;
