import { createRouter, createWebHistory } from 'vue-router';

import EditView from '@/views/EditView.vue';
import HomeView from '@/views/HomeView.vue';
import JoinView from '@/views/JoinView.vue';
import MapView from '@/views/MapView.vue';
import NotFoundView from '@/views/NotFoundView.vue';
import PresetsView from '@/views/PresetsView.vue';
import ScanView from '@/views/ScanView.vue';
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
            component: ScanView,
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

/**
 * A cookie check that runs when the application is initially loaded from the server.
 * It will automatically unregister if cookies are already accepted to prevent checks on every route change.
 */
const unregisterCookieCheck = router.beforeEach((_to, _from) => {
    const COOKIES_ACCEPTED_KEY = 'cookiesAccepted';
    const cookiesAlreadyAccepted = JSON.parse(
        localStorage.getItem(COOKIES_ACCEPTED_KEY)
    );
    if (cookiesAlreadyAccepted !== true) {
        const cookiesAccepted = confirm(
            [
                'This web app needs to store data in your web browser to function properly. This data includes:',
                '- A unique client ID that helps the backend discern you from others',
                '- Your settings',
                '- Collection configurations you create',
                'Additionally, the following data is stored and shared with other participants via a backend if you parttake in a collection:',
                '- The username you chose when joining the collection',
                '- Your GPS history since you joined',
                '- The collection configuration (if you start one of your collections)',
                'Do you accept this? If you do not accept, you will note be able to use this app.',
            ].join('\n')
        );
        if (cookiesAccepted) {
            localStorage.setItem(COOKIES_ACCEPTED_KEY, JSON.stringify(true));
        } else {
            window.location.href = 'about:blank';
        }
    } else {
        unregisterCookieCheck();
    }
});
