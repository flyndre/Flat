import App from '@/App.vue';
import SettingsPlugin from '@/plugins/SettingsPlugin';
import ThemePlugin from '@/plugins/ThemePlugin';
import router from '@/router';
import '@/style.css';
import PrimeVue from 'primevue/config';
import ToastService from 'primevue/toastservice';
import { createApp } from 'vue';

createApp(App)
    .use(router)
    .use(ToastService)
    .use(SettingsPlugin)
    .use(ThemePlugin)
    .use(PrimeVue, {
        ripple: true,
    })
    .mount('#app');
