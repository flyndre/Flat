import App from "@/App.vue";
import "@/assets/themes/dark.css";
import router from "@/router";
import "@/style.css";
import "@mdi/font/css/materialdesignicons.min.css";
import PrimeVue from "primevue/config";
import { createApp } from "vue";

createApp(App)
    .use(router)
    .use(PrimeVue, {
        ripple: true,
    })
    .mount("#app");
