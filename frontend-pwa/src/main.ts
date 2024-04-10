import App from '@/App.vue';
import '@/assets/themes/dark.css';
import router from '@/router';
import '@/style.css';
import PrimeVue from 'primevue/config';
import ToastService from 'primevue/toastservice';
import { createApp } from 'vue';

createApp(App)
    .use(router)
    .use(PrimeVue, {
        ripple: true,
    })
    .use(ToastService)
    .mount('#app');
