import { createRouter, createWebHistory } from 'vue-router';

import EditView from '@/views/EditView.vue';
import HomeView from '@/views/HomeView.vue';
import JoinView from '@/views/JoinView.vue';
import MapView from '@/views/MapView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import PresetsView from '@/views/PresetsView.vue';
import SettingsView from '@/views/SettingsView.vue';
import TrackingView from '@/views/TrackingView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
        },
        {
            path: '/settings',
            name: 'settings',
            component: SettingsView,
        },
        {
            path: '/join',
            name: 'scan',
            component: JoinView,
        },
        {
            path: '/join/:id',
            name: 'join',
            component: JoinView,
            props: true,
        },
        {
            path: '/presets',
            name: 'presets',
            component: PresetsView,
        },
        {
            path: '/create',
            name: 'create',
            component: EditView,
        },
        {
            path: '/create/map',
            name: 'create-map',
            component: MapView,
        },
        {
            path: '/edit/:id',
            name: 'edit',
            component: EditView,
            props: (r) => ({
                edit: true,
                id: <string>r.params['id'],
            }),
        },
        {
            path: '/edit/:id/map',
            name: 'edit-map',
            component: MapView,
            props: (r) => ({
                edit: true,
                id: <string>r.params['id'],
            }),
        },
        {
            path: '/track',
            name: 'track',
            component: TrackingView,
        },
        {
            path: '/:pathMatch(.*)*',
            name: '404',
            component: NotFoundView,
        },
    ],
});
export default router;
