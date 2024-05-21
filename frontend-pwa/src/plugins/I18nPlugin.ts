import { createI18n } from 'vue-i18n';

const messages = {
    en: {
        prompts: {
            back: 'Back',
            cancel: 'Cancel',
            close: 'Close',
            confirm: 'Yes',
            deny: 'No',
            settings: 'Settings',
            dark_theme: 'Dark',
            light_theme: 'Light',
            device_theme: 'Device',
            persistence_enabled: 'Save settings',
            persistence_disabled: "Don't save settings",
            copy_url_success: 'URL copied!',
            copy_text_success: 'Text copied!',
        },
        locales: {
            en: 'English',
            de: 'German',
        },
    },
    de: {
        prompts: {
            back: 'Zurück',
            cancel: 'Abbrechen',
            close: 'Schließen',
            confirm: 'Ja',
            deny: 'Nein',
            settings: 'Einstellungen',
            dark_theme: 'Dunkel',
            light_theme: 'Hell',
            device_theme: 'Gerät',
            persistence_enabled: 'Einstellungen speichern',
            persistence_disabled: 'Einstellungen nicht speichern',
            copy_url_success: 'URL kopiert!',
            copy_text_success: 'Text kopiert!',
        },
        locales: {
            en: 'Englisch',
            de: 'Deutsch',
        },
    },
};

const availableLocales = Object.keys(messages).map((l) => l.toLowerCase());
const locale = navigator.languages.find((l) =>
    availableLocales.includes(l.toLowerCase())
);
const fallbackLocale = 'en';

const I18nPlugin = createI18n({
    availableLocales,
    fallbackLocale,
    legacy: false,
    locale,
    messages,
});
export default I18nPlugin;
