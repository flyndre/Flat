import vue from '@vitejs/plugin-vue';
import { fileURLToPath } from 'url';
import { defineConfig } from 'vite';
import { VitePWA, VitePWAOptions } from 'vite-plugin-pwa';

const pwaProps: Partial<VitePWAOptions> = {
    registerType: 'prompt',
    includeAssets: [
        'favicon.ico',
        'apple-touch-icon.png',
        'masked-icon.svg',
        'pwa-192x192.png',
    ],
    manifest: {
        name: 'Fleet Live Area Tracking',
        short_name: 'Flat',
        description:
            'The modern cross-platform route tracking app to orchestrate volunteer collection campaigns in your neighborhood.',
        theme_color: '#f8fafc',
        start_url: '/',
        icons: [
            {
                src: 'pwa-192x192.png',
                sizes: '240x240',
                type: 'image/png',
            },
            {
                src: 'pwa-512x512.png',
                sizes: '512x512',
                type: 'image/png',
            },
            {
                src: 'pwa-192x192.png',
                sizes: '240x240',
                type: 'image/png',
                purpose: 'any',
            },
        ],
    },
    workbox: {
        maximumFileSizeToCacheInBytes: 2097152 * 10
    }
};

// https://vitejs.dev/config/
export default defineConfig({
    base: '/',
    plugins: [vue(), VitePWA(pwaProps)],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
        },
    },
    define: {
        'import.meta.env.BUILD_DATE': JSON.stringify(new Date().toISOString()),
    },
});
